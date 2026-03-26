package ca.hccis.perfumeshop

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bo.PerfumeTransactionBO
import entity.PerfumeTransaction
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val customColorScheme = lightColorScheme(
                primary = Color(0xFFB8860B),
                secondary = Color(0xFF00008B),
                error = Color(0xFF8B0000)
            )

            MaterialTheme(colorScheme = customColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        MainScreen()
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Perfume Shop", color = Color(0xFFD4AF37),
            fontSize = 40.sp, fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    // SPRINT 4: Snackbar State
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    BatteryWarningReceiver(context = context)

    // WORK ITEM: Shared Preferences (Saves the Cashier's Name)
    val sharedPreferences = remember { context.getSharedPreferences("PerfumeShopPrefs", Context.MODE_PRIVATE) }
    var cashierNameInput by remember { mutableStateOf(sharedPreferences.getString("CASHIER_NAME", "") ?: "") }

    // --- STATE VARIABLES (Renamed to prevent Scope Collisions) ---
    var dateInput by remember { mutableStateOf("") }
    var customerNameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var quantityInput by remember { mutableStateOf("") }

    var showCamera by remember { mutableStateOf(false) }
    val cameraPermissionState = com.google.accompanist.permissions.rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    val perfumeOptions = listOf("Sauvage by Dior", "Bleu de Chanel", "Acqua Di Gio", "YSL La Nuit", "Tom Ford Oud Wood")
    var expanded by remember { mutableStateOf(false) }
    var selectedPerfume by remember { mutableStateOf(perfumeOptions[0]) }

    val sizeOptions = listOf("50ml", "100ml")
    var selectedSize by remember { mutableStateOf(sizeOptions[0]) }

    val transactionsList = remember { mutableStateListOf<PerfumeTransaction>() }
    val coroutineScope = rememberCoroutineScope()

    val db = remember { DatabaseProvider.getDatabase(context) }
    val dao = db.perfumeDao()

    // PAGE LOAD: Two-Way Sync (Offline-First)
    LaunchedEffect(Unit) {
        val localData = dao.getAllLocalTransactions()
        transactionsList.clear()
        transactionsList.addAll(localData)

        try {
            val apiData = RetrofitClient.apiService.getTransactions()
            val unsyncedRecords = localData.filter { localRecord ->
                apiData.none { cloudRecord -> cloudRecord.id == localRecord.id }
            }
            unsyncedRecords.forEach { offlineTransaction ->
                RetrofitClient.apiService.addTransaction(offlineTransaction)
            }
            val finalApiData = RetrofitClient.apiService.getTransactions()
            dao.insertAll(finalApiData)

            val updatedLocalData = dao.getAllLocalTransactions()
            transactionsList.clear()
            transactionsList.addAll(updatedLocalData)
        } catch (e: Exception) {
            Toast.makeText(context, "Offline Mode: Showing local data", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // SPRINT 4: Promo Carousel
        PromoCarousel()
        Spacer(modifier = Modifier.height(8.dp))

        // SPRINT 4: Cashier Name Box
        OutlinedTextField(
            value = cashierNameInput,
            onValueChange = { newName ->
                cashierNameInput = newName
                sharedPreferences.edit().putString("CASHIER_NAME", newName).apply()
            },
            label = { Text("Cashier Name (Auto-Saves)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            singleLine = true
        )

        Text("New Perfume Sale", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = dateInput, onValueChange = { dateInput = it },
            label = { Text("Date (yyyy-mm-dd)") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = customerNameInput, onValueChange = { customerNameInput = it },
            label = { Text("Customer Name") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phoneInput, onValueChange = { phoneInput = it },
            label = { Text("Phone Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedPerfume, onValueChange = {}, readOnly = true,
                label = { Text("Perfume Choice") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                perfumeOptions.forEach { selectionOption ->
                    DropdownMenuItem(text = { Text(selectionOption) }, onClick = {
                        selectedPerfume = selectionOption
                        expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Bottle Size:", modifier = Modifier.align(Alignment.Start))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            sizeOptions.forEach { text ->
                RadioButton(selected = (text == selectedSize), onClick = { selectedSize = text })
                Text(text = text, modifier = Modifier.padding(end = 16.dp))
            }
        }

        OutlinedTextField(
            value = priceInput, onValueChange = { priceInput = it },
            label = { Text("Price Per Bottle ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = quantityInput, onValueChange = { quantityInput = it },
            label = { Text("Quantity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!cameraPermissionState.status.isGranted) {
                    cameraPermissionState.launchPermissionRequest()
                } else {
                    showCamera = true
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text("📷 Scan Item Barcode")
        }

        // SAVE BUTTON
        Button(
            onClick = {
                // 📍 SPRINT 4: Form Validation & Snackbar!
                if (dateInput.isBlank() || customerNameInput.isBlank() || phoneInput.isBlank() || priceInput.isBlank() || quantityInput.isBlank()) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("⚠️ Please fill out all fields before saving!")
                    }
                    return@Button // This instantly stops the save process!
                }
                try {
                    val transaction = PerfumeTransaction().apply {
                        transactionDate = dateInput
                        customerName = customerNameInput
                        phoneNumber = phoneInput
                        perfumeChoice = selectedPerfume
                        perfumeSize = selectedSize
                        pricePerBottle = priceInput.toIntOrNull() ?: 0
                        quantity = quantityInput.toIntOrNull() ?: 0
                        cashierName = cashierNameInput
                    }

                    PerfumeTransactionBO.calculateTotals(transaction)

                    coroutineScope.launch {
                        try {
                            dao.insertTransaction(transaction)

                            val updatedData = dao.getAllLocalTransactions()
                            transactionsList.clear()
                            transactionsList.addAll(updatedData)

                            // Clear Form
                            customerNameInput = ""
                            phoneInput = ""
                            priceInput = ""
                            quantityInput = ""
                            selectedPerfume = perfumeOptions[0]
                            selectedSize = sizeOptions[0]

                            // 📍 SPRINT 4: START THE AUDIO SERVICE!
                            context.startService(Intent(context, AudioService::class.java))

                            try {
                                RetrofitClient.apiService.addTransaction(transaction)
                                Toast.makeText(context, "Saved & Synced to Cloud!", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Offline: Saved locally, will sync later.", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Database Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error saving", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate & Save")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Previous Transactions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        transactionsList.forEach { t ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ID: ${t.id} - ${t.customerName}", fontWeight = FontWeight.Bold)
                        Text("${t.perfumeChoice} (${t.perfumeSize})")
                        Text(
                            "Total: $${String.format("%.2f", t.total)}",
                            color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cashier: ${t.cashierName.ifEmpty { "Unknown" }}",
                            style = MaterialTheme.typography.bodyMedium, color = Color.Gray
                        )
                    }

                    IconButton(onClick = {
                        val receiptText = """
                            🌸 Perfume Shop Receipt 🌸
                            Customer: ${t.customerName}
                            Item: ${t.perfumeChoice} (${t.perfumeSize})
                            Quantity: ${t.quantity}
                            Total Paid: $${String.format("%.2f", t.total)}
                            Cashier: ${t.cashierName}
                            Thank you for your purchase!
                        """.trimIndent()

                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, receiptText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Receipt Via..."))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
    // SPRINT 4: Draw the Snackbar at the bottom of the screen
    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showCamera) {
        CameraScannerScreen(onBarcodeScanned = { scannedNumber ->
            showCamera = false
            when (scannedNumber) {
                "3145891073607" -> {
                    selectedPerfume = perfumeOptions[1]
                    Toast.makeText(context, "Scanned: ${perfumeOptions[1]}", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Item not in database. Scanned ID: $scannedNumber", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}

@Composable
fun BatteryWarningReceiver(context: Context) {
    DisposableEffect(context) {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_LOW) {
                    Toast.makeText(context, "⚠️ CRITICAL: Battery Low!", Toast.LENGTH_LONG).show()
                }
            }
        }
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_LOW))
        onDispose { context.unregisterReceiver(batteryReceiver) }
    }
}

@Composable
fun PromoCarousel() {
    val promos = listOf("🌟 Holiday Sale: 20% Off Dior", "🔥 Top Seller: Chanel No. 5", "🎁 Free Gift with $100 Purchase", "✨ New Arrival: Tom Ford Vanilla")
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = "Today's Promotions", style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            promos.forEach { promoText ->
                Card(
                    modifier = Modifier.width(200.dp).height(100.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = promoText, style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
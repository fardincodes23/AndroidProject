package ca.hccis.perfumeshop

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bo.PerfumeTransactionBO
import entity.PerfumeTransaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.material.icons.filled.Share
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Our custom Material Design Color Scheme
            val customColorScheme = lightColorScheme(
                primary = Color(0xFFB8860B), // Dark Goldenrod
                secondary = Color(0xFF00008B), // Dark Blue
                error = Color(0xFF8B0000) // Dark Red
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
    // This simple state handles our Splash Screen logic!
    var showSplash by remember { mutableStateOf(true) }

    // Coroutine to hide splash after 2 seconds
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
            .background(Color(0xFF1E1E1E)), // Dark background
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Perfume Shop", color = Color(0xFFD4AF37), // Gold text
            fontSize = 40.sp, fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current

    // NEW: Activate our System Broadcast Listener
    BatteryWarningReceiver(context = context)

    // --- STATE VARIABLES (These replace findViewById) ---
    var date by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("") }

    // Tracks if the camera is open or closed
    var showCamera by remember { mutableStateOf(false) }

    // Asks the user for Camera Permission
    val cameraPermissionState = com.google.accompanist.permissions.rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    // Dropdown State
    val perfumeOptions = listOf(
        "Sauvage by Dior", "Bleu de Chanel", "Acqua Di Gio", "YSL La Nuit", "Tom Ford Oud Wood"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedPerfume by remember { mutableStateOf(perfumeOptions[0]) }

    // Radio Button State
    val sizeOptions = listOf("50ml", "100ml")
    var selectedSize by remember { mutableStateOf(sizeOptions[0]) }

    // Ledger State
    val transactionsList = remember { mutableStateListOf<PerfumeTransaction>() }
    val coroutineScope = rememberCoroutineScope() // Allows us to run background internet tasks

    // Connect to our local Room Database
    val db = remember { DatabaseProvider.getDatabase(context) }
    val dao = db.perfumeDao()

    // PAGE LOAD: Offline-First Strategy
    LaunchedEffect(Unit) {
        // 1. Immediately load whatever is on the phone so the user isn't staring at a blank screen
        val localData = dao.getAllLocalTransactions()
        transactionsList.clear()
        transactionsList.addAll(localData)

        // 2. Try to fetch from the internet to see if there is anything new from the cloud
        try {
            val apiData = RetrofitClient.apiService.getTransactions()

            // Save the new internet data into our local phone database to keep them synced
            dao.insertAll(apiData)

            // Refresh the screen with the perfectly synced data
            val updatedLocalData = dao.getAllLocalTransactions()
            transactionsList.clear()
            transactionsList.addAll(updatedLocalData)

        } catch (e: Exception) {
            // If the internet is down, do nothing! The user can still see their local data.
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
        Text(
            "New Perfume Sale",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Basic Text Fields
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (yyyy-mm-dd)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Customer Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // WIDGET 1: Compose Dropdown (Spinner)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedPerfume,
                onValueChange = {},
                readOnly = true,
                label = { Text("Perfume Choice") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
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

        // WIDGET 2: Radio Buttons
        Text("Bottle Size:", modifier = Modifier.align(Alignment.Start))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            sizeOptions.forEach { text ->
                RadioButton(selected = (text == selectedSize), onClick = { selectedSize = text })
                Text(text = text, modifier = Modifier.padding(end = 16.dp))
            }
        }

        // Numbers Fields
        OutlinedTextField(
            value = priceStr,
            onValueChange = { priceStr = it },
            label = { Text("Price Per Bottle ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = quantityStr,
            onValueChange = { quantityStr = it },
            label = { Text("Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                try {

                    val transaction = PerfumeTransaction().apply {
                        //id = transactionsList.size + 1
                        transactionDate = date
                        this.customerName = customerName
                        phoneNumber = phone
                        perfumeChoice = selectedPerfume
                        perfumeSize = selectedSize
                        pricePerBottle = priceStr.toIntOrNull() ?: 0
                        quantity = quantityStr.toIntOrNull() ?: 0
                    }

                    PerfumeTransactionBO.calculateTotals(transaction)
// NEW API LOGIC: Upload to the internet in the background
                    // SAVE LOGIC: Offline-First Strategy
                    coroutineScope.launch {
                        try {
                            // 1. ALWAYS save to the local phone database first
                            dao.insertTransaction(transaction)

                            // 2. Refresh the UI to show the new receipt immediately
                            val updatedData = dao.getAllLocalTransactions()
                            transactionsList.clear()
                            transactionsList.addAll(updatedData)

                            Toast.makeText(context, "Saved Locally!", Toast.LENGTH_SHORT).show()

                            // Clear form for next user
                            customerName = ""
                            phone = ""
                            priceStr = ""
                            quantityStr = ""
                            selectedPerfume = perfumeOptions[0]
                            selectedSize = sizeOptions[0]

                            // 3. NOW, silently try to push it to the Cloud API in the background
                            try {
                                RetrofitClient.apiService.addTransaction(transaction)
                                Toast.makeText(
                                    context, "Saved to Phone AND Cloud!", Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                // CHANGED: Show the ACTUAL error message from the server
                                Toast.makeText(
                                    context, "API Error: ${e.message}", Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Database Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // Clear form for next user
                    customerName = ""
                    phone = ""
                    priceStr = ""
                    quantityStr = ""
                    selectedPerfume = perfumeOptions[0]
                    selectedSize = sizeOptions[0]

                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error saving", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate & Save")
        }

        Spacer(modifier = Modifier.height(24.dp))
// LEDGER: Displaying entities via Material Cards
        Text("Previous Transactions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        transactionsList.forEach { t ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Use a Row so we can put the text on the left and the button on the right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ID: ${t.id} - ${t.customerName}", fontWeight = FontWeight.Bold)
                        Text("${t.perfumeChoice} (${t.perfumeSize})")
                        Text(
                            "Total: $${String.format("%.2f", t.total)}",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // WORK ITEM 8: The Share Button
                    IconButton(onClick = {
                        // 1. Format the receipt text
                        val receiptText = """
                            🌸 Perfume Shop Receipt 🌸
                            Customer: ${t.customerName}
                            Item: ${t.perfumeChoice} (${t.perfumeSize})
                            Quantity: ${t.quantity}
                            Total Paid: $${String.format("%.2f", t.total)}
                            Thank you for your purchase!
                        """.trimIndent()

                        // 2. Create the Implicit Intent to share
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, receiptText)
                            type = "text/plain"
                        }

                        // 3. Launch the Android Share Menu
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "Share Receipt Via...")
                        context.startActivity(shareIntent)
                    }) {
                        // Built-in Material Share Icon
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Share,
                            contentDescription = "Share Receipt",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

            }
        }
    }
    if (showCamera) {
        CameraScannerScreen(onBarcodeScanned = { scannedNumber ->
            // 1. Close the camera
            showCamera = false

            // 2. Check the catalog! Match the scanned number to a perfume
            when (scannedNumber) {
                // REPLACE "123456789" with the EXACT number you just scanned on your phone/perfume box!
                "3145891073607" -> {
                    selectedPerfume = perfumeOptions[1] // Changes the dropdown to the 1st perfume
                    Toast.makeText(context, "Scanned: ${perfumeOptions[1]}", Toast.LENGTH_SHORT).show()
                }

                // You can add as many as you want here...
                "987654321" -> {
                    selectedPerfume = perfumeOptions[1] // Changes the dropdown to the 2nd perfume
                    Toast.makeText(context, "Scanned: ${perfumeOptions[1]}", Toast.LENGTH_SHORT).show()
                }

                // If they scan a random bottle of water or something not in our system:
                else -> {
                    Toast.makeText(context, "Item not in database. Scanned ID: $scannedNumber", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}

@Composable
fun BatteryWarningReceiver(context: Context) {
    // DisposableEffect ensures the receiver is safely closed when the app is closed
    DisposableEffect(context) {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_LOW) {
                    Toast.makeText(
                        context,
                        "⚠️ CRITICAL: Battery Low! Plug in the POS system!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Tell Android we specifically only want to listen for the "Battery Low" broadcast
        val filter = IntentFilter(Intent.ACTION_BATTERY_LOW)
        context.registerReceiver(batteryReceiver, filter)

        // Cleanup when the app is closed
        onDispose {
            context.unregisterReceiver(batteryReceiver)
        }
    }
}
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
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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
            text = "Perfume Shop",
            color = Color(0xFFD4AF37), // Gold text
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current

    // --- STATE VARIABLES (These replace findViewById) ---
    var date by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("") }

    // Dropdown State
    val perfumeOptions = listOf("Sauvage by Dior", "Bleu de Chanel", "Acqua Di Gio", "YSL La Nuit", "Tom Ford Oud Wood")
    var expanded by remember { mutableStateOf(false) }
    var selectedPerfume by remember { mutableStateOf(perfumeOptions[0]) }

    // Radio Button State
    val sizeOptions = listOf("50ml", "100ml")
    var selectedSize by remember { mutableStateOf(sizeOptions[0]) }

    // Ledger State
    val transactionsList = remember { mutableStateListOf<PerfumeTransaction>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("New Perfume Sale", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))

        // Basic Text Fields
        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (yyyy-mm-dd)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Customer Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // WIDGET 1: Compose Dropdown (Spinner)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedPerfume,
                onValueChange = {},
                readOnly = true,
                label = { Text("Perfume Choice") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                perfumeOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedPerfume = selectionOption
                            expanded = false
                        }
                    )
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
        OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Price Per Bottle ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = quantityStr, onValueChange = { quantityStr = it }, label = { Text("Quantity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // SAVE BUTTON
        Button(
            onClick = {
                try {
                    val transaction = PerfumeTransaction().apply {
                        id = transactionsList.size + 1
                        transactionDate = date
                        this.customerName = customerName
                        phoneNumber = phone
                        perfumeChoice = selectedPerfume
                        perfumeSize = selectedSize
                        pricePerBottle = priceStr.toIntOrNull() ?: 0
                        quantity = quantityStr.toIntOrNull() ?: 0
                    }

                    PerfumeTransactionBO.calculateTotals(transaction)
                    transactionsList.add(transaction)

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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate & Save")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // LEDGER: Displaying entities via Material Cards
        Text("Previous Transactions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        transactionsList.forEach { t ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ID: ${t.id} - ${t.customerName}", fontWeight = FontWeight.Bold)
                    Text("${t.perfumeChoice} (${t.perfumeSize})")
                    Text("Total: $${String.format("%.2f", t.total)}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
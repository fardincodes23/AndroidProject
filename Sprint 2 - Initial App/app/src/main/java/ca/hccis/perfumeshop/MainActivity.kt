package ca.hccis.perfumeshop

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import bo.PerfumeTransactionBO
import entity.PerfumeTransaction
class MainActivity : AppCompatActivity() {

    // RUBRIC REQUIREMENT: Keep a list of all entities added since app launch
    private val transactionsList = ArrayList<PerfumeTransaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Connect XML widgets to Kotlin variables using findViewById
        val etDate = findViewById<EditText>(R.id.etDate)
        val etCustomerName = findViewById<EditText>(R.id.etCustomerName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val spnPerfumeChoice = findViewById<Spinner>(R.id.spnPerfumeChoice)
        val rgSize = findViewById<RadioGroup>(R.id.rgSize)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etQuantity = findViewById<EditText>(R.id.etQuantity)
        val btnSaveTransaction = findViewById<Button>(R.id.btnSaveTransaction)
        val tvReceipt = findViewById<TextView>(R.id.tvReceipt)

        // 2. What happens when the user clicks "Calculate & Save"
        btnSaveTransaction.setOnClickListener {
            try {
                // A. Create a new blank Entity
                val transaction = PerfumeTransaction()
                transaction.id = transactionsList.size + 1 // Auto-increment ID

                // B. Grab the standard text inputs
                transaction.transactionDate = etDate.text.toString()
                transaction.customerName = etCustomerName.text.toString()
                transaction.phoneNumber = etPhone.text.toString()

                // C. Grab the Spinner (Dropdown) value
                transaction.perfumeChoice = spnPerfumeChoice.selectedItem.toString()

                // D. Grab the RadioButton value
                val selectedSizeId = rgSize.checkedRadioButtonId
                val selectedRadioButton = findViewById<RadioButton>(selectedSizeId)
                transaction.perfumeSize = selectedRadioButton.text.toString()

                // E. Grab the Numbers (If they left it blank, default to 0 to prevent crashing)
                transaction.pricePerBottle = etPrice.text.toString().toIntOrNull() ?: 0
                transaction.quantity = etQuantity.text.toString().toIntOrNull() ?: 0

                // F. Pass to your Business Object for calculations! (Exact same as Console App)
                PerfumeTransactionBO.calculateTotals(transaction)

                // G. Add to our running list
                transactionsList.add(transaction)

                // H. Display the Receipt AND the full list of transactions
                var outputText = "--- LATEST RECEIPT ---\n"
                outputText += transaction.toString() // Uses your formatted toString()

                outputText += "\n--- PREVIOUS TRANSACTIONS (${transactionsList.size}) ---\n"
                for (t in transactionsList) {
                    outputText += "ID ${t.id}: ${t.customerName} bought ${t.perfumeChoice} for $${String.format("%.2f", t.total)}\n"
                }

                tvReceipt.text = outputText

                // --- RESET FORM FOR NEXT CUSTOMER ---

                // 1. Clear text fields
                etCustomerName.text.clear()
                etPhone.text.clear()
                etPrice.text.clear()
                etQuantity.text.clear()
                // (Note: I left etDate alone since the date usually stays the same all day!)

                // 2. Reset Spinner (Dropdown) to the very first option (Index 0)
                spnPerfumeChoice.setSelection(0)

                // 3. Reset RadioGroup to default 50ml selection
                rgSize.check(R.id.rb50ml)

            } catch (e: Exception) {
                // If something goes wrong, show a little popup message
                Toast.makeText(this, "Error saving transaction. Check your inputs.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
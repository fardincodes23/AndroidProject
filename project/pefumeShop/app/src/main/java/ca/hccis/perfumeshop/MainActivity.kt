package ca.hccis.perfumeshop
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import bo.PerfumeTransactionBO
import entity.PerfumeTransaction
class MainActivity : AppCompatActivity() {


    private val transactionsList = ArrayList<PerfumeTransaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etDate = findViewById<EditText>(R.id.etDate)
        val etCustomerName = findViewById<EditText>(R.id.etCustomerName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val spnPerfumeChoice = findViewById<Spinner>(R.id.spnPerfumeChoice)
        val rgSize = findViewById<RadioGroup>(R.id.rgSize)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etQuantity = findViewById<EditText>(R.id.etQuantity)
        val btnSaveTransaction = findViewById<Button>(R.id.btnSaveTransaction)
        val tvReceipt = findViewById<TextView>(R.id.tvReceipt)

        btnSaveTransaction.setOnClickListener {
            try {

                val transaction = PerfumeTransaction()
                transaction.id = transactionsList.size + 1
                transaction.transactionDate = etDate.text.toString()
                transaction.customerName = etCustomerName.text.toString()
                transaction.phoneNumber = etPhone.text.toString()
                transaction.perfumeChoice = spnPerfumeChoice.selectedItem.toString()
                val selectedSizeId = rgSize.checkedRadioButtonId
                val selectedRadioButton = findViewById<RadioButton>(selectedSizeId)
                transaction.perfumeSize = selectedRadioButton.text.toString()
                transaction.pricePerBottle = etPrice.text.toString().toIntOrNull() ?: 0
                transaction.quantity = etQuantity.text.toString().toIntOrNull() ?: 0
                PerfumeTransactionBO.calculateTotals(transaction)
                transactionsList.add(transaction)
                var outputText = "--- LATEST RECEIPT ---\n"
                outputText += transaction.toString()

                outputText += "\n--- PREVIOUS TRANSACTIONS (${transactionsList.size}) ---\n"
                for (t in transactionsList) {
                    outputText += "ID ${t.id}: ${t.customerName} bought ${t.perfumeChoice} for $${String.format("%.2f", t.total)}\n"
                }

                tvReceipt.text = outputText
                etCustomerName.text.clear()
                etPhone.text.clear()
                etPrice.text.clear()
                etQuantity.text.clear()
                spnPerfumeChoice.setSelection(0)
                rgSize.check(R.id.rb50ml)

            } catch (e: Exception) {
                Toast.makeText(this, "Error saving transaction. Check your inputs.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
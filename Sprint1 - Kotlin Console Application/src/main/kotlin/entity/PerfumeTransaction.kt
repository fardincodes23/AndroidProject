package entity

import utility.CisUtility
import java.util.Objects


/**
 * Entity class
 *
 * @author Fardin
 * @since 2026-01-12
 */
class PerfumeTransaction {

    var id: Int = 0
    var transactionDate: String? = null
    var customerName: String? = null
    var phoneNumber: String? = null
    var perfumeChoice: String? = null
    var perfumeSize: String? = null
    var pricePerBottle: Int = 0
    var quantity: Int = 0

    var subTotal: Double = 0.0
    var taxAmount: Double = 0.0
    var total: Double = 0.0

    constructor()

    // Custom constructor
    constructor(
        id: Int,
        transactionDate: String?,
        customerName: String?,
        phoneNumber: String?,
        perfumeChoice: String?,
        perfumeSize: String?,
        pricePerBottle: Int,
        quantity: Int,
        subTotal: Double,
        taxAmount: Double,
        total: Double
    ) {
        this.id = id
        this.transactionDate = transactionDate
        this.customerName = customerName
        this.phoneNumber = phoneNumber
        this.perfumeChoice = perfumeChoice
        this.perfumeSize = perfumeSize
        this.pricePerBottle = pricePerBottle
        this.quantity = quantity
        this.subTotal = subTotal
        this.taxAmount = taxAmount
        this.total = total
    }

    fun getInformation() {
        transactionDate = CisUtility.getInputString("Enter transaction date (yyyy-MM-dd):")
        customerName = CisUtility.getInputString("Enter customer name:")
        phoneNumber = CisUtility.getInputString("Enter phone number:")
        perfumeChoice = CisUtility.getInputString("Enter perfume choice:")
        perfumeSize = CisUtility.getInputString("Enter perfume size:")
        pricePerBottle = CisUtility.getInputInt("Enter price per bottle:")
        quantity = CisUtility.getInputInt("Enter quantity:")
    }
    /**
     * Specialized function for editing.
     * Shows the current value and keeps it if the user just presses Enter.
     */
    fun editInformation() {
        println("\n(Press ENTER to keep the current value)")

        val newDate = CisUtility.getInputString("Date [${this.transactionDate}]: ")
        if (newDate.isNotBlank()) this.transactionDate = newDate

        val newName = CisUtility.getInputString("Customer Name [${this.customerName}]: ")
        if (newName.isNotBlank()) this.customerName = newName

        val newPhone = CisUtility.getInputString("Phone Number [${this.phoneNumber}]: ")
        if (newPhone.isNotBlank()) this.phoneNumber = newPhone

        val newChoice = CisUtility.getInputString("Perfume Choice [${this.perfumeChoice}]: ")
        if (newChoice.isNotBlank()) this.perfumeChoice = newChoice

        val newSize = CisUtility.getInputString("Perfume Size [${this.perfumeSize}]: ")
        if (newSize.isNotBlank()) this.perfumeSize = newSize

        // Handle integers carefully so we don't crash if they leave it blank
        val newPriceStr = CisUtility.getInputString("Price per bottle [${this.pricePerBottle}]: ")
        val newPrice = newPriceStr.toIntOrNull() // Converts to Int, or becomes null if blank
        if (newPrice != null) this.pricePerBottle = newPrice

        val newQtyStr = CisUtility.getInputString("Quantity [${this.quantity}]: ")
        val newQty = newQtyStr.toIntOrNull()
        if (newQty != null) this.quantity = newQty
    }

    override fun toString(): String {
        return "PerfumeTransaction\n" +
                "    ID               = " + id + "\n" +
                "    Date             = " + transactionDate + "\n" +
                "    Customer         = " + customerName + "\n" +
                "    Phone            = " + phoneNumber + "\n" +
                "    Perfume          = " + perfumeChoice + " (" + perfumeSize + ")\n" +
                "    Price/Bottle     = " + CisUtility.toCurrency(pricePerBottle.toDouble()) + "\n" +
                "    Quantity         = " + quantity + "\n" +
                "    SubTotal         = " + CisUtility.toCurrency(subTotal) + "\n" +
                "    Tax              = " + CisUtility.toCurrency(taxAmount) + "\n" +
                "    TOTAL            = " + CisUtility.toCurrency(total) + "\n"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PerfumeTransaction) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hashCode(this.id)
    }
}
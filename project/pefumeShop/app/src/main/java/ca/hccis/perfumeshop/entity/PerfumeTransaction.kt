package entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Objects
import java.text.NumberFormat

/**
 * Entity class
 *
 * @author Fardin
 * @since 2026-01-12
 */

@Entity(tableName = "transactions")
class PerfumeTransaction {

    @PrimaryKey(autoGenerate = true)
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


    override fun toString(): String {
        val formatter = NumberFormat.getCurrencyInstance() // Native currency formatter

        return "PerfumeTransaction\n" +
                "    ID               = " + id + "\n" +
                "    Date             = " + transactionDate + "\n" +
                "    Customer         = " + customerName + "\n" +
                "    Phone            = " + phoneNumber + "\n" +
                "    Perfume          = " + perfumeChoice + " (" + perfumeSize + ")\n" +
                "    Price/Bottle     = " + formatter.format(pricePerBottle.toDouble()) + "\n" +
                "    Quantity         = " + quantity + "\n" +
                "    SubTotal         = " + formatter.format(subTotal) + "\n" +
                "    Tax              = " + formatter.format(taxAmount) + "\n" +
                "    TOTAL            = " + formatter.format(total) + "\n"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PerfumeTransaction) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hashCode(this.id)
    }
}
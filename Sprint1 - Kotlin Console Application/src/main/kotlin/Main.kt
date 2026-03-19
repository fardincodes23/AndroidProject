import bo.PerfumeTransactionBO
import entity.PerfumeTransaction
import utility.CisUtility
import java.lang.IO.println
import java.util.ArrayList

/**
 * Does the console work, adding or viewing transactions.
 * Controls whom to call for specific work.
 *
 * @author Fardin
 * @since 2026-01-12
 */

var transactions: ArrayList<PerfumeTransaction> = ArrayList()

fun main() {

    initialize()

    val appName = "Perfume Shop POS"
    println("Welcome to the $appName application!\n")

    val MENU = "\nMain Menu\nA-Add Transaction\nB-Show All Transactions\nX-Exit"
    var option: String

    do {
        option = CisUtility.getInputString(MENU)

        when (option.uppercase()) {
            "A" -> add()
            "B" -> show()
            "X" -> println("Goodbye")
            else -> println("Invalid Option")
        }

    } while (!option.equals("X", ignoreCase = true))
}

fun add(): PerfumeTransaction {
    println("\nAdd a Transaction")
    val transaction = PerfumeTransaction()
    transaction.id = transactions.size + 1
    transaction.getInformation()
    PerfumeTransactionBO.calculateTotals(transaction)

    println("")
    println(transaction.toString())
    println("")

    transactions.add(transaction)
    return transaction
}

fun show() {
    println("\nHere are the transactions:")
    if (transactions.isEmpty()) {
        println("No transactions found.")
    } else {
        for (t in transactions) {
            println(t.toString())
            println("-------------------------------")
        }
    }
}

fun initialize() {
    // Dummy Data 1
    val t1 = PerfumeTransaction(
        id = 1,
        transactionDate = "2026-01-15",
        customerName = "John Doe",
        phoneNumber = "555-0101",
        perfumeChoice = "Sauvage",
        perfumeSize = "100ml",
        pricePerBottle = 140,
        quantity = 2,
        subTotal = 280.0,
        taxAmount = 42.0,
        total = 322.0
    )
    transactions.add(t1)

    // Dummy Data 2
    val t2 = PerfumeTransaction(
        id = 2,
        transactionDate = "2026-01-16",
        customerName = "Jane Smith",
        phoneNumber = "555-0102",
        perfumeChoice = "Chanel No. 5",
        perfumeSize = "50ml",
        pricePerBottle = 180,
        quantity = 1,
        subTotal = 180.0,
        taxAmount = 27.0,
        total = 207.0
    )
    transactions.add(t2)
}
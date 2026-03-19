import bo.PerfumeTransactionBO
import entity.PerfumeTransaction
import utility.CisUtility

/**
 * Does the console work, adding, viewing, or editing transactions.
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

    // UPDATED: Added C-Edit Transaction to the menu
    val MENU = "\nMain Menu\nA-Add Transaction\nB-Show All Transactions\nC-Edit Transaction\nX-Exit"
    var option: String

    do {
        option = CisUtility.getInputString(MENU)

        // UPDATED: Added "C" to the switch statement
        when (option.uppercase()) {
            "A" -> add()
            "B" -> show()
            "C" -> edit()
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

// NEW FUNCTION: Handles the editing of an existing transaction
fun edit() {
    println("\nEdit a Transaction")

    // 1. Ask the user which ID they want to edit
    val idToEdit = CisUtility.getInputInt("Enter the ID of the transaction to edit:")

    // 2. Search the list for that specific ID
    val transactionToEdit = transactions.find { it.id == idToEdit }

    // 3. If found, let them re-enter the data and recalculate
    if (transactionToEdit != null) {
        println("\n-- Editing Transaction #$idToEdit --")

        // Use the new edit method so users can skip fields
        transactionToEdit.editInformation()

        // Recalculate the taxes and totals with the new prices/quantities
        PerfumeTransactionBO.calculateTotals(transactionToEdit)

        println("\nTransaction Updated Successfully!")
        println(transactionToEdit.toString())
    } else {
        println("\nError: Transaction with ID $idToEdit not found.")
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
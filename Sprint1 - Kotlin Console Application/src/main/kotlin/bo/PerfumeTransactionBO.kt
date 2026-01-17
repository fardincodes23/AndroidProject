package bo
import entity.PerfumeTransaction
object PerfumeTransactionBO {

    /**
     * Calculates transaction totals based on price and quantity.
     * Updates the entity properties directly.
     * @author Fardin
     * @since 2026-01-12
     * @param transaction The transaction entity
     * @return The final total
     */

    const val TAX_RATE: Double = 0.15 // 15% Tax

    fun calculateTotals(transaction: PerfumeTransaction): Double {

        val sub = (transaction.pricePerBottle * transaction.quantity).toDouble()
        val tax = sub * TAX_RATE
        val finalTotal = sub + tax

        transaction.subTotal = sub
        transaction.taxAmount = tax
        transaction.total = finalTotal

        return finalTotal
    }
}
package ca.hccis.perfumeshop // Check your package name!

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import entity.PerfumeTransaction

@Dao
interface PerfumeDao {
    // Grab all saved transactions from the phone's memory
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    suspend fun getAllLocalTransactions(): List<PerfumeTransaction>

    // Save a new transaction to the phone
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PerfumeTransaction)

    // Optional: Save a whole list at once (useful for syncing with the API)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<PerfumeTransaction>)
}
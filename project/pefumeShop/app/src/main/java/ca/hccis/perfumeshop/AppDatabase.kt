package ca.hccis.perfumeshop

import androidx.room.Database
import androidx.room.RoomDatabase
import entity.PerfumeTransaction

@Database(entities = [PerfumeTransaction::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Connects our DAO to the Database
    abstract fun perfumeDao(): PerfumeDao

}
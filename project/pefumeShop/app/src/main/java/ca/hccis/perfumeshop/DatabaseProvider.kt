package ca.hccis.perfumeshop // Check your package name!

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "perfume_database" // Name of the local file
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
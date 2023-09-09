package com.exponential_groth.notenlesetrainer.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

@androidx.room.Database(
    entities = [HighScore::class],
    version = 12
)
abstract class Database: RoomDatabase() {
    abstract fun getHighScoreDao(): HighScoreDao

    companion object {
        @Volatile private var instance: Database? = null

        fun getInstance(context: Context): Database {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): Database {
            return Room.databaseBuilder(context, Database::class.java, "DataBase").fallbackToDestructiveMigration().build()
        }
    }
}
package com.voidloop.formulae.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FormulaEntity::class], version = 1, exportSchema = false)
abstract class AppLocalDatabase : RoomDatabase() {
    abstract fun FormulaDao(): FormulaDAO

    companion object {

        private const val DATABASE_NAME = "formula.db"
        private var INSTANCE: AppLocalDatabase? = null
        private val lock = Any()

        fun getInstance(context: Context): AppLocalDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppLocalDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                }
                return INSTANCE!!
            }
        }
    }
}
package com.example.tallerintegrador.data.local.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * ✅ Base de datos Room para cache local
 *
 * Versión 1: Cache básico de películas y favoritos
 */
@Database(
    entities = [
        PeliculaCacheEntity::class,
        FavoritoCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun peliculaCacheDao(): PeliculaCacheDao
    abstract fun favoritoCacheDao(): FavoritoCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Singleton para la base de datos
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cinema_aguilas_database"
                )
                    .fallbackToDestructiveMigration() // Para desarrollo
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * Limpia la instancia (útil para testing)
         */
        fun clearInstance() {
            INSTANCE = null
        }
    }
}
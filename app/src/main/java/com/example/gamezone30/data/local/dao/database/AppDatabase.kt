package com.example.gamezone30.data.local.dao.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gamezone30.data.local.dao.UserDao
import com.example.gamezone30.data.local.dao.entity.User

@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // 4. Conecta la BD con DAO
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gamezone_database" // Nombre del archivo de la BD
                )
                    .fallbackToDestructiveMigration() // Si cambias la "version", borra la BD vieja
                    .build()

                INSTANCE = instance
                instance // Devuelve la instancia creada
            }
        }
    }
}
package com.unisza.fiknavigator.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unisza.fiknavigator.data.db.model.Building
import com.unisza.fiknavigator.data.db.model.Node
import com.unisza.fiknavigator.data.db.model.NodeConnection
import com.unisza.fiknavigator.data.db.model.RoomEntity
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Database(entities = [Node::class, NodeConnection::class, RoomEntity::class, Building::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nodeDao(): NodeDao
    abstract fun nodeConnectionDao(): NodeConnectionDao
    abstract fun roomDao(): RoomEntityDao
    abstract fun buildingDao(): BuildingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "map.db"
        private const val DB_VERSION_PREF = "db_version_pref"
        private const val DB_VERSION_KEY = "db_version_key"
        private const val CURRENT_DB_VERSION = 1.0f // Update this whenever the database schema is updated

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Check and copy the SQLite database from assets if necessary
                context.applicationContext.copyDatabaseFromAssets()

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).createFromAsset("database/$DB_NAME")
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private fun Context.copyDatabaseFromAssets() {
            val dbPath = getDatabasePath(DB_NAME)
            val preferences = getSharedPreferences(DB_VERSION_PREF, Context.MODE_PRIVATE)
            val savedVersion = preferences.getFloat(DB_VERSION_KEY, 0.0f)

            if (!dbPath.exists() || savedVersion < CURRENT_DB_VERSION) {
                assets.open("database/$DB_NAME").use { inputStream ->
                    FileOutputStream(dbPath).use { outputStream ->
                        copyStream(inputStream, outputStream)
                    }
                }

                // Update the saved version in shared preferences
                preferences.edit().putFloat(DB_VERSION_KEY, CURRENT_DB_VERSION).apply()
            }
        }

        @Throws(IOException::class)
        private fun copyStream(input: InputStream, output: OutputStream) {
            val buffer = ByteArray(1024)
            var length: Int
            while (input.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }
        }
    }
}
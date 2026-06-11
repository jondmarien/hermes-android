package com.hermes.android.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hermes.android.data.local.db.converters.Converters
import com.hermes.android.data.local.db.dao.ChatDao
import com.hermes.android.data.local.db.dao.RemoteConfigDao
import com.hermes.android.data.local.db.dao.SessionDao
import com.hermes.android.domain.model.Chat
import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.domain.model.Session

@Database(
    entities = [
        Session::class,
        Chat::class,
        RemoteConfig::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HermesDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao
    abstract fun chatDao(): ChatDao
    abstract fun remoteConfigDao(): RemoteConfigDao

    companion object {
        @Volatile
        private var INSTANCE: HermesDatabase? = null

        val MIGRATION_1_2 = Migration(1, 2) { database ->
            // Future migrations go here
        }

        fun getDatabase(context: Context): HermesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HermesDatabase::class.java,
                    "hermes_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
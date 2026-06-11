package com.hermes.android.di

import android.content.Context
import androidx.room.Room
import com.hermes.android.data.local.db.HermesDatabase
import com.hermes.android.data.local.db.dao.ChatDao
import com.hermes.android.data.local.db.dao.MessageDao
import com.hermes.android.data.local.db.dao.SessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): HermesDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            HermesDatabase::class.java,
            "hermes_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: HermesDatabase): ChatDao {
        return database.chatDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: HermesDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: HermesDatabase): SessionDao {
        return database.sessionDao()
    }
}
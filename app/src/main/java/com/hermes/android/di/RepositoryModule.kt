package com.hermes.android.di

import com.hermes.android.data.local.db.dao.ChatDao
import com.hermes.android.data.local.db.dao.RemoteConfigDao
import com.hermes.android.data.local.db.dao.SessionDao
import com.hermes.android.data.local.preferences.HermesPreferences
import com.hermes.android.data.local.termux.TermuxManager
import com.hermes.android.data.remote.api.HermesApiService
import com.hermes.android.data.remote.api.HermesHealthService
import com.hermes.android.data.remote.api.HermesResponsesApiService
import com.hermes.android.data.repository.ChatRepositoryImpl
import com.hermes.android.data.repository.RemoteConfigRepositoryImpl
import com.hermes.android.data.repository.SessionRepositoryImpl
import com.hermes.android.data.remote.RemoteHermesRepositoryImpl
import com.hermes.android.domain.repository.ChatRepository
import com.hermes.android.domain.repository.RemoteConfigRepository
import com.hermes.android.domain.repository.SessionRepository
import com.hermes.android.domain.repository.RemoteHermesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideChatRepository(
        chatDao: ChatDao,
        sessionDao: SessionDao
    ): ChatRepository {
        return ChatRepositoryImpl(chatDao, sessionDao)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(
        sessionDao: SessionDao
    ): SessionRepository {
        return SessionRepositoryImpl(sessionDao)
    }

    @Provides
    @Singleton
    fun provideRemoteConfigRepository(
        remoteConfigDao: RemoteConfigDao
    ): RemoteConfigRepository {
        return RemoteConfigRepositoryImpl(remoteConfigDao)
    }

    @Provides
    @Singleton
    fun provideRemoteHermesRepository(
        apiService: HermesApiService,
        responsesApiService: HermesResponsesApiService,
        healthService: HermesHealthService,
        eventSourceListenerFactory: okhttp3.sse.EventSourceListener.Factory
    ): RemoteHermesRepository {
        return RemoteHermesRepositoryImpl(apiService, responsesApiService, healthService, eventSourceListenerFactory)
    }

    @Provides
    @Singleton
    fun provideHermesPreferences(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): HermesPreferences {
        return HermesPreferences(context)
    }

    @Provides
    @Singleton
    fun provideTermuxManager(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
        preferences: HermesPreferences
    ): TermuxManager {
        return TermuxManager(context, preferences)
    }
}
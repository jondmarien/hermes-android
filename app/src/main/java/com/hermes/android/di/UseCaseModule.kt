package com.hermes.android.di

import com.hermes.android.domain.usecase.chat.*
import com.hermes.android.domain.usecase.message.*
import com.hermes.android.domain.usecase.session.*
import com.hermes.android.domain.usecase.remote.*
import com.hermes.android.domain.usecase.termux.*
import com.hermes.android.domain.repository.ChatRepository
import com.hermes.android.domain.repository.MessageRepository
import com.hermes.android.domain.repository.SessionRepository
import com.hermes.android.domain.repository.RemoteHermesRepository
import com.hermes.android.data.local.termux.TermuxManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Chat UseCases
    @Provides
    @Singleton
    fun provideSendMessageUseCase(
        chatRepository: ChatRepository,
        sessionRepository: SessionRepository,
        remoteHermesRepository: RemoteHermesRepository,
        termuxManager: TermuxManager
    ): SendMessageUseCase {
        return SendMessageUseCase(chatRepository, sessionRepository, remoteHermesRepository, termuxManager)
    }

    @Provides
    @Singleton
    fun provideGetMessagesUseCase(
        chatRepository: ChatRepository
    ): GetMessagesUseCase {
        return GetMessagesUseCase(chatRepository)
    }

    @Provides
    @Singleton
    fun provideStreamMessageUseCase(
        remoteHermesRepository: RemoteHermesRepository
    ): StreamMessageUseCase {
        return StreamMessageUseCase(remoteHermesRepository)
    }

    // Session UseCases
    @Provides
    @Singleton
    fun provideCreateSessionUseCase(
        sessionRepository: SessionRepository
    ): CreateSessionUseCase {
        return CreateSessionUseCase(sessionRepository)
    }

    @Provides
    @Singleton
    fun provideGetSessionsUseCase(
        sessionRepository: SessionRepository
    ): GetSessionsUseCase {
        return GetSessionsUseCase(sessionRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteSessionUseCase(
        sessionRepository: SessionRepository,
        chatRepository: ChatRepository,
        messageRepository: MessageRepository
    ): DeleteSessionUseCase {
        return DeleteSessionUseCase(sessionRepository, chatRepository, messageRepository)
    }

    @Provides
    @Singleton
    fun provideRenameSessionUseCase(
        sessionRepository: SessionRepository
    ): RenameSessionUseCase {
        return RenameSessionUseCase(sessionRepository)
    }

    @Provides
    @Singleton
    fun provideGetSessionUseCase(
        sessionRepository: SessionRepository
    ): GetSessionUseCase {
        return GetSessionUseCase(sessionRepository)
    }

    // Remote UseCases
    @Provides
    @Singleton
    fun provideTestRemoteConnectionUseCase(
        remoteHermesRepository: RemoteHermesRepository
    ): TestRemoteConnectionUseCase {
        return TestRemoteConnectionUseCase(remoteHermesRepository)
    }

    @Provides
    @Singleton
    fun provideGetRemoteModelsUseCase(
        remoteHermesRepository: RemoteHermesRepository
    ): GetRemoteModelsUseCase {
        return GetRemoteModelsUseCase(remoteHermesRepository)
    }

    @Provides
    @Singleton
    fun provideGetRemoteCapabilitiesUseCase(
        remoteHermesRepository: RemoteHermesRepository
    ): GetRemoteCapabilitiesUseCase {
        return GetRemoteCapabilitiesUseCase(remoteHermesRepository)
    }

    // Termux UseCases
    @Provides
    @Singleton
    fun provideStartHermesLocalUseCase(
        termuxManager: TermuxManager
    ): StartHermesLocalUseCase {
        return StartHermesLocalUseCase(termuxManager)
    }

    @Provides
    @Singleton
    fun provideStopHermesLocalUseCase(
        termuxManager: TermuxManager
    ): StopHermesLocalUseCase {
        return StopHermesLocalUseCase(termuxManager)
    }

    @Provides
    @Singleton
    fun provideGetTermuxStatusUseCase(
        termuxManager: TermuxManager
    ): GetTermuxStatusUseCase {
        return GetTermuxStatusUseCase(termuxManager)
    }

    @Provides
    @Singleton
    fun provideExecuteTermuxCommandUseCase(
        termuxManager: TermuxManager
    ): ExecuteTermuxCommandUseCase {
        return ExecuteTermuxCommandUseCase(termuxManager)
    }

    @Provides
    @Singleton
    fun provideInstallHermesInTermuxUseCase(
        termuxManager: TermuxManager
    ): InstallHermesInTermuxUseCase {
        return InstallHermesInTermuxUseCase(termuxManager)
    }
}
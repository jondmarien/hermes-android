package com.hermes.android.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hermes.android.data.remote.api.HermesApiService
import com.hermes.android.data.remote.api.HermesHealthService
import com.hermes.android.data.remote.api.HermesResponsesApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.sse.EventSourceListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()
    }

    @Provides
    @Singleton
    fun provideBaseUrl(
        @Named("RemoteBaseUrl") baseUrl: HttpUrl
    ): HttpUrl {
        return baseUrl
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
        @Named("RemoteBaseUrl") baseUrl: HttpUrl
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideHermesApiService(retrofit: Retrofit): HermesApiService {
        return retrofit.create(HermesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHermesResponsesApiService(retrofit: Retrofit): HermesResponsesApiService {
        return retrofit.create(HermesResponsesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHermesHealthService(retrofit: Retrofit): HermesHealthService {
        return retrofit.create(HermesHealthService::class.java)
    }

    @Provides
    @Singleton
    @Named("IO")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @Named("Default")
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton
    fun provideEventSourceListenerFactory(
        okHttpClient: OkHttpClient
    ): (EventSourceListener.Factory) = okHttpClient.newEventSourceListenerFactory()
}
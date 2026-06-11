package com.hermes.android.di

import com.hermes.android.presentation.viewmodel.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.ActivityRetainedComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object ViewModelModule {

    @Provides
    @Singleton
    fun provideMainViewModel(): MainViewModel {
        return MainViewModel()
    }
}
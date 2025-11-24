package com.fragcut.di

import com.fragcut.data.VideoEditorRepository
import com.fragcut.data.VideoEditorRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindVideoEditorRepository(
        videoEditorRepositoryImpl: VideoEditorRepositoryImpl
    ): VideoEditorRepository
}

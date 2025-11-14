package com.stopstone.musicplaylist.di

import com.stopstone.musicplaylist.ui.common.resource.DefaultStringResourceProvider
import com.stopstone.musicplaylist.ui.common.resource.StringResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ResourceModule {
    @Binds
    @Singleton
    abstract fun bindStringResourceProvider(
        provider: DefaultStringResourceProvider,
    ): StringResourceProvider
}


package com.mustafacanyucel.wikirealmexplorer.module

import com.mustafacanyucel.wikirealmexplorer.repository.IWikiRepository
import com.mustafacanyucel.wikirealmexplorer.repository.implementation.MockWikiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WikiRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWikiRepository(mockWikiRepository: MockWikiRepository): IWikiRepository
}
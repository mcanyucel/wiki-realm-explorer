package com.mustafacanyucel.wikirealmexplorer.di

import com.mustafacanyucel.wikirealmexplorer.repository.IWikiRepository
import com.mustafacanyucel.wikirealmexplorer.repository.implementation.WikiRepository
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
    abstract fun bindWikiRepository(wikiRepository: WikiRepository): IWikiRepository
}
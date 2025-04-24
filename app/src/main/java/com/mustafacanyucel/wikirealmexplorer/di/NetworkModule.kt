package com.mustafacanyucel.wikirealmexplorer.di

import com.mustafacanyucel.wikirealmexplorer.api.ConnectionDebugInterceptor
import com.mustafacanyucel.wikirealmexplorer.api.MediaWikiApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        connectionDebugInterceptor: ConnectionDebugInterceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(connectionDebugInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMediaWikiApi(okHttpClient: OkHttpClient): MediaWikiApi {
        val serverUrl = "https://commons.wikimedia.org/" // notice the trailing slash
        return Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MediaWikiApi::class.java)
    }
}


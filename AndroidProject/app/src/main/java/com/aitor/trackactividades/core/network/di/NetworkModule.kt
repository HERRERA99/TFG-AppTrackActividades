package com.aitor.trackactividades.core.network.di

import com.aitor.trackactividades.authentication.data.AuthenticationApiService
import com.aitor.trackactividades.recordActivity.data.ActivitiesApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthenticationApiService(retrofit: Retrofit): AuthenticationApiService {
        return retrofit.create(AuthenticationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideActivitiesApiService(retrofit: Retrofit): ActivitiesApiService {
        return retrofit.create(ActivitiesApiService::class.java)
    }
}
package com.quicksoft.testapp.client

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {
   private const val BASE_URL = "https://supay.in/recharge_api/"
    private const val CACHE_SIZE = 8 * 1024 * 1024L // 5 MB

    @Volatile
    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { retrofit = it }
        }
    }

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(provideApiKeyInterceptor())
            .cache(provideCache())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun provideApiKeyInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                // Optional: Add default headers if needed globally
                // .addHeader("x-rapidapi-key", "YOUR_API_KEY")
                // .addHeader("x-rapidapi-host", "YOUR_API_HOST")
                .build()
            chain.proceed(request)
        }
    }

    private fun provideCache(): Cache {
        val cacheDir = File(System.getProperty("java.io.tmpdir"), "http-cache")
        return Cache(cacheDir, CACHE_SIZE)
    }
}

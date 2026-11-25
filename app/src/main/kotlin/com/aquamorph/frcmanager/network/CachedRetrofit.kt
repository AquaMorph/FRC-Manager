package com.aquamorph.frcmanager.network

import com.aquamorph.frcmanager.utils.Constants
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

private val offlineCache = CacheControl.Builder()
    .onlyIfCached()
    .maxStale(30, TimeUnit.DAYS)
    .build()

internal fun cachedRetrofit(
    baseUrl: String,
    cacheDirectory: File,
    hasNetwork: () -> Boolean,
    authHeader: Pair<String, String>? = null
): Retrofit {
    val client = OkHttpClient.Builder()
        .cache(Cache(cacheDirectory, Constants.CACHE_SIZE))
        .addInterceptor { chain ->
            val request = chain.request()
            val builder = request.newBuilder()
            if (authHeader != null) builder.header(authHeader.first, authHeader.second)
            if (request.method == "GET" && !hasNetwork()) builder.cacheControl(offlineCache)
            chain.proceed(builder.build())
        }
        .build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

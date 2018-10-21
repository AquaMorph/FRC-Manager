package com.aquamorph.frcmanager.network

import android.content.Context
import com.aquamorph.frcmanager.network.NetworkCheck.Companion.hasNetwork
import com.aquamorph.frcmanager.utils.Constants
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private lateinit var retrofit: Retrofit

        fun getRetrofit(context: Context): Retrofit {
            val cacheSize = (10 * 1024 * 1024).toLong()
            val myCache = Cache(context.cacheDir, cacheSize)

            val client = OkHttpClient.Builder().cache(myCache)
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                                    .header(Constants.TBA_HEADER, Constants.getApiHeader()).build()
                        chain.proceed(request)
                    }.build()
            retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.URL)
                    .client(client)
                    .build()
            return retrofit
        }
    }
}
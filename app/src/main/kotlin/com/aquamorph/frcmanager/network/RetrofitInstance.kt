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
            val cacheSize = (5 * 1024 * 1024).toLong()
            val myCache = Cache(context.cacheDir, cacheSize)

            val httpClient = OkHttpClient.Builder().cache(myCache)
                    .addInterceptor { chain ->
                var request = chain.request()
                request = if (hasNetwork(context)!!)
                    request.newBuilder()
                            .header("Cache-Control", "public, max-age=" + 5)
                            .header(Constants.TBA_HEADER, Constants.getApiHeader())
                            .method(request.method(), request.body()).build()
                else
                    request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                            .header(Constants.TBA_HEADER, Constants.getApiHeader())
                            .method(request.method(), request.body()).build()
                chain.proceed(request)
            }

            val client = httpClient.build()
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
package com.aquamorph.frcmanager.network

import com.aquamorph.frcmanager.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private lateinit var retrofit: Retrofit

        fun getRetrofit(): Retrofit {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                        .header(Constants.TBA_HEADER, Constants.getApiHeader())
                        .method(original.method(), original.body())
                        .build()
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
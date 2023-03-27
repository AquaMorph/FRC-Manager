package com.aquamorph.frcmanager.network

import android.content.Context
import com.aquamorph.frcmanager.utils.Constants
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class StatboticsRetrofitInstance {

    companion object {
        private lateinit var retrofit: Retrofit

        /**
         * returns a retrofit instance for Statbotics.
         *
         * @param context app context
         * @return retrofit instance
         */
        fun getRetrofit(context: Context): Retrofit {
            val myCache = Cache(context.cacheDir, Constants.CACHE_SIZE)

            val client = OkHttpClient.Builder().cache(myCache)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder().build()
                    chain.proceed(request)
                }.build()
            retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.STATBOTICS_API_URL)
                .client(client)
                .build()
            return retrofit
        }
    }
}
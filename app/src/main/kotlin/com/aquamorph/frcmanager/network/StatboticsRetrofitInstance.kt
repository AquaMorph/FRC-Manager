package com.aquamorph.frcmanager.network

import android.content.Context
import com.aquamorph.frcmanager.utils.Constants
import retrofit2.Retrofit

object StatboticsRetrofitInstance {
    private var retrofit: Retrofit? = null

    @Synchronized
    fun getRetrofit(context: Context): Retrofit {
        val appContext = context.applicationContext
        return retrofit ?: cachedRetrofit(
            Constants.STATBOTICS_API_URL,
            appContext.cacheDir.resolve("statbotics-http"),
            { NetworkCheck.hasNetwork(appContext) }
        ).also { retrofit = it }
    }
}

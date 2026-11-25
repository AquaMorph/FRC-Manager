package com.aquamorph.frcmanager.network

import android.content.Context
import com.aquamorph.frcmanager.utils.Constants
import retrofit2.Retrofit

object TBARetrofitInstance {
    private var retrofit: Retrofit? = null

    @Synchronized
    fun getRetrofit(context: Context): Retrofit {
        val appContext = context.applicationContext
        return retrofit ?: cachedRetrofit(
            Constants.TBA_API_URL,
            appContext.cacheDir.resolve("tba-http"),
            { NetworkCheck.hasNetwork(appContext) },
            Constants.TBA_HEADER to Constants.getApiHeader()
        ).also { retrofit = it }
    }
}

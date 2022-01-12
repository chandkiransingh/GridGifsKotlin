package com.ck.gridgifskotlin.Interfaces

import com.ck.gridgifskotlin.Interfaces.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ReterofitHelper {

    var BASE_URL = "https://api.giphy.com/"

    fun getInstances() : Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }
}
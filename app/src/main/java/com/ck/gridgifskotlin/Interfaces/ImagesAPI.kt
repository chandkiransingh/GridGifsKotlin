package com.ck.gridgifskotlin.Interfaces

import com.ck.gridgifskotlin.Data
import com.ck.gridgifskotlin.ImagesListJSON
import com.ck.gridgifskotlin.MyClasses.ImagesData
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ImagesAPI {

    @GET("/v1/stickers/trending")
    suspend fun getImages(
        @Query("api_key") api_key: String?,
        @Query("limit") limit: Int?,
        @Query("rating") rating: String?,
        @Query("offset") offset: Int
    ): Response<ImagesListJSON>

}
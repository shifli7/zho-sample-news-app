package com.example.zho.samplenewsapp.data.remote

import com.example.zho.samplenewsapp.BuildConfig
import com.example.zho.samplenewsapp.data.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsNetworkApi {

    companion object {
        var BASEURL = BuildConfig.API_BASEURL
            private set

        var APIKEY = BuildConfig.API_KEY
            private set

        var IMAGE_BASEURL = BuildConfig.IMAGE_BASEURL
            private set
    }

    @GET("/svc/search/v2/articlesearch.json")
    suspend fun getNewsByPage(
        @Query("page") pageNo: Int,
        @Query("api-key") apiKey: String = APIKEY
    ): Response<NewsResponse>

    @GET("/svc/search/v2/articlesearch.json")
    suspend fun getNewsBySearch(
        @Query("q") queryText: String,
        @Query("api-key") apiKey: String = APIKEY
    ): Response<NewsResponse>
}
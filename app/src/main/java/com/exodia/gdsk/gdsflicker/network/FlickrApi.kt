package com.exodia.gdsk.gdsflicker.network

import com.exodia.gdsk.gdsflicker.data.FlickrResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val SEARCH_PARAM = "text"
private const val PAGE_PARAM = "page"

const val FLICKR_API_BASE_URL = "https://api.flickr.com/"

interface FlickrApiService{
    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos(): FlickrResponse

    @GET("services/rest?method=flickr.photos.search")
    suspend fun searchPhotos(@Query(SEARCH_PARAM) query :String,
                             @Query(PAGE_PARAM) page :Long = 1L): FlickrResponse
}
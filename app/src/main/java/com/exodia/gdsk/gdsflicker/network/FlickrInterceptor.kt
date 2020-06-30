package com.exodia.gdsk.gdsflicker.network

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response


const val FLICKR_API_KEY = "044b575b128858997d60420e12d3f86f"
private const val SAFE_SEARCH_PARAM = "safesearch"
private const val API_KEY_PARAM = "api_key"
private const val FORMAT_PARAM = "format"
private const val JSON_CALLBACK_PARAM = "nojsoncallback"
private const val EXTRAS_PARAM = "extras"

class FlickrInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        var newUrl: HttpUrl = originalRequest.url.newBuilder()
            .addQueryParameter(API_KEY_PARAM, FLICKR_API_KEY)
            .addQueryParameter(FORMAT_PARAM, "json")
            .addQueryParameter(JSON_CALLBACK_PARAM, "1")
            .addQueryParameter(EXTRAS_PARAM,"url_s,url_l")
            .addQueryParameter(SAFE_SEARCH_PARAM, "1")
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
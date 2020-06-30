package com.exodia.gdsk.gdsflicker.network

import androidx.lifecycle.MutableLiveData
import com.exodia.gdsk.gdsflicker.data.*
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class FlickrFetcher {
    private val flickrApiService : FlickrApiService
    val dataLoadingState = MutableLiveData<DataLoadingState>()

    init{
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        val  logging : HttpLoggingInterceptor =
            httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }

        val httpClient = OkHttpClient.Builder().addInterceptor(FlickrInterceptor()).addInterceptor(logging).build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(FLICKR_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory()) //to be able to use coroutines and deferred  with retrofit
            .client(httpClient)
            .build()

        flickrApiService = retrofit.create(FlickrApiService::class.java)
    }

    suspend fun fetchPhotos(searchTerm : String, page : Long = 1, scenario: Scenario = Scenario.INITIAL): List<PhotoItem> {
        dataLoadingState.postValue(
            DataLoadingState(
                Status.LOADING,
                scenario = scenario
            )
        )
        try {
            var flickrResponse = if (searchTerm.isEmpty()) {
                flickrApiService.fetchPhotos()
            } else {
                flickrApiService.searchPhotos(searchTerm, page)
            }
            val info: PhotoResponse? = flickrResponse.photos
            var photosList: List<PhotoItem> = info?.photoItems ?: mutableListOf()

            photosList = photosList.filterNot {
                it.url.isBlank() || it.url_large.isBlank()
            }
            dataLoadingState.postValue(
                DataLoadingState(
                    Status.SUCCESS,
                    scenario = scenario
                )
            )

            return photosList
        } catch (e: HttpException) {
            dataLoadingState.postValue(DataLoadingState.error(e.message(), e.code(), scenario = scenario))
            e.printStackTrace()
        } catch (e: IOException) {
            dataLoadingState.postValue(DataLoadingState.error("Please, check your network settings.", scenario = scenario))
            e.printStackTrace()
        } catch (e: Exception) {
            dataLoadingState.postValue(DataLoadingState.error("Unknown internal error.", scenario = scenario))
            e.printStackTrace()
        }

        return emptyList()
    }
}
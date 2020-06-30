package com.exodia.gdsk.gdsflicker.data

import androidx.paging.PageKeyedDataSource
import com.exodia.gdsk.gdsflicker.network.FlickrFetcher
import kotlinx.coroutines.*

class PhotosDataSource(var searchTerm : String, private val coroutineScope: CoroutineScope) : PageKeyedDataSource<Long, PhotoItem>() {

    private val flickrFetcher :FlickrFetcher = FlickrFetcher()
    val dataLoadingState = flickrFetcher.dataLoadingState

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, PhotoItem>
    ) {
        coroutineScope.launch {
            val photosList = flickrFetcher.fetchPhotos(searchTerm)
            callback.onResult(photosList, null, 2)
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, PhotoItem>) {
        coroutineScope.launch {
            val photosList = flickrFetcher.fetchPhotos(searchTerm,
                page = params.key,
                scenario = Scenario.LOAD_AFTER)
            callback.onResult(photosList, params.key.inc())
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, PhotoItem>) {
        coroutineScope.launch {
            val photosList = flickrFetcher.fetchPhotos(searchTerm,
                page = params.key.dec(),
                scenario = Scenario.LOAD_BEFORE)
            callback.onResult(photosList, params.key)
        }
    }

    override fun invalidate() {
        super.invalidate()
        coroutineScope.cancel()
    }
}
package com.exodia.gdsk.gdsflicker.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope

class PhotosDataSourceFactory (var searchTerm : String, private val coroutineScope: CoroutineScope): DataSource.Factory<Long, PhotoItem>() {
    var mutableLiveDataSource : MutableLiveData<PhotosDataSource> = MutableLiveData()
    lateinit var photosDataSource : PhotosDataSource

    override fun create(): PhotosDataSource {
        photosDataSource = PhotosDataSource(searchTerm, coroutineScope)
        mutableLiveDataSource.postValue(photosDataSource)
        return photosDataSource
    }
}
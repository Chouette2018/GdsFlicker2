package com.exodia.gdsk.gdsflicker.ui.gallery

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.exodia.gdsk.gdsflicker.data.DataLoadingState
import com.exodia.gdsk.gdsflicker.data.DataResponse
import com.exodia.gdsk.gdsflicker.data.FlickrRepository
import com.exodia.gdsk.gdsflicker.data.PhotoItem
import com.exodia.gdsk.gdsflicker.search.QueryPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(private val app : Application) : AndroidViewModel(app){
    private val flickRepository = FlickrRepository(viewModelScope, app)
    private val photosLiveData : LiveData<PagedList<PhotoItem>>
    private val searchPhotosLiveData : LiveData<DataResponse>
    private val mutableSearchTerm : MutableLiveData<String> = MutableLiveData<String>()

    val dataLoadingState : LiveData<DataLoadingState>
    val searchTerm : String
        get() = mutableSearchTerm.value ?:""

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)
        searchPhotosLiveData = Transformations.map(mutableSearchTerm) { searchTerm ->
            flickRepository.searchPhotos(searchTerm)
        }

        photosLiveData = Transformations.switchMap(searchPhotosLiveData) { it.photoItems }
        dataLoadingState = Transformations.switchMap(searchPhotosLiveData) { it.dataLoadingState }
    }

    fun getPhotosForContext(): LiveData<PagedList<PhotoItem>>{
        return photosLiveData
    }

    fun searchPhotos(searchTerm :String = ""){

        QueryPreferences.setStoredQuery(app, searchTerm)
        viewModelScope.launch(Dispatchers.IO) {
            flickRepository.insertQuery(searchTerm)
        }
        mutableSearchTerm.value = searchTerm
    }

    fun getSearchTerms(): LiveData<List<String>>{
        return flickRepository.getSearchTerms()
    }
}
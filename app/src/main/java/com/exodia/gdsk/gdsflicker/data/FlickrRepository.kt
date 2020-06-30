package com.exodia.gdsk.gdsflicker.data


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.exodia.gdsk.gdsflicker.data.search.FlickrRoomDatabase
import com.exodia.gdsk.gdsflicker.data.search.QueryDao
import com.exodia.gdsk.gdsflicker.data.search.UserQuery
import kotlinx.coroutines.CoroutineScope

class FlickrRepository(private val scope: CoroutineScope, private val application: Application) {

    private lateinit var pagedSearchPhotos : LiveData<PagedList<PhotoItem>>

    private var userQueryDao : QueryDao = FlickrRoomDatabase.getDatabase(application).queryDao()

    fun searchPhotos(searchTerm : String) : DataResponse{
        val photosDataSourceFactory = PhotosDataSourceFactory(searchTerm, scope)

        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(200)
            .setPageSize(100).build()

        pagedSearchPhotos =
            LivePagedListBuilder<Long, PhotoItem>(photosDataSourceFactory, pagedListConfig).build()

        return DataResponse(
            pagedSearchPhotos,
            dataLoadingState = Transformations.switchMap(photosDataSourceFactory.mutableLiveDataSource){ it.dataLoadingState}
        )
    }

    fun getSearchTerms() : LiveData<List<String>>{
        return userQueryDao.getSearchTerms()
    }

    suspend fun insertQuery(searchTerm: String) {
        userQueryDao.insertQuery(UserQuery(searchTerm))
    }
}
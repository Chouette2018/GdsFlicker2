package com.exodia.gdsk.gdsflicker.data

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.exodia.gdsk.gdsflicker.data.DataLoadingState

data class DataResponse(val photoItems : LiveData<PagedList<PhotoItem>>, val dataLoadingState: LiveData<DataLoadingState>) {
}
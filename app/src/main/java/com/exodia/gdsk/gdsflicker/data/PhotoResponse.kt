package com.exodia.gdsk.gdsflicker.data

import com.google.gson.annotations.SerializedName

class PhotoResponse {

    @SerializedName("page")
    var currentPage : Long = 1

    @SerializedName("pages")
    var numPages : Long = 1

    @SerializedName("perpage")
    var pageSize : Long = 1

    @SerializedName("photo")
    lateinit var photoItems:List<PhotoItem>
}
package com.exodia.gdsk.gdsflicker.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

const val FLICKR_BASE_URL = "https://www.flickr.com/photos/"

@Parcelize
data class PhotoItem
(
    var title : String = "title-gds",
    var id :String = "",
    @SerializedName("url_s") var url : String = "",
    @SerializedName("url_l") var url_large : String = "",
    @SerializedName("owner") var owner : String = "",
    var description : String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ){}

    val photoPageUri : Uri
        get() {
            return Uri.parse(FLICKR_BASE_URL)
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build()
        }

    companion object : Parceler<PhotoItem> {

        override fun PhotoItem.write(parcel: Parcel, flags: Int) {
            parcel.writeString(title)
            parcel.writeString(id)
            parcel.writeString(url)
            parcel.writeString(url_large)
            parcel.writeString(owner)
            parcel.writeString(description)
        }

        override fun create(parcel: Parcel): PhotoItem {
            return PhotoItem(parcel)
        }
    }


}
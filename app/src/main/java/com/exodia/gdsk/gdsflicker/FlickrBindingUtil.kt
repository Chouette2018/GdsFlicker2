package com.exodia.gdsk.gdsflicker

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.exodia.gdsk.gdsflicker.data.PhotoItem

@BindingAdapter("photoTitle")
fun TextView.setTitle(photoItem: PhotoItem){
    text = photoItem.title
}

@BindingAdapter("photoCore")
fun bindImage(imgView: ImageView,  imagePath:String){
    Log.d("GDS", imagePath)
    imagePath.let {
        val imgUrl: Uri? = Uri.parse(imagePath)
        if(imgUrl != null) {
            val imgUri = imgUrl.buildUpon().scheme("https").build()
            Glide.with(imgView.context)
                .load(imgUri)
                .apply(
                    RequestOptions()
                        .error(R.drawable.oops_yellow_320)
                )
                .into(imgView)
        }
    }
}
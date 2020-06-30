package com.exodia.gdsk.gdsflicker.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.exodia.gdsk.gdsflicker.data.PhotoItem
import com.exodia.gdsk.gdsflicker.databinding.GridPhotoItemOverlayBinding

class PhotosAdapter : PagedListAdapter<PhotoItem, PhotoItemHolder>(
    PhototItemDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemHolder {
        return PhotoItemHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: PhotoItemHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }
}

class PhotoItemHolder private constructor (val binding : GridPhotoItemOverlayBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(photoItem:PhotoItem){

        binding.clickListener = View.OnClickListener {

            val action =
                PhotoGalleryFragmentDirections.actionPhototGalleryFragmentToPhotoDetailFragment(
                    photoItem
                )

            it.findNavController().navigate(action)
        }
        binding.photoItem = photoItem
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent : ViewGroup) : PhotoItemHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = GridPhotoItemOverlayBinding.inflate(layoutInflater, parent, false)
            return PhotoItemHolder(binding)
        }
    }
}

class PhototItemDiffCallback : DiffUtil.ItemCallback<PhotoItem>() {
    override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
        return oldItem.id == newItem.id
                && oldItem.title == newItem.title
                && oldItem.url == newItem.url
                && oldItem.url_large == newItem.url_large
    }
}
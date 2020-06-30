package com.exodia.gdsk.gdsflicker.ui.details

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.Target
import com.exodia.gdsk.gdsflicker.R
import com.exodia.gdsk.gdsflicker.databinding.PhotoItemDetailsBinding
import com.exodia.gdsk.gdsflicker.ui.web.PhotoWebActivity
import jp.wasabeef.glide.transformations.BlurTransformation

class PhotoDetailFragment : Fragment() {
    val args: PhotoDetailFragmentArgs by navArgs()
    private lateinit var scaleGestureDetector:ScaleGestureDetector
    private val scaleFactor = 1f
    lateinit var imgPhoto: ImageView
    lateinit var binding : PhotoItemDetailsBinding
    private val TAG = PhotoDetailFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
        setHasOptionsMenu(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.photo_item_details, container,false )

        binding.photoItem = args.argPhotoItem

        //setting background picture
        val urlLarge = binding.photoItem!!.url_large
        val urlSmall = binding.photoItem!!.url

        binding.pbLoadDetails.visibility = View.VISIBLE

        binding.imgPhoto.apply{
            Glide.with(this)
                .load(urlLarge)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(Glide.with(this).load(urlSmall).onlyRetrieveFromCache(true))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d(TAG, "failed to load")
                        binding.pbLoadDetails.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d(TAG, "Main image is ready $urlLarge")

                        binding.pbLoadDetails.visibility = View.INVISIBLE
                        return false
                    }

                })
                .apply(
                    RequestOptions()
                        .error(R.drawable.oops_yellow_320)
                )
                .into(this)
        }

        binding.imgBlurredBackground.apply {
            Glide.with(this)
                .load(urlSmall)
                .onlyRetrieveFromCache(true)
                .apply(bitmapTransform(BlurTransformation(25, 3)))
                .into(this)
        }

        //for zooming
        binding.imgPhoto.setOnTouchListener { _, motionEvent ->
            scaleGestureDetector.onTouchEvent(motionEvent)
            true
        }
        scaleGestureDetector = ScaleGestureDetector(context,
            ScaleListener(
                scaleFactor,
                binding.imgPhoto
            )
        )

        //Image Title on the bar
        if(activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.subtitle = binding.photoItem!!.title
        }

        return binding.root
    }

    private fun getWebInfo(){
        val intent = PhotoWebActivity.newIntent(requireContext(), binding.photoItem!!.photoPageUri)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.photo_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_item_more_info -> {
                getWebInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private class ScaleListener(var scaleFactor:Float,  var imgPhoto:ImageView) : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= detector!!.scaleFactor
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
            if(scaleFactor >= 1) {//don't want to zoom out too much
                imgPhoto.setScaleX(scaleFactor);
                imgPhoto.setScaleY(scaleFactor);
            }
            return true;
        }
    }
}


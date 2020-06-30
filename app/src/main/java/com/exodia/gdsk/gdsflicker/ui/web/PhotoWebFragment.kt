package com.exodia.gdsk.gdsflicker.ui.web

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.exodia.gdsk.gdsflicker.R
import com.exodia.gdsk.gdsflicker.databinding.FragmentPhotoWebBinding
import com.exodia.gdsk.gdsflicker.ui.VisibleFragment

private const val PHOTO_WEB_URI_KEY = "photo_web_url"

class PhotoWebFragment : VisibleFragment() {
    private lateinit var uri: Uri
    private lateinit var webview: WebView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uri = arguments?.getParcelable(PHOTO_WEB_URI_KEY) ?: Uri.EMPTY
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentPhotoWebBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_web, container, false)

        progressBar = binding.pbLoadWeb
        progressBar.max = 100

        webview = binding.wvPhoto
        webview.settings.javaScriptEnabled = true
        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(webView: WebView, newProgress: Int){
                if(newProgress == 100){
                    progressBar.visibility = View.GONE
                }else{
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
            }


            override fun onReceivedTitle(view: WebView?, title: String?) {
                (activity as AppCompatActivity).supportActionBar?.subtitle = title
            }
        }

        webview.webViewClient = WebViewClient()
        webview.loadUrl(uri.toString())

        return binding.root
    }

    companion object {
        fun newInstance(uri:Uri):PhotoWebFragment {
            return PhotoWebFragment().apply{
                arguments = Bundle().apply{
                    putParcelable(PHOTO_WEB_URI_KEY, uri)
                }
            }
        }
    }
}
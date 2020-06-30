package com.exodia.gdsk.gdsflicker.ui.web

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.exodia.gdsk.gdsflicker.R

class PhotoWebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_web)

        val fm = supportFragmentManager
        val currentFragment = fm.findFragmentById(R.id.fragment_container)

        if (currentFragment == null){
            val fragment = PhotoWebFragment.newInstance(intent.data!!)
            fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    companion object{
        fun newIntent(context: Context, photoWebUri : Uri): Intent {
            return Intent(context, PhotoWebActivity::class.java).apply{
                data = photoWebUri
            }
        }
    }
}

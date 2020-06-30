package com.exodia.gdsk.gdsflicker.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.exodia.gdsk.gdsflicker.polling.PollWorker

abstract class VisibleFragment : Fragment() {
    private val onShowNotification = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent : Intent) {
            Toast.makeText(requireContext(),
            "received broadcast : ${intent.action}",
            Toast.LENGTH_LONG)
            .show()

            resultCode = Activity.RESULT_CANCELED
        }
    }
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(PollWorker.ACTION_DISPLAY_NOTIFICATION)
        requireActivity().registerReceiver(
            onShowNotification,
            filter,
            PollWorker.PERMISSION_PRIVATE,
            null
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(onShowNotification)
    }
}
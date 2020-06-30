package com.exodia.gdsk.gdsflicker.polling

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class NotificationReceiver : BroadcastReceiver() {
    private val TAG = NotificationReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "received broadcast : ${intent.action}")
        Log.i(TAG, "received result : $resultCode")

        if(resultCode != Activity.RESULT_OK){
            return
        }

        NotificationAgent.sendPollingNotification(context)
    }
}

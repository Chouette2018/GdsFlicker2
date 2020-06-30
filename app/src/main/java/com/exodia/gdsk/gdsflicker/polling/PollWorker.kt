package com.exodia.gdsk.gdsflicker.polling

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import com.exodia.gdsk.gdsflicker.data.PhotoItem
import com.exodia.gdsk.gdsflicker.network.FlickrFetcher
import com.exodia.gdsk.gdsflicker.search.QueryPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.Dispatcher
import java.util.concurrent.TimeUnit

private const val POLL_WORK_ID = "com.exodia.gdsk.gdsflicker.polling.worker"
private const val POLL_WORK_INTERVAL = 15L
private val TAG = PollWorker::class.java.simpleName

class PollWorker(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.i(TAG, "poll worker on the move")

        val query = QueryPreferences.getStoredQuery(context)
        CoroutineScope(Dispatchers.IO).launch {
            val items = FlickrFetcher().fetchPhotos(query)
            withContext(Dispatchers.Default) {
                val lastResultId = QueryPreferences.getLastResultId(context)
                if (items.isNotEmpty()) {
                    val resultId = items.first().id
                    if (resultId == lastResultId) {
                        Log.i(TAG, "no changes : $resultId")
                    } else {
                        Log.i(TAG, "new result : $resultId")
                        QueryPreferences.setLastResultId(context, resultId)

                        context.sendOrderedBroadcast(Intent(ACTION_DISPLAY_NOTIFICATION), PERMISSION_PRIVATE)
                    }
                }
            }
        }
        return Result.success()
    }

    companion object{
        const val ACTION_DISPLAY_NOTIFICATION =
            "com.exodia.gdsk.gdsflicker.DISPLAY_NOTIFICATION"

        const val PERMISSION_PRIVATE =
            "com.exodia.gdsk.gdsflicker.PRIVATE"

        fun updatePolling(currentContext: Context){
            val isPolling = QueryPreferences.isPolling(currentContext)
            if(isPolling){
                WorkManager.getInstance().cancelUniqueWork(POLL_WORK_ID)
                QueryPreferences.setPolling(currentContext, false)
            }else{
                Log.i(TAG, "enabling polling")
                val constraints = Constraints
                    .Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .setRequiresBatteryNotLow(true)
                    .build()

                val periodicRequest = PeriodicWorkRequest
                    .Builder(PollWorker::class.java, POLL_WORK_INTERVAL, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build()

                WorkManager.getInstance().enqueueUniquePeriodicWork(POLL_WORK_ID,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicRequest)

                QueryPreferences.setPolling(currentContext, true)
            }
        }
    }
}
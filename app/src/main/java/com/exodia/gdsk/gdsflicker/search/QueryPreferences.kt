package com.exodia.gdsk.gdsflicker.search

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

private const val PREF_SEARCH_QUERY_KEY = "com.exodia.gdsk.gdsflicker.search.query"
private const val PREF_LAST_RESULT_ID_KEY = "com.exodia.gdsk.gdsflicker.search.lastResultId"
private const val PREF_FLICKR_FILE_KEY = "com.exodia.gdsk.gdsflicker"
private const val PREF_IS_POLLING_KEY = "com.exodia.gdsk.gdsflicker.search.polling"

object QueryPreferences {

    fun getStoredQuery(context: Context) : String {
        val prefs = context.getSharedPreferences(PREF_FLICKR_FILE_KEY, MODE_PRIVATE)
        return prefs.getString(PREF_SEARCH_QUERY_KEY, "")!!
    }

    fun setStoredQuery(context: Context,query : String) {
        context.getSharedPreferences(PREF_FLICKR_FILE_KEY, MODE_PRIVATE)
            .edit {
                putString(PREF_SEARCH_QUERY_KEY, query)
            }
    }

    fun getLastResultId(context: Context) : String {
        val prefs = context.getSharedPreferences(PREF_FLICKR_FILE_KEY, MODE_PRIVATE)
        return prefs.getString(PREF_LAST_RESULT_ID_KEY, "")!!
    }

    fun setLastResultId(context: Context,lastResultId : String) {
        context.getSharedPreferences(PREF_FLICKR_FILE_KEY, MODE_PRIVATE)
            .edit {
                putString(PREF_LAST_RESULT_ID_KEY, lastResultId)
            }
    }

    fun isPolling(context: Context) : Boolean {
        val prefs = context.getSharedPreferences(PREF_FLICKR_FILE_KEY, MODE_PRIVATE)
        return prefs.getBoolean(PREF_IS_POLLING_KEY, false)
    }

    fun setPolling(context: Context, isPolling : Boolean) {
        context.getSharedPreferences(PREF_FLICKR_FILE_KEY, MODE_PRIVATE)
            .edit {
                putBoolean(PREF_IS_POLLING_KEY, isPolling)
            }
    }
}
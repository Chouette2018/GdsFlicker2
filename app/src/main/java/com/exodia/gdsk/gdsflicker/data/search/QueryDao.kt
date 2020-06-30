package com.exodia.gdsk.gdsflicker.data.search

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QueryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(query : UserQuery)

    @Query("SELECT user_query FROM user_queries_table ORDER BY addition_time DESC")
    fun getSearchTerms() : LiveData<List<String>>

    @Query("SELECT user_query FROM user_queries_table WHERE user_query LIKE '%' || :partialQuery || '%' ORDER BY user_query ASC")
    fun getSearchTermsStartingWith(partialQuery:String) : LiveData<List<String>>

    @Query("DELETE FROM user_queries_table")
    suspend fun deleteAll()
}
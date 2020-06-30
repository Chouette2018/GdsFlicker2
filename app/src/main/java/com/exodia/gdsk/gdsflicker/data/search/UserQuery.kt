package com.exodia.gdsk.gdsflicker.data.search

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_queries_table")
data class UserQuery(@PrimaryKey @ColumnInfo(name = "user_query") val query : String = "dummy",
                     @ColumnInfo(name = "addition_time") val additionTime : Long = System.currentTimeMillis())
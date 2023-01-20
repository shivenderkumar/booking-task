package com.seirios.taskbooking.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity(tableName = "booking")
data class BookingItem(
    @PrimaryKey(autoGenerate = true)
    val id : Int?,
    val facility : String,
    val date : String,
    val startTime : String,
    val endTime : String,
    val fee : Int
)
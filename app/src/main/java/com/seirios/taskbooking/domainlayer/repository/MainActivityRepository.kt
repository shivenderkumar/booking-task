package com.seirios.taskbooking.domainlayer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seirios.taskbooking.data.db.BookingDao
import com.seirios.taskbooking.data.model.BookingItem
import javax.inject.Inject

class MainActivityRepository @Inject constructor(
    private  val bookingDao: BookingDao
){

    suspend fun insertBookedItem(bookingItem: BookingItem) = bookingDao.insertBooking(bookingItem)
    suspend fun deleteBookedItemById(id: Int) = bookingDao.deleteBookedItemById(id)
    fun getBookingItems(): LiveData<List<BookingItem>> = bookingDao.getAllBookings()
}
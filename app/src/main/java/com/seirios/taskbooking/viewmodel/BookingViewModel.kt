package com.seirios.taskbooking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seirios.taskbooking.data.model.BookingItem
import com.seirios.taskbooking.domainlayer.repository.MainActivityRepository
import kotlinx.coroutines.launch

class BookingViewModel (private val mainActivityRepository: MainActivityRepository) : ViewModel(){

    suspend fun insertBookedItem(bookingItem: BookingItem) = mainActivityRepository.insertBookedItem(bookingItem)
    suspend fun deleteBookedItemById(id: Int) = mainActivityRepository.deleteBookedItemById(id)
    fun getBookingItems(): LiveData<List<BookingItem>> = mainActivityRepository.getBookingItems()
}

















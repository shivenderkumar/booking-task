package com.seirios.taskbooking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.seirios.taskbooking.domainlayer.repository.MainActivityRepository
import javax.inject.Inject

class BookingViewModelFactory
@Inject constructor (private val mainActivityRepository: MainActivityRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookingViewModel(mainActivityRepository) as T
    }
}
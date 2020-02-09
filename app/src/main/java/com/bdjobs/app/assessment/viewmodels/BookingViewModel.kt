package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.assessment.models.Booking
import com.bdjobs.app.assessment.models.BookingResponse
import com.bdjobs.app.assessment.repositories.BookingRepository
import kotlinx.coroutines.launch

class BookingViewModel(booking: Booking,application: Application) : AndroidViewModel(application) {

    private val bookingRepository = BookingRepository(application,booking)
    lateinit var bookingResponse : BookingResponse

    fun bookSchedule(){
        viewModelScope.launch {
            try {
                bookingResponse = bookingRepository.bookSchedule()
                Log.d("rakib", "${bookingResponse.message}")
            } catch (e: Exception) {
            }
        }
    }

}
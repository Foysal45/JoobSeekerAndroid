package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.assessment.Event
import com.bdjobs.app.assessment.models.Booking
import com.bdjobs.app.assessment.models.BookingResponse
import com.bdjobs.app.assessment.models.ScheduleData
import com.bdjobs.app.assessment.repositories.BookingRepository
import kotlinx.coroutines.launch

class BookingOverviewViewModel(scheduleData: ScheduleData, application: Application) : AndroidViewModel(application) {

    private var bookingRepository: BookingRepository
    lateinit var bookingResponse: BookingResponse
    private var booking: Booking = Booking()

    var schedule: ScheduleData

    private val _scheduleData = MutableLiveData<ScheduleData>()
    val scheduleData: LiveData<ScheduleData>
        get() = _scheduleData

    private val _bookingData = MutableLiveData<Booking>()
    val bookingData: LiveData<Booking>
        get() = _bookingData

    private val _navigateToPayment = MutableLiveData<Event<Booking>>()
    val navigateToPayment: LiveData<Event<Booking>>
        get() = _navigateToPayment

    init {
        _scheduleData.value = scheduleData
        schedule = scheduleData
        bookingRepository = BookingRepository(application, booking)
    }

    fun setUpBookingData() {

        booking.apply {
            scId = schedule.scId
            schId = schedule.schlId
            strActionType = schedule.actionType
        }

        _bookingData.value = booking

        displayPayment(booking)

    }

    fun cancelSchedule() {

        booking.apply {
            scId = schedule.scId
            schId = schedule.schlId
            strActionType = "D"
        }

        viewModelScope.launch {
            try {
                bookingResponse = bookingRepository.manageSchedule()
            } catch (e: Exception) {
            }
        }
    }

    private fun displayPayment(booking: Booking){
        _navigateToPayment.value = Event(booking)
    }

}
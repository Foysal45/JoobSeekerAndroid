package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.assessment.Event
import com.bdjobs.app.assessment.enums.Status
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

    private val _navigateToHome = MutableLiveData<Event<BookingResponse>>()
    val navigateToHome: LiveData<Event<BookingResponse>>
        get() = _navigateToHome

    private val _showSnackbar = MutableLiveData<Event<BookingResponse>>()
    val showSnackbar : LiveData<Event<BookingResponse>>
    get() = _showSnackbar

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    init {
        _status.value = Status.DONE
        _scheduleData.value = scheduleData
        schedule = scheduleData
        bookingRepository = BookingRepository(application, booking)
    }

    fun setUpBookingData() {

        booking.apply {
            scId = schedule.scId
            schId = schedule.schlId
            strActionType = schedule.actionType
            isFromHome = "0"
        }

        Log.d("rakib","${booking.strActionType}")

        _bookingData.value = booking

        when(booking.strActionType)
        {
            "I" -> {
                displayPayment(booking)
            }
            "U" ->{
                updateSchedule()
            }
        }
    }

    fun updateSchedule()
    {
        booking.apply {
            scId = schedule.scId
            schId = schedule.schlId
            strActionType = "U"
        }

        viewModelScope.launch {
            try {
                _status.value = Status.LOADING
                bookingResponse = bookingRepository.manageSchedule()
                _status.value = Status.DONE
                if (bookingResponse.statuscode.equals("0")){
                    displayHome(bookingResponse)
                } else {
                    displaySnackBar(bookingResponse)
                }
            } catch (e: Exception) {
                Log.d("rakib","catch")
                _status.value = Status.ERROR
            }
        }
    }

    fun cancelSchedule() {

        booking.apply {
            scId = schedule.scId
            schId = schedule.schlId
            strActionType = "D"
        }

        viewModelScope.launch {
            try {
                _status.value = Status.LOADING
                bookingResponse = bookingRepository.manageSchedule()
                _status.value = Status.DONE
                if (bookingResponse.statuscode.equals("0")){
                    displayHome(bookingResponse)
                } else {
                    displaySnackBar(bookingResponse)
                }
            } catch (e: Exception) {
                _status.value = Status.ERROR
            }
        }
    }

    private fun displayPayment(booking: Booking) {
        _navigateToPayment.value = Event(booking)
    }

    private fun displayHome(bookingResponse: BookingResponse) {
        _navigateToHome.value = Event(bookingResponse)
    }

    private fun displaySnackBar(bookingResponse: BookingResponse) {
        _showSnackbar.value = Event(bookingResponse)
    }

}
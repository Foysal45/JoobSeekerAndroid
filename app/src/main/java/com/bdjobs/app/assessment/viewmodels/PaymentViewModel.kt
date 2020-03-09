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
import com.bdjobs.app.assessment.repositories.BookingRepository
import com.bdjobs.app.assessment.repositories.PaymentRepository
import kotlinx.coroutines.launch

class PaymentViewModel(booking: Booking, application: Application) : AndroidViewModel(application) {

    private var bookingRepository: BookingRepository
    private var paymentRepository : PaymentRepository
    lateinit var bookingResponse: BookingResponse
    private var booking: Booking = Booking()

    var fullname : String? = null

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    private val _navigateToSuccessful = MutableLiveData<BookingResponse>()
    val navigateToSuccessful: LiveData<BookingResponse>
        get() = _navigateToSuccessful

    init {
        paymentRepository = PaymentRepository(application)
        fullname = paymentRepository.bdjobsUserSession.fullName
        bookingRepository = BookingRepository(application, booking)
        _status.value = Status.DONE
    }

    fun bookSchedule() {
        Log.d("rakib", "came here")
        _status.value = Status.LOADING
        viewModelScope.launch {
            try {
                _status.value = Status.LOADING
                bookingResponse = bookingRepository.manageSchedule()
                if (bookingResponse.statuscode.equals("0"))
                    _navigateToSuccessful.value = bookingResponse
                _status.value = Status.DONE
            } catch (e: Exception) {
                _status.value = Status.ERROR
            }
        }
    }

}
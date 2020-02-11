package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bdjobs.app.assessment.models.Booking

class PaymentViewModel(booking: Booking, application: Application) : AndroidViewModel(application) {

    private val _bookingData = MutableLiveData<Booking>()
    val booking : LiveData<Booking>
        get() = _bookingData



}
package com.bdjobs.app.sms.ui.payment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository

class PaymentViewModel(val repository: SMSRepository, private val totalSMS: Int?, val totalTaka: Int?) : ViewModel() {

    private val _quantity = MutableLiveData<Int>().apply {
        value = 1
    }

    private val _totalNumberOfSMS = MutableLiveData<Int>().apply {
        value = totalSMS
    }
    val totalNumberOfSMS = _totalNumberOfSMS


    private val _totalAmountIntTaka = MutableLiveData<Int>().apply {
        value = totalTaka?.times(_quantity.value!!)
    }
    val totalAmountIntTaka = _totalAmountIntTaka


}
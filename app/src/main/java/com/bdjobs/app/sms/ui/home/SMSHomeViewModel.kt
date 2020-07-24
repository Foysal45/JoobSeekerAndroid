package com.bdjobs.app.sms.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.sms.data.repository.SMSRepository

class SMSHomeViewModel(private val smsRepository: SMSRepository) : ViewModel() {


//    private val _navigateToPayment = MutableLiveData<Event<Boolean>>()
//    val navigateToPayment : LiveData<Event<Boolean>> = _navigateToPayment
//
//    fun onStartFreeTrialButtonClick(){
//        viewModelScope.launch {
//           val response = smsRepository.callPaymentInfoBeforeGatewayApi()
//        }
//    }

    private val _isSMSFree = MutableLiveData<Boolean>()
    val isSMSFree : LiveData<Boolean> = _isSMSFree

    init {
        _isSMSFree.value = Constants.isSMSFree.equalIgnoreCase("True")
    }
}
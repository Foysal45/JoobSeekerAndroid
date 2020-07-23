package com.bdjobs.app.sms.ui.payment_success

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.videoInterview.util.Event

class PaymentSuccessViewModel(private val repository: SMSRepository) : ViewModel() {

    private val _fullName = MutableLiveData<String>().apply {
        value = repository.getFullName()
    }
    val fullName: LiveData<String> = _fullName.apply {
        value = this.value.toString().trim()
    }

    private val _navigateToSMSHome = MutableLiveData<Event<Boolean>>()
    val navigateToSMSHome: LiveData<Event<Boolean>> = _navigateToSMSHome

    private val _navigateToSMSFreeTrialHome = MutableLiveData<Event<Boolean>>()
    val navigateToSMSFreeTrialHome: LiveData<Event<Boolean>> = _navigateToSMSFreeTrialHome

    init {
        Constants.isSMSFree = "False"
    }

    fun onSMSJobAlertButtonClick() {
        if (Constants.isSMSFree.equalIgnoreCase("True")) {
            _navigateToSMSFreeTrialHome.value = Event(true)
        } else {
            _navigateToSMSHome.value = Event(true)
        }
    }
}

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

    init {
        Constants.isSMSFree = "False"
    }

}

package com.bdjobs.app.sms.ui.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.databinding.FragmentPaymentSmsBinding
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.sms.ui.payment.PaymentViewModel
import com.bdjobs.app.videoInterview.util.Event
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.coroutines.launch

class HomeCommonViewModel(private val smsRepository: SMSRepository) : ViewModel() {


//    private val _navigateToPayment = MutableLiveData<Event<Boolean>>()
//    val navigateToPayment : LiveData<Event<Boolean>> = _navigateToPayment
//
//    fun onStartFreeTrialButtonClick(){
//        viewModelScope.launch {
//           val response = smsRepository.callPaymentInfoBeforeGatewayApi()
//        }
//    }
}
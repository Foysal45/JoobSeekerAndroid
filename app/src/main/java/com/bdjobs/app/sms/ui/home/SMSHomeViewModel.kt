package com.bdjobs.app.sms.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.sms.data.model.SMSSettingsData
import com.bdjobs.app.sms.data.repository.SMSRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.lang.Error
import java.net.SocketException

class SMSHomeViewModel(private val smsRepository: SMSRepository) : ViewModel() {


    private val _price = MutableLiveData<Int>().apply {
        value = 50
    }
    val price : LiveData<Int> = _price

    private val _isSMSFree = MutableLiveData<Boolean>()
    val isSMSFree : LiveData<Boolean> = _isSMSFree

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private var _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> = _isSuccess

    private var _smsData = MutableLiveData<SMSSettingsData>()
    val smsData : LiveData<SMSSettingsData> = _smsData

    private var _error = MutableLiveData<String> ()
    val error: LiveData<String> = _error

    fun checkIfSMSFree() {
        _isSMSFree.value = Constants.isSMSFree.equalIgnoreCase("True")
    }


    fun fetchSMSSettingsData() {
        _isLoading.value = true

        viewModelScope.launch {
            try {

                val response = smsRepository.getSMSSettings()

                _isLoading.value = false

                if (response.statuscode=="0" && response.message == "Success") {
                    _isSuccess.value = true
                } else {
                    Timber.e("Invalid response: ${response.statuscode} :: Message: ${response.message}")
                    _error.value = "Something went wrong! Please try again later"
                }

            } catch (t:Throwable) {
                Timber.e("Exception while fetching sms settings data: ${t.localizedMessage}")
                _isLoading.value = false
                when(t) {
                    is IOException -> _error.value = "Please check your internet connection and try again later"
                    is SocketException -> _error.value = "Please check your internet connection and try again later"
                    else -> _error.value = "Something went wrong! Please try again later"
                }
            }
        }
    }
}
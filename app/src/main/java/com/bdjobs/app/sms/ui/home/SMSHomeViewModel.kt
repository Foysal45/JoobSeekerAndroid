package com.bdjobs.app.sms.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.utilities.Constants
import com.bdjobs.app.utilities.equalIgnoreCase
import com.bdjobs.app.sms.data.model.SMSSettingsData
import com.bdjobs.app.sms.data.repository.SMSRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.net.SocketException

class SMSHomeViewModel(private val smsRepository: SMSRepository) : ViewModel() {


    private val _price = MutableLiveData<Int>().apply {
        value = 50
    }
    val price: LiveData<Int> = _price

    private val _isSMSFree = MutableLiveData<Boolean>()
    val isSMSFree: LiveData<Boolean> = _isSMSFree

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private var _smsData = MutableLiveData<SMSSettingsData>()
    val smsData: LiveData<SMSSettingsData> = _smsData

    private var _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var _isDataFound = MutableLiveData<Boolean>()
    val isDataFound: LiveData<Boolean> = _isDataFound

    private var _isTrialConsumed = MutableLiveData<Boolean>()
    val isTrialConsumed: LiveData<Boolean> = _isTrialConsumed

    private var _remainingSMSCount = MutableLiveData<Int>().apply { value = 0 }
    val remainingSMSCount: LiveData<Int> = _remainingSMSCount

    private var _isSMSAlertOn = MutableLiveData<Boolean>()
    val isSMSAlertOn: LiveData<Boolean> = _isSMSAlertOn

    private var _customSmsAmount = MutableLiveData<Int>()
    val customSmsAmount: LiveData<Int> = _customSmsAmount

    private var _bonusSmsAmount = MutableLiveData<Int>()
    val bonusSmsAmount: LiveData<Int> = _bonusSmsAmount

    private var _customSmsPrice = MutableLiveData<Int>()
    val customSmsPrice: LiveData<Int> = _customSmsPrice

    private var _freeSMSLimit = MutableLiveData<String>()
    val freeSMSLimit: LiveData<String> = _freeSMSLimit

    private var _isFreeSMSAvailable = MutableLiveData<Boolean>()
    val isFreeSMSAvailable : LiveData<Boolean> = _isFreeSMSAvailable

    init {
        _customSmsAmount.value = 100
        _bonusSmsAmount.value = 0
        _customSmsPrice.value = 50
    }

    fun checkIfSMSFree() {
        _isSMSFree.value = Constants.isSMSFree.equalIgnoreCase("True")
    }


    fun fetchSMSSettingsData() {
        _isLoading.value = true

        viewModelScope.launch {
            try {

                val response = smsRepository.getSMSSettings()

                _isLoading.value = false

                if (response.statuscode == "0" && response.message == "Success") {
                    _isSuccess.value = true
                    _isDataFound.value = true

                    val data = response.data!![0]

                    _smsData.value = data
                    _isTrialConsumed.value = data.trialConsumed == "True"
                    _remainingSMSCount.value = data.remainingSMSAmount?.toInt() ?: 0
                    _isSMSAlertOn.value = data.smsAlertOn == "True"
                    _freeSMSLimit.value = data.freeSmsLimite ?: "0"

                    _isFreeSMSAvailable.value = _freeSMSLimit.value!="0"

                } else if (response.statuscode == "3") {
                    _isSuccess.value = true
                    _isTrialConsumed.value = false
                    _isDataFound.value = false
                    Timber.d("Data Size : ${response.data?.size}")
                    if (response.data != null && response.data.isNotEmpty()) {
                        Timber.d("Free limit = ${response.data[0].freeSmsLimite}")
                        _freeSMSLimit.value = response.data[0].freeSmsLimite ?: "0"
                        _isFreeSMSAvailable.value = _freeSMSLimit.value!="0"
                    }

                } else {
                    Timber.e("Invalid response: ${response.statuscode} :: Message: ${response.message}")
                    _error.value =
                        response.message ?: "Something went wrong! Please try again later"
                }

            } catch (t: Throwable) {
                Timber.e("Exception while fetching sms settings data: ${t.localizedMessage}")
                _isLoading.value = false
                when (t) {
                    is IOException -> _error.value =
                        "Please check your internet connection and try again later"
                    is SocketException -> _error.value =
                        "Please check your internet connection and try again later"
                    else -> _error.value = "Something went wrong! Please try again later"
                }
            }
        }
    }

    fun onCustomSMSAddClicked() {
        if (_customSmsAmount.value!! < 9900) {
            _customSmsAmount.value = _customSmsAmount.value?.plus(100)
            _bonusSmsAmount.value = _customSmsAmount.value!! / 10
            _customSmsPrice.value = _customSmsPrice.value?.plus(50)
        }

    }

    fun onCustomSMSMinusClicked() {
        if (_customSmsAmount.value!! > 100) {
            _customSmsAmount.value = _customSmsAmount.value?.minus(100)
            if (customSmsAmount.value == 100) _bonusSmsAmount.value = 0
            else _bonusSmsAmount.value = _customSmsAmount.value!! / 10
            _customSmsPrice.value = _customSmsPrice.value?.minus(50)
        }

    }
}
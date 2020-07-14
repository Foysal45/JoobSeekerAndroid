package com.bdjobs.app.sms.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.videoInterview.util.Event
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SMSRepository) : ViewModel() {

    private val _limit = MutableLiveData<String?>()
    val limit: LiveData<String?> = _limit

    private val _remainingSMS = MutableLiveData<String?>()
    val remainingSMS: LiveData<String?> = _remainingSMS

    private val _totalSMS = MutableLiveData<String?>()
    val totalSMS: LiveData<String?> = _totalSMS

    private val _isAlertOn = MutableLiveData<String?>()
    val isAlertOn: LiveData<String?> = _isAlertOn

    private val _maxProgress = MutableLiveData<Int>()
    val maxProgress: LiveData<Int> = _maxProgress

    private val _totalProgress = MutableLiveData<Int>()
    val totalProgress: LiveData<Int> = _totalProgress


    private val _navigateToHome = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val navigateToHome: LiveData<Event<Boolean>> = _navigateToHome

    private val _navigateToHomeFreeTrial = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val navigateToHomeFreeTrial: LiveData<Event<Boolean>> = _navigateToHomeFreeTrial


    private val _openDialogEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val openDialogEvent: LiveData<Event<Boolean>> = _openDialogEvent

    private val _openTurnOffSMSDialogEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val openTurnOffSMSDialogEvent: LiveData<Event<Boolean>> = _openTurnOffSMSDialogEvent


    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean> = _isDataLoading


    private val _showToastMessage = MutableLiveData<Event<String?>>()
    val showToastMessage : LiveData<Event<String?>> = _showToastMessage

    init {
        getSMSSettings()
    }


    fun onViewSMSJobAlertButtonClick() {
        if (Constants.isSMSFree.equalIgnoreCase("False"))
            _navigateToHome.value = Event(true)
        else
            _navigateToHomeFreeTrial.value = Event(true)
    }

    fun onChooseLimitButtonClick() {
        _openDialogEvent.value = Event(true)
    }

    fun onSaveButtonClick() {
        updateSMSSettings()
    }

    fun setLimit(selectedLimit: String) {
        _limit.value = selectedLimit
    }

    fun onCheckedChanged(checked: Boolean) {

        if (!checked) {
            _isAlertOn.value = "False"
            if (remainingSMS.value!!.toInt() > 5) {
                _openTurnOffSMSDialogEvent.value = Event(true)
            }
        } else {
            _isAlertOn.value = "True"
        }
    }

    private fun getSMSSettings() {
        viewModelScope.launch {
            try {
                val response = repository.getSMSSettings()
                //if (response.statusCode == "0"){
                val data = response.data?.get(0)
                _totalSMS.value = data?.totalSMSAmount
                _remainingSMS.value = data?.remainingSMSAmount
                _limit.value = data?.dailySmsLimit
                _isAlertOn.value = data?.smsAlertOn

                _totalProgress.value = remainingSMS.value?.toInt()
                _maxProgress.value = totalSMS.value?.toInt()
                // }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateSMSSettings() {
        _isDataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateSMSSettings(
                        dailyLimit = limit.value?.toInt(),
                        alertOn = if (isAlertOn.value!!.equalIgnoreCase("True")) 1 else 0
                )
                _showToastMessage.value = Event(response.message as String)
                _isDataLoading.value = false
            } catch (e:Exception){
               e.printStackTrace()
            }

        }
    }

}
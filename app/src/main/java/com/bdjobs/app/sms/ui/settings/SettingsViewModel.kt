package com.bdjobs.app.sms.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.videoInterview.util.Event

class SettingsViewModel(private val repository : SMSRepository) : ViewModel() {

    private val _limit = MutableLiveData<String>().apply {
        value = "3"
    }
    val limit : LiveData<String> = _limit

    private val _remainingSMS = MutableLiveData<Int>().apply {
        value = 15
    }
    val remainingSMS : LiveData<Int> = _remainingSMS

    private val _totalSMS = MutableLiveData<Int>().apply {
        value = 20
    }
    val totalSMS = _totalSMS


    private val _navigateToHome = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val navigateToHome : LiveData<Event<Boolean>> = _navigateToHome

    private val _openDialogEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val openDialogEvent : LiveData<Event<Boolean>> = _openDialogEvent

    private val _openTurnOffSMSDialogEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val openTurnOffSMSDialogEvent : LiveData<Event<Boolean>> = _openTurnOffSMSDialogEvent


    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading : LiveData<Boolean> = _isDataLoading


    fun onViewSMSJobAlertButtonClick(){
        _navigateToHome.value = Event(true)
    }

    fun onChooseLimitButtonClick(){
        _openDialogEvent.value = Event(true)
    }

    fun onSaveButtonClick(){
        _isDataLoading.value = true
    }

    fun setLimit(selectedLimit : String) {
        _limit.value = selectedLimit
    }

    fun onCheckedChanged(checked : Boolean){
        if (!checked){
            if (remainingSMS.value!! > 5){
                _openTurnOffSMSDialogEvent.value = Event(true)
            }
        }
    }



}
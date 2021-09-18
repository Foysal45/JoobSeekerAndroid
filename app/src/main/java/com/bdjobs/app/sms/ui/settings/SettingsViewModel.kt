package com.bdjobs.app.sms.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.videoInterview.util.Event
import kotlinx.coroutines.launch
import kotlin.math.ceil

class SettingsViewModel(private val repository: SMSRepository) : ViewModel() {

    private val _limit = MutableLiveData<String?>().apply {
        value = "1"
    }
    val limit: LiveData<String?> = _limit

    private val _remainingSMS = MutableLiveData<String?>().apply {
        value = "0"
    }
    val remainingSMS: LiveData<String?> = _remainingSMS

    private val _totalSMS = MutableLiveData<String?>().apply {
        value = "0"
    }
    val totalSMS: LiveData<String?> = _totalSMS

    private val _probableRemainingDays = MutableLiveData<String>().apply { value = "0" }
    val probableRemainingDays : LiveData<String> = _probableRemainingDays

    private val _isAlertOn = MutableLiveData<String?>()
    val isAlertOn: LiveData<String?> = _isAlertOn

    private val _isMatchedJobSubscribed = MutableLiveData<String>()
    val isMatchedJobSubscribed : LiveData<String> = _isMatchedJobSubscribed

    private val _isFollowedEmpSubscribed = MutableLiveData<String>()
    val isFollowedEmpSubscribed : LiveData<String> = _isFollowedEmpSubscribed

    private val _isFavSearchSubscribed = MutableLiveData<String>()
    val isFavSearchSubscribed : LiveData<String> = _isFavSearchSubscribed

    private val _maxProgress = MutableLiveData<Int>()
    val maxProgress: LiveData<Int> = _maxProgress

    private val _totalProgress = MutableLiveData<Int>()
    val totalProgress: LiveData<Int> = _totalProgress


    private val _navigateToHome = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val navigateToHome: LiveData<Event<Boolean>> = _navigateToHome

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

    private val _isSubmitDataLoading = MutableLiveData<Boolean>()
    val isSubmitDataLoading : LiveData<Boolean> =_isSubmitDataLoading

    private val _showToastMessage = MutableLiveData<String?>()
    val showToastMessage : LiveData<String?> = _showToastMessage

    private val _statusCode = MutableLiveData<String>()
    val statusCode : LiveData<String> = _statusCode

    var availedSMS = MutableLiveData<Int>()

    fun onViewSMSJobAlertButtonClick() {
            _navigateToHome.value = Event(true)
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

        try {
            if (!checked) {
                _isAlertOn.value = "False"
                if (remainingSMS.value!!.toInt() > 5) {
                    _openTurnOffSMSDialogEvent.value = Event(true)
                }
            } else {
                _isAlertOn.value = "True"
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

    }

    fun onCheckedChangedMatchedJob(checked: Boolean) {

        try {
            if (!checked) {
                _isMatchedJobSubscribed.value = "False"
            } else {
                _isMatchedJobSubscribed.value = "True"
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

    }

    fun onCheckedChangedFavSearch(checked: Boolean) {

        try {
            if (!checked) {
                _isFavSearchSubscribed.value = "False"
            } else {
                _isFavSearchSubscribed.value = "True"
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

    }

    fun onCheckedChangedFollowedEmp(checked: Boolean) {

        try {
            if (!checked) {
                _isFollowedEmpSubscribed.value = "False"
            } else {
                _isFollowedEmpSubscribed.value = "True"
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

    }

    fun getSMSSettings() {
        _isDataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getSMSSettings()
                val data = response.data?.get(0)
                _isDataLoading.value = false
                _totalSMS.value = data?.totalSMSAmount
                _remainingSMS.value = data?.remainingSMSAmount
                _probableRemainingDays.value = data?.probableRemainingDay
                _limit.value = data?.dailySmsLimit
                _isAlertOn.value = data?.smsAlertOn
                _isMatchedJobSubscribed.value = data?.isMatchedJobEnable?:"True"
                _isFavSearchSubscribed.value = data?.isFavSearchEnable?:"False"
                _isFollowedEmpSubscribed.value = data?.isFollowedEmployerEnable?:"False"

                try {
                    availedSMS.value = _totalSMS.value!!.toInt() - _remainingSMS.value!!.toInt()
                } catch (e:Exception){
                    e.printStackTrace()
                }

                _totalProgress.value = availedSMS.value?.toInt()
                _maxProgress.value = totalSMS.value?.toInt()

                _statusCode.value = response.statuscode!!

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateSMSSettings() {
        _isSubmitDataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateSMSSettings(
                        dailyLimit = limit.value?.toInt(),
                        alertOn = if (isAlertOn.value!!.equalIgnoreCase("True")) 1 else 0,
                    isMatchedJobEnable = isMatchedJobSubscribed.value,
                    isFollowedEmployerEnable = isFollowedEmpSubscribed.value,
                    isFavSearchEnable = if (isFavSearchSubscribed.value!!.equalIgnoreCase("True")) 1 else 0
                )
                _showToastMessage.value = response.message.toString()
                _isSubmitDataLoading.value = false

                if (response.statuscode=="0") {
                    val probableRemainingDays = ceil(_remainingSMS.value?.toDouble() !!.div(_limit.value!!.toDouble())).toInt()

                    _probableRemainingDays.value = probableRemainingDays.toString()
                }

            } catch (e:Exception){
               e.printStackTrace()
            }

        }
    }

}
package com.bdjobs.app.liveInterview.ui.interview_session

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.videoInterview.util.Event
import kotlinx.coroutines.launch
import timber.log.Timber

//
// Created by Soumik on 5/8/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 BDJobs.com Ltd. All rights reserved.
//

class InterviewSessionViewModel(
        val repository: LiveInterviewRepository,
        val processID: String?,
        val applyID: String?) : ViewModel() {


    private var timer: CountDownTimer? = null

    private val _isNetworkAvailable = MutableLiveData<Boolean>()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    private val _isWaitingForEmployers = MutableLiveData<Boolean>()
    val isWaitingForEmployers: LiveData<Boolean> = _isWaitingForEmployers

    private val _isShowReadyView = MutableLiveData<Boolean>()
    val isShowReadyView: LiveData<Boolean> = _isShowReadyView

    private val _isShowBottomOptionView = MutableLiveData<Boolean>()
    val isShowBottomOptionView: LiveData<Boolean> = _isShowBottomOptionView

    private val _isShowLoadingCounter = MutableLiveData<Boolean>()
    val isShowLoadingCounter: LiveData<Boolean> = _isShowLoadingCounter

    private var _countDownFinish = MutableLiveData<Boolean>()
    val countDownFinish: LiveData<Boolean> = _countDownFinish

    private val _isShowParentReadyView = MutableLiveData<Boolean>()
    val isShowParentReadyView: LiveData<Boolean> = _isShowParentReadyView

    private val _isShowInterviewRoomView = MutableLiveData<Boolean>()
    val isShowInterviewRoomView: LiveData<Boolean> = _isShowInterviewRoomView

    val yesButtonClickedEvent = MutableLiveData<Event<Boolean>>()
    val yesClick = MutableLiveData<Boolean>()

    val noButtonClickedEvent = MutableLiveData<Event<Boolean>>()
    val noClick = MutableLiveData<Boolean>()

    val toggleAudioClickEvent = MutableLiveData<Event<Boolean>>()
    val toggleVideoClickEvent = MutableLiveData<Event<Boolean>>()

    val messageButtonClickEvent = MutableLiveData<Event<Boolean>>()

    val instructionButtonClickEvent = MutableLiveData<Event<Boolean>>()

    val waitingText = MutableLiveData<String>()


    private var _counterText = MutableLiveData<String>()
    val counterText: LiveData<String>
        get() = _counterText

    var onChatReceived = MutableLiveData<Array<Any?>?>()

    init {
        parentReadyViewCheck(true)
        bottomOptionShowCheck(true)
        readyCheck(true)
        yesClick.value = true
        noClick.value = false
        _counterText.value = "5"
    }

    fun networkCheck(isAvailable: Boolean) {
        if (isAvailable) {
            _isNetworkAvailable.postValue(true)
            bottomOptionShowCheck(true)
        } else {
            _isNetworkAvailable.postValue(false)
            bottomOptionShowCheck(false)
        }
    }

    fun waitingCheck(isWaiting: Boolean) {
        _isWaitingForEmployers.postValue(isWaiting)
//        readyCheck(false)
    }

    fun readyCheck(isReady: Boolean) {
        _isShowReadyView.postValue(isReady)
    }

    fun bottomOptionShowCheck(isShow: Boolean) {
        _isShowBottomOptionView.postValue(isShow)
    }

    fun loadingCounterShowCheck(isShow: Boolean) {
        _isShowLoadingCounter.postValue(isShow)
        startLoadingTimer()
        if (isShow) {
            bottomOptionShowCheck(false)
            waitingCheck(false)
        }
    }

    fun parentReadyViewCheck(isShow: Boolean) {
        _isShowParentReadyView.postValue(isShow)
//        interviewRoomViewCheck(false)
    }

    fun interviewRoomViewCheck(isShow: Boolean) {
        _isShowInterviewRoomView.postValue(isShow)
        parentReadyViewCheck(false)
        loadingCounterShowCheck(false)
    }

    fun isCountDownFinished(value: Boolean) {
        _countDownFinish.value = value

        if (value) {

            interviewRoomViewCheck(true)
        }
    }


    fun onYesButtonClicked() {
        yesButtonClickedEvent.value = Event(true)
        yesClick.value = true
        noClick.value = false
        applicantStatusUpdate("1")
    }

    fun onNoButtonClicked() {
        noButtonClickedEvent.value = Event(true)
        noClick.value = true
        yesClick.value = false
        applicantStatusUpdate("2")
//        loadingCounterShowCheck(true)
    }

    fun onMessageButtonClicked() {
        messageButtonClickEvent.value = Event(true)
    }

    fun onInstructionButtonClicked() {
        instructionButtonClickEvent.value = Event(true)
    }

    fun onToggleAudioClicked() {
        toggleAudioClickEvent.value = Event(true)
    }

    fun onToggleVideoClicked() {
        toggleVideoClickEvent.value = Event(true)
    }

    private fun startLoadingTimer() {
        var totalSeconds = 5
        timer = object : CountDownTimer(WAITING_TIME, 1000) {
            override fun onFinish() {
                isCountDownFinished(true)
            }

            override fun onTick(millisUntilFinished: Long) {
                totalSeconds--
                _counterText.value = "$totalSeconds"
            }

        }.start()
    }

    private fun applicantStatusUpdate(status: String) {
        viewModelScope.launch {
            try {
                val response = repository.applicantStatus(applyID, processID, status)

                if (response.statuscode == "4") {
                    Timber.d("Applicant status updated")
                    waitingText.value = response.message
                    waitingCheck(true)
                } else {
                    Timber.d("Response status code: ${response.statuscode}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("Exception while updating application status: ${e.localizedMessage}")
            }
        }
    }

    override fun onCleared() {
        timer?.cancel()
        super.onCleared()
    }

    companion object {
        const val WAITING_TIME = 5000L
    }

}
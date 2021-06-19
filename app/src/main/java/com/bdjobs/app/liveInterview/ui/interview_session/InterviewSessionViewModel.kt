package com.bdjobs.app.liveInterview.ui.interview_session

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.liveInterview.data.models.ChatLogModel
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

    private val _isShowActionView = MutableLiveData<Boolean>()
    val isShowActionView: LiveData<Boolean> = _isShowActionView

    private val _isShowFeedbackView = MutableLiveData<Boolean>()
    val isShowFeedbackView: LiveData<Boolean> = _isShowFeedbackView

    private val _isShowChatView = MutableLiveData<Boolean>()
    val isShowChatView: LiveData<Boolean> = _isShowChatView

    val yesButtonClickedEvent = MutableLiveData<Event<Boolean>>()
    val yesClick = MutableLiveData<Boolean>()

    val noButtonClickedEvent = MutableLiveData<Event<Boolean>>()
    val noClick = MutableLiveData<Boolean>()

    val toggleAudioClickEvent = MutableLiveData<Event<Boolean>>()
    val toggleVideoClickEvent = MutableLiveData<Event<Boolean>>()


    val messageButtonClickEvent = MutableLiveData<Event<Boolean>>()
    val messageButtonClicked = MutableLiveData<Boolean>()

    val instructionButtonClickEvent = MutableLiveData<Event<Boolean>>()

    val receivedChatData = MutableLiveData<Array<Any?>?>()

    fun receivedData(args:Array<Any?>?) {
        receivedChatData.postValue(args)
    }

    //Chat
    private var _counterText = MutableLiveData<String>()
    val counterText: LiveData<String>
        get() = _counterText

    var onChatReceived = MutableLiveData<Array<Any?>?>()
    private val _welcomeText = MutableLiveData<String>()
    val welcomeText: LiveData<String>
        get() = _welcomeText

    private val _anotherInterviewText = MutableLiveData<String>()
    val anotherInterviewText: LiveData<String>
        get() = _anotherInterviewText

    private val _waitingText = MutableLiveData<String>()
    val waitingText: LiveData<String>
        get() = _waitingText

    private val _askedText = MutableLiveData<String>()
    val askedText: LiveData<String>
        get() = _askedText

    private val _logLoading = MutableLiveData<Boolean> ()
    val logLoading : LiveData<Boolean> = _logLoading

    private val _chatLogFetchSuccess = MutableLiveData<Boolean>()
    val chatLogFetchSuccess : LiveData<Boolean> = _chatLogFetchSuccess

    private val _chatLogData = MutableLiveData<ChatLogModel?>()
    val chatLogData : LiveData<ChatLogModel?> = _chatLogData

    private val _chatLogFetchError = MutableLiveData<String?>()
    val chatLogFetchError : LiveData<String?> = _chatLogFetchError


    private val _postLoading = MutableLiveData<Boolean> ()
    val postLoading : LiveData<Boolean> = _postLoading

    private val _postSuccess = MutableLiveData<Boolean> ()
    val postSuccess : LiveData<Boolean> = _postSuccess

    private val _postError = MutableLiveData<String> ()
    val postError: LiveData<String> = _postError

    private val _postMessage = MutableLiveData<String> ()
    val postMessage: LiveData<String> = _postMessage

    val sendButtonClickEvent = MutableLiveData<Event<Boolean>> ()

    val welcomeTextShow = MutableLiveData<Boolean>()
    val anotherInterviewTextShow = MutableLiveData<Boolean>()
    val waitingTextShow = MutableLiveData<Boolean>()
    val askedTextShow = MutableLiveData<Boolean>()

    init {
        bottomOptionShowCheck(true)
        readyCheck(true)
        yesClick.value = true
        noClick.value = false
        _counterText.value = "5"
        fetchChatLog()
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
    }

    fun readyCheck(isReady: Boolean) {
        _isShowReadyView.postValue(isReady)
    }

    fun bottomOptionShowCheck(isShow: Boolean) {
        _isShowBottomOptionView.postValue(isShow)
    }

    fun actionShowCheck(isShow: Boolean) {
        _isShowActionView.postValue(isShow)
    }

    fun loadingCounterShowCheck(isShow: Boolean) {
        _isShowLoadingCounter.postValue(isShow)
        startLoadingTimer()
        if (isShow) {
            bottomOptionShowCheck(false)
            waitingCheck(false)
        }
    }

    fun interviewRoomViewCheck(isShow: Boolean) {
        _isShowActionView.postValue(isShow)
        if (isShow) {
            loadingCounterShowCheck(false)
            bottomOptionShowCheck(false)
            waitingCheck(false)
        }
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

    fun onShowChatViewClicked(){
        _isShowChatView.value = true
        interviewRoomViewCheck(false)
        actionShowCheck(false)
    }

    fun hideChatView(){
        interviewRoomViewCheck(true)
        actionShowCheck(true)
    }

    fun onNoButtonClicked() {
        noButtonClickedEvent.value = Event(true)
        noClick.value = true
        yesClick.value = false
        applicantStatusUpdate("2")
    }

    fun onMessageButtonClicked() {
        Timber.tag("live").d("view model onMessageButtonClicked")
        messageButtonClickEvent.value = Event(true)
        onShowChatViewClicked()
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
                 //   waitingText.value = response.message
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

    fun sendButtonClickedEvent() {
        sendButtonClickEvent.value = Event(true)
    }

    fun postChatMessage(message:String) {
        _postLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.postChatMessage(processID,message,"A","0","0")
                _postLoading.value = false

                if (response.statuscode=="4") {
                    _postMessage.value = message
                    _postSuccess.value = true
                }
            } catch (e:Exception) {
                _postLoading.value = false
                e.printStackTrace()
                Timber.e("Exception: ${e.localizedMessage}")
                _postError.value = e.localizedMessage
            }
        }
    }

    private fun fetchChatLog() {
        _logLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.chatLog(processID)

                _logLoading.value = false

                if (response.Message=="Success") {
                    _chatLogData.value = response
                    _chatLogFetchSuccess.value = true
                }

                val data = chatLogData.value?.arrChatdata
                if (data!!.isNotEmpty()) {
                    for (i in data.indices) {

                        if (data[i]?.chatText != null && data[i]?.chatText!!.isNotEmpty()) {
                            when (data[i]?.hostType) {
                                "AC" -> {
                                    _welcomeText.value = data[i]?.chatText!!
                                    welcomeTextShow.value = true
                                }
                                "AU" -> {
                                    _waitingText.value = data[i]?.chatText!!
                                    waitingTextShow.value = true
                                }
                                "AA" -> {
                                    _anotherInterviewText.value = data[i]?.chatText!!
                                    anotherInterviewTextShow.value = true
                                }
                                "AL" -> {
                                    _askedText.value = data[i]?.chatText!!
                                    askedTextShow.value = true
                                }
                            }
                        }
                        else {
                            welcomeTextShow.value = false
                            anotherInterviewTextShow.value = false
                            askedTextShow.value = false
                            waitingTextShow.value = false
                        }
                    }
                }
            } catch (e:Exception) {
                _logLoading.value = false
                welcomeTextShow.value = false
                anotherInterviewTextShow.value = false
                askedTextShow.value = false
                waitingTextShow.value = false
                e.printStackTrace()
                Timber.e("Exception: ${e.localizedMessage}")
                _chatLogFetchError.value = e.localizedMessage
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
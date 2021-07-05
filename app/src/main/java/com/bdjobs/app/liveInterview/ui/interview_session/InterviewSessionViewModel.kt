package com.bdjobs.app.liveInterview.ui.interview_session

import android.os.CountDownTimer
import android.text.Editable
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
        val applyID: String?,
        val jobID: String?) : ViewModel() {



    private val _isNetworkAvailable = MutableLiveData<Boolean>()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    private val _canGoToDetailView = MutableLiveData<Boolean>()
    val canGoToDetailView: LiveData<Boolean> = _canGoToDetailView

    //Ready View
    private val _isReadyViewVisible = MutableLiveData<Boolean>()
    val isReadyViewVisible: MutableLiveData<Boolean> = _isReadyViewVisible

    private val _isReadyViewHidden = MutableLiveData<Boolean>()
    val isReadyViewHidden: MutableLiveData<Boolean> = _isReadyViewHidden

    private val _isEmployerArrived = MutableLiveData<Boolean>()
    val isEmployerArrived: MutableLiveData<Boolean> = _isEmployerArrived

    val yesButtonClickedEvent = MutableLiveData<Event<Boolean>>()
    val isYesButtonClicked = MutableLiveData<Boolean>()

    val noButtonClickedEvent = MutableLiveData<Event<Boolean>>()
    val isNoButtonClicked = MutableLiveData<Boolean>()

    val applicantStatusText = MutableLiveData<String>().apply { value = ""}

    val chatButtonClickEvent = MutableLiveData<Event<Boolean>> ()

    private val _isChatViewShowing = MutableLiveData<Boolean>()
    val isChatViewShowing: MutableLiveData<Boolean> = _isChatViewShowing

    private val _isChatViewHidden = MutableLiveData<Boolean>()
    val isChatViewHidden: MutableLiveData<Boolean> = _isChatViewHidden

    private var _messageCountText = MutableLiveData<String>().apply { value = "0"}
    val messageCountText: MutableLiveData<String> get() = _messageCountText

    val instructionButtonClickEvent = MutableLiveData<Event<Boolean>>()

    private val _isInstructionVisible = MutableLiveData<Boolean>()
    val isInstructionVisible: MutableLiveData<Boolean> = _isInstructionVisible

    private val _isInstructionHidden = MutableLiveData<Boolean>()
    val isInstructionHidden: MutableLiveData<Boolean> = _isInstructionHidden

    //CountDown
    private var timer: CountDownTimer? = null

    private val _isCountDownVisible = MutableLiveData<Boolean>()
    val isCountDownVisible: MutableLiveData<Boolean> = _isCountDownVisible

    private var _countDownFinish = MutableLiveData<Boolean>()
    val countDownFinish: MutableLiveData<Boolean> = _countDownFinish

    private var _countDownText = MutableLiveData<String>()
    val countDownText: MutableLiveData<String> get() = _countDownText

    //Ongoing Interview
    private val _isOngoingInterviewVisible = MutableLiveData<Boolean>()
    val isOngoingInterviewVisible: MutableLiveData<Boolean> = _isOngoingInterviewVisible

    private val _isOngoingInterviewHidden = MutableLiveData<Boolean>()
    val isOngoingInterviewHidden: MutableLiveData<Boolean> = _isOngoingInterviewHidden

    val toggleAudioClickEvent = MutableLiveData<Event<Boolean>>()

    private val _isAudioOn = MutableLiveData<Boolean>()
    val isAudioOn: MutableLiveData<Boolean> = _isAudioOn

    private val _isAudioOff = MutableLiveData<Boolean>()
    val isAudioOff: MutableLiveData<Boolean> = _isAudioOff

    val toggleVideoClickEvent = MutableLiveData<Event<Boolean>>()

    private val _isVideoOn = MutableLiveData<Boolean>()
    val isVideoOn: MutableLiveData<Boolean> = _isVideoOn

    private val _isVideoOff = MutableLiveData<Boolean>()
    val isVideoOff: MutableLiveData<Boolean> = _isVideoOff

    val onGoingChatButtonClickEvent = MutableLiveData<Event<Boolean>> ()

    private val _isOnGoingChatViewShowing = MutableLiveData<Boolean>()
    val isOnGoingChatViewShowing: MutableLiveData<Boolean> = _isOnGoingChatViewShowing

    private val _isOnGoingChatViewHidden = MutableLiveData<Boolean>()
    val isOnGoingChatViewHidden: MutableLiveData<Boolean> = _isOnGoingChatViewHidden

    private var _onGoingMessageCountText = MutableLiveData<String>().apply { value = "0"}
    val onGoingMessageCountText: MutableLiveData<String> get() = _onGoingMessageCountText

    private val _isFeedbackViewShowing = MutableLiveData<Boolean>()
    val isFeedbackViewShowing: MutableLiveData<Boolean> = _isFeedbackViewShowing

    private val _isFeedbackViewHidden = MutableLiveData<Boolean>()
    val isFeedbackViewHidden: MutableLiveData<Boolean> = _isFeedbackViewHidden


    //Chat
    var onChatReceived = MutableLiveData<Array<Any?>?>()
    fun receivedChatData(args:Array<Any?>?) { onChatReceived.postValue(args) }

    private val _welcomeText = MutableLiveData<String>()
    val welcomeText: LiveData<String> get() = _welcomeText

    private val _anotherInterviewText = MutableLiveData<String>()
    val anotherInterviewText: LiveData<String> get() = _anotherInterviewText

    private val _waitingText = MutableLiveData<String>()
    val waitingText: LiveData<String> get() = _waitingText

    private val _askedText = MutableLiveData<String>()
    val askedText: LiveData<String> get() = _askedText

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

    val sendMessageClickEvent = MutableLiveData<Event<Boolean>> ()

    val welcomeTextShow = MutableLiveData<Boolean>()
    val anotherInterviewTextShow = MutableLiveData<Boolean>()
    val waitingTextShow = MutableLiveData<Boolean>()
    val askedTextShow = MutableLiveData<Boolean>()

    //feedback
    val feedback = MutableLiveData<String>().apply { value = ""}
    val rating = MutableLiveData<Int>().apply { value = 0 }
    val enableSubmitFeedbackButton = MutableLiveData<Boolean>().apply { value = false }
    val submitFeedbackClickEvent = MutableLiveData<Event<Boolean>> ()
    val isFeedbackSubmitted = MutableLiveData<Boolean>().apply { value = false }

    val onMessageToEmployerClickEvent = MutableLiveData<Event<Boolean>> ()

    private val _isMessageToEmployerShowing = MutableLiveData<Boolean>()
    val isMessageToEmployerShowing: MutableLiveData<Boolean> = _isMessageToEmployerShowing

    private val _isMessageToEmployerHidden = MutableLiveData<Boolean>()
    val isMessageToEmployerHidden: MutableLiveData<Boolean> = _isMessageToEmployerHidden


    //First View
    init {
        _isReadyViewVisible.postValue(true)
        _isReadyViewHidden.postValue(false)

        _isInstructionHidden.postValue(true)
        _isChatViewHidden.postValue(true)

        _isCountDownVisible.postValue(false)
        _isOngoingInterviewHidden.postValue(true)
        _isOnGoingChatViewHidden.postValue(true)

        _isFeedbackViewHidden.postValue(true)
        _isMessageToEmployerHidden.postValue(true)

        isYesButtonClicked.value = true
        isNoButtonClicked.value = false
        fetchChatLog()
    }

    fun networkCheck(isAvailable: Boolean) {
        if (isAvailable) {
            _isNetworkAvailable.postValue(true)
        } else {
            _isNetworkAvailable.postValue(false)
        }
    }

    fun canGoBackToDetailFragment(){
        if(isChatViewShowing.value == true){

            _isChatViewShowing.postValue(false)
            _isChatViewHidden.postValue(true)

            _isReadyViewVisible.postValue(true)
            _isReadyViewHidden.postValue(false)

            _canGoToDetailView.postValue(false)
        }

        else if(isInstructionVisible.value == true){
            _isInstructionVisible.postValue(false)
            _isInstructionHidden.postValue(true)

            _isReadyViewVisible.postValue(true)
            _isReadyViewHidden.postValue(false)

            _canGoToDetailView.postValue(false)

        }

        else if(isOnGoingChatViewShowing.value == true){
            _isOnGoingChatViewShowing.postValue(false)
            _isOnGoingChatViewHidden.postValue(true)

            _isOngoingInterviewVisible.postValue(true)
            _isOngoingInterviewHidden.postValue(false)

            _canGoToDetailView.postValue(false)

        }

        else if(isMessageToEmployerShowing.value == true){
            _isMessageToEmployerShowing.postValue(false)
            _isMessageToEmployerHidden.postValue(true)

            _isFeedbackViewShowing.postValue(true)
            _isFeedbackViewHidden.postValue(false)

            _canGoToDetailView.postValue(false)

        }

        else _canGoToDetailView.postValue(true)
    }

    /* Ready View Start */

    fun onYesButtonClicked() {
        yesButtonClickedEvent.value = Event(true)
        isYesButtonClicked.value = true
        isNoButtonClicked.value = false
        applicantStatusUpdate("1")
    }

    fun onNoButtonClicked() {
        noButtonClickedEvent.value = Event(true)
        isNoButtonClicked.value = true
        isYesButtonClicked.value = false
        applicantStatusUpdate("2")
    }

    fun onChatButtonClicked() {
        chatButtonClickEvent.value = Event(true)

        _isChatViewShowing.postValue(true)
        _isChatViewHidden.postValue(false)

        _isReadyViewVisible.postValue(false)
        _isReadyViewHidden.postValue(true)
    }

    fun onInstructionButtonClicked() {
        _isInstructionVisible.postValue(true)
        _isInstructionHidden.postValue(false)

        _isReadyViewVisible.postValue(false)
        _isReadyViewHidden.postValue(true)
    }

    private fun applicantStatusUpdate(status: String) {
        viewModelScope.launch {
            try {
                val response = repository.applicantStatus(applyID, processID, status)

                if (response.statuscode == "4") {
                    Timber.d("Applicant status updated")
                    applicantStatusText.value = response.message
                } else {
                    Timber.d("Response status code: ${response.statuscode}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("Exception while updating application status: ${e.localizedMessage}")
            }
        }
    }
    /* Ready View End */

    /* CountDown View Start */
    companion object { const val WAITING_TIME = 5000L }

    fun showCountDown() {
        _isReadyViewVisible.postValue(false)
        _isReadyViewHidden.postValue(true)

        _isOngoingInterviewVisible.postValue(false)
        _isOngoingInterviewHidden.postValue(true)

        _isCountDownVisible.postValue(true)
        var totalSeconds = 5
        timer = object : CountDownTimer(WAITING_TIME, 1000) {
            override fun onFinish() {
                isCountDownFinished(true)
            }
            override fun onTick(millisUntilFinished: Long) {
                totalSeconds--
                _countDownText.value = "$totalSeconds"
            }

        }.start()
    }

    private fun isCountDownFinished(value: Boolean) {
        _countDownFinish.value = value
        if (value) {
            _isCountDownVisible.postValue(false)

            _isOngoingInterviewVisible.postValue(true)
            _isOngoingInterviewHidden.postValue(false)

            _isAudioOn.postValue(true)
            _isVideoOn.postValue(true)

            _isInstructionVisible.postValue(false)
            _isInstructionHidden.postValue(true)

            _isChatViewShowing.postValue(false)
            _isChatViewHidden.postValue(true)

        }
    }

    override fun onCleared() {
        timer?.cancel()
        super.onCleared()
    }
    /* CountDown View End */

    /* Multipeer View Start */
    fun applicantJoinedOnGoingSession(){
        _isReadyViewVisible.postValue(false)
        _isReadyViewHidden.postValue(true)

        _isCountDownVisible.postValue(false)

        _isOngoingInterviewVisible.postValue(true)
        _isOngoingInterviewHidden.postValue(false)

        _isAudioOn.postValue(true)
        _isVideoOn.postValue(true)

        _isInstructionVisible.postValue(false)
        _isInstructionHidden.postValue(true)

        _isChatViewShowing.postValue(false)
        _isChatViewHidden.postValue(true)
    }

    fun onToggleAudioButtonClicked() {
        if(isAudioOff.value == true){
            _isAudioOff.postValue(false)
            _isAudioOn.postValue(true)
        }
        else if(isAudioOn.value == true){
            _isAudioOff.postValue(true)
            _isAudioOn.postValue(false)
        }
    }

    fun onToggleVideoButtonClicked() {
        if(isVideoOff.value == true){
            _isVideoOff.postValue(false)
            _isVideoOn.postValue(true)
        }
        else if(_isVideoOn.value == true){
            _isVideoOff.postValue(true)
            _isVideoOn.postValue(false)
        }
    }

    fun onGoingChatButtonClicked() {
        onGoingChatButtonClickEvent.value = Event(true)

        _isOnGoingChatViewShowing.postValue(true)
        _isOnGoingChatViewHidden.postValue(false)

        _isOngoingInterviewVisible.postValue(false)
        _isOngoingInterviewHidden.postValue(true)
    }
    /* Multipeer View End */

    /* Chat View Start */
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
                                "RC" -> {
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
                Timber.e("Exception: ${e.localizedMessage}")
                _chatLogFetchError.value = e.localizedMessage
                e.printStackTrace()
            }
        }
    }

    fun newMessageArrived(){

    }
    fun sendMessageButtonClicked(){
        sendMessageClickEvent.value = Event(true)
    }

    fun updateMessageCounter(count:Int){
        _messageCountText.value = count.toString()
        _onGoingMessageCountText.value = count.toString()
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
                Timber.e("Exception: ${e.localizedMessage}")
                _postError.value = e.localizedMessage
                e.printStackTrace()
            }
        }
    }
    /* Chat View End */

    /* Feedback View Start */
    fun employerEndedCall(){
        _isFeedbackViewShowing.postValue(true)

        _isOngoingInterviewVisible.postValue(false)
        _isOngoingInterviewHidden.postValue(true)
    }

    fun onRatingChanged() {
        Timber.tag("live").d("onRatingChanged called")
        checkValidation()
    }

    fun afterFeedbackTextChanged(editable: Editable) {
        Timber.tag("live").d("afterFeedbackTextChanged called")
        checkValidation()
    }

    fun checkValidation() {
        enableSubmitFeedbackButton.value = !feedback.value.isNullOrBlank() && rating.value!!.toInt() > 0
    }

    fun onSubmitFeedbackButtonClick() {
        Timber.tag("live").d("onSubmitFeedbackButtonClick ${rating.value} ${feedback.value}")
        Timber.tag("live").d("onSubmitFeedbackButtonClick ApplyID: $applyID :: JobID: $jobID")

        viewModelScope.launch {
            val response = repository.submitVideoInterviewFeedback(
                applyId = applyID,
                jobId = jobID,
                feedbackComment = feedback.value,
                rating = rating.value.toString()
            )
            if(response.statuscode == "4"){
                Timber.tag("live").d("onSubmitFeedbackButtonClick isFeedbackSubmitted")
                isFeedbackSubmitted.postValue(true)
            }
        }
    }

    fun onMessageToEmployerButtonClicked() {
        onMessageToEmployerClickEvent.value = Event(true)

        _isMessageToEmployerShowing.postValue(true)
        _isMessageToEmployerHidden.postValue(false)

        _isFeedbackViewShowing.postValue(false)
        _isFeedbackViewHidden.postValue(true)
    }

    /* Feedback View End */

}
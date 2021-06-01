package com.bdjobs.app.liveInterview.ui.interview_session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.videoInterview.util.Event

//
// Created by Soumik on 5/8/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 BDJobs.com Ltd. All rights reserved.
//

class InterviewSessionViewModel : ViewModel() {

    private val _isNetworkAvailable = MutableLiveData<Boolean>()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    private val _isWaitingForEmployers = MutableLiveData<Boolean>()
    val isWaitingForEmployers : LiveData<Boolean> = _isWaitingForEmployers

    private val _isShowReadyView = MutableLiveData<Boolean> ()
    val isShowReadyView:LiveData<Boolean> = _isShowReadyView

    private val _isShowBottomOptionView = MutableLiveData<Boolean> ()
    val isShowBottomOptionView:LiveData<Boolean> = _isShowBottomOptionView

    private val _isShowLoadingCounter = MutableLiveData<Boolean> ()
    val isShowLoadingCounter:LiveData<Boolean> = _isShowLoadingCounter

    private val _isShowParentReadyView = MutableLiveData<Boolean> ()
    val isShowParentReadyView:LiveData<Boolean> = _isShowParentReadyView

    private val _isShowInterviewRoomView = MutableLiveData<Boolean> ()
    val isShowInterviewRoomView : LiveData<Boolean> = _isShowInterviewRoomView

    val yesButtonClickedEvent = MutableLiveData<Event<Boolean>> ()
    val yesClick = MutableLiveData<Boolean> ()

    val noButtonClickedEvent = MutableLiveData<Event<Boolean>> ()
    val noClick = MutableLiveData<Boolean> ()

    val toggleAudioClickEvent = MutableLiveData<Event<Boolean>> ()
    val toggleVideoClickEvent = MutableLiveData<Event<Boolean>> ()

    val messageButtonClickEvent = MutableLiveData<Event<Boolean>> ()

    val instructionButtonClickEvent = MutableLiveData<Event<Boolean>> ()

    init {
        parentReadyViewCheck(true)
        bottomOptionShowCheck(true)
        readyCheck(true)
        yesClick.value = true
        noClick.value = false
    }

    fun networkCheck(isAvailable:Boolean) {
        if (isAvailable) {
            _isNetworkAvailable.postValue(true)
            bottomOptionShowCheck(true)
        }
        else {
            _isNetworkAvailable.postValue(false)
            bottomOptionShowCheck(false)
        }
    }

    fun waitingCheck(isWaiting:Boolean) {
        _isWaitingForEmployers.postValue(isWaiting)
        readyCheck(false)
    }

    fun readyCheck(isReady:Boolean) {
        _isShowReadyView.postValue(isReady)
    }

    fun bottomOptionShowCheck(isShow:Boolean) {
        _isShowBottomOptionView.postValue(isShow)
    }

    fun loadingCounterShowCheck(isShow: Boolean) {
        _isShowLoadingCounter.postValue(isShow)
        bottomOptionShowCheck(false)
        waitingCheck(false)
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

    fun onYesButtonClicked() {
        yesButtonClickedEvent.value = Event(true)
        yesClick.value = true
        noClick.value= false
        waitingCheck(true)
    }

    fun onNoButtonClicked() {
        noButtonClickedEvent.value = Event(true)
        noClick.value = true
        yesClick.value = false
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


}
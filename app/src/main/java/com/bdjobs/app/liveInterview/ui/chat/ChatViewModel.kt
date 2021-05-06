package com.bdjobs.app.liveInterview.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.videoInterview.util.Event

class ChatViewModel : ViewModel() {

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


    val sendButtonClickEvent = MutableLiveData<Event<Boolean>> ()

    init {
        _welcomeText.value = "Welcome to the Live interview room."
        _anotherInterviewText.value = "Interviewer is taking another interview.\nPlease wait sometimes."
        _waitingText.value = "Please wait and take your preparation.\nThe interviewer will start the interview process soon."
        _askedText.value = "Interviewer asked for you. Enable Yes if your are ready.\nWrite message here to let him know that you are present now. [You can also call employer on the [given number].]"
    }

    fun sendButtonClickedEvent() {
        sendButtonClickEvent.value = Event(true)
    }
}
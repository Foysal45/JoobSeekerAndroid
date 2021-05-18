package com.bdjobs.app.liveInterview.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.liveInterview.data.models.ChatLogModel
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.videoInterview.util.Event
import kotlinx.coroutines.launch
import timber.log.Timber

class ChatViewModel(
        private val repository: LiveInterviewRepository,
        private val processID:String?
) : ViewModel() {

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

    val sendButtonClickEvent = MutableLiveData<Event<Boolean>> ()

    init {
//        _welcomeText.value = "Welcome to the Live interview room."
//        _anotherInterviewText.value = "Interviewer is taking another interview.\nPlease wait sometimes."
//        _waitingText.value = "Please wait and take your preparation.\nThe interviewer will start the interview process soon."
//        _askedText.value = "Interviewer asked for you. Enable Yes if your are ready.\nWrite message here to let him know that you are present now. [You can also call employer on the [given number].]"

        fetchChatLog()
    }

    fun sendButtonClickedEvent() {
        sendButtonClickEvent.value = Event(true)
    }

    private fun fetchChatLog() {
        _logLoading.value = true

        viewModelScope.launch {
            try {
                Timber.d("Process ID: $processID")
                val response = repository.chatLog(processID)

                Timber.d("Chat log response: $response")
                _logLoading.value = false
                _chatLogFetchSuccess.value = true
                _chatLogData.value = response

                val data = chatLogData.value?.arrChatdata
                if (data!!.isNotEmpty()) {
                    for (i in data.indices) {
                        if (data[i]?.hostType=="AC") _welcomeText.value = data[i]?.chatText!!
                        else if (data[i]?.hostType=="AU") _waitingText.value = data[i]?.chatText!!
                    }
                }


            } catch (e:Exception) {
                _logLoading.value = false
                e.printStackTrace()
                Timber.e("Exception: ${e.localizedMessage}")
                _chatLogFetchError.value = e.localizedMessage
            }
        }
    }
}
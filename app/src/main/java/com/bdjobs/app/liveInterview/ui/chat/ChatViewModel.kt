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


    private val _postLoading = MutableLiveData<Boolean> ()
    val postLoading : LiveData<Boolean> = _postLoading

    private val _postSuccess = MutableLiveData<Boolean> ()
    val postSuccess : LiveData<Boolean> = _postSuccess

    private val _postError = MutableLiveData<String> ()
    val postError: LiveData<String> = _postError

    private val _postMessage = MutableLiveData<String> ()
    val postMessage: LiveData<String> = _postMessage

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
                        when (data[i]?.hostType) {
                            "AC" -> _welcomeText.value = data[i]?.chatText!!
                            "AU" -> _waitingText.value = data[i]?.chatText!!
                            "AA" -> _anotherInterviewText.value = data[i]?.chatText!!
                            "AL" -> _askedText.value = data[i]?.chatText!!
                        }
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
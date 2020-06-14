package com.bdjobs.app.videoInterview.ui.question_list

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.videoInterview.data.models.AnswerManager
import com.bdjobs.app.videoInterview.data.models.VideoInterviewQuestionList
import com.bdjobs.app.videoInterview.data.models.VideoManager
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.util.Event
import kotlinx.coroutines.launch

class QuestionListViewModel(val videoInterviewRepository: VideoInterviewRepository) : ViewModel() {

    val _isNotInterestedToSubmitChecked = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isNotInterestedToSubmitChecked: LiveData<Boolean> = _isNotInterestedToSubmitChecked

    private val _onSubmitButtonClickEvent = MutableLiveData<Event<Boolean>>()
    val onSubmitButtonClickEvent: LiveData<Event<Boolean>> = _onSubmitButtonClickEvent

    private val _onNextQuestionClickEvent = MutableLiveData<Event<Boolean>>()
    val onNextQuestionClickEvent: LiveData<Event<Boolean>> = _onNextQuestionClickEvent

    private val _onPreviousQuestionClickEvent = MutableLiveData<Event<Boolean>>()
    val onPreviousQuestionClickEvent: LiveData<Event<Boolean>> = _onPreviousQuestionClickEvent

    private val _questionListData = MutableLiveData<List<VideoInterviewQuestionList.Data?>?>()
    val questionListData: LiveData<List<VideoInterviewQuestionList.Data?>?> = _questionListData

    private val _questionCommonData = MutableLiveData<VideoInterviewQuestionList.Common?>()
    val questionCommonData: LiveData<VideoInterviewQuestionList.Common?> = _questionCommonData

    val _videoManagerData = MutableLiveData<VideoManager?>()
    var videoManagerData: LiveData<VideoManager?> = _videoManagerData

    private val _shouldShowRemainingTime = MutableLiveData<Boolean>().apply {
        value = false
    }
    val shouldShowRemainingTime: LiveData<Boolean> = _shouldShowRemainingTime

    private val _remainingTimeInString = MutableLiveData<String>()
    val remainingTimeInString = _remainingTimeInString

    lateinit var answerManagerData: AnswerManager

    private lateinit var timer: CountDownTimer

    var secondsRemaining = 0L

    var minutes = 0L
    var seconds = 0L

    private val _shouldEnableSubmitButtonAfterTimerEnd = MutableLiveData<Boolean>().apply {
        value = false
    }
    val shouldEnableSubmitButtonAfterTimerEnd: LiveData<Boolean> = _shouldEnableSubmitButtonAfterTimerEnd

    private val _shouldShowOneHourInfo = MutableLiveData<Boolean>().apply {
        value = true
    }
    val shouldShowOneHourInfo: LiveData<Boolean> = _shouldShowOneHourInfo

    init {
        //getQuestionList()
    }

    fun getQuestionList(jobId: String?, applyId: String?) {
        viewModelScope.launch {
            try {
                val response = videoInterviewRepository.getQuestionListFromRemote(jobId, applyId)
                _questionListData.value = response.data
                _questionCommonData.value = response.common

                var remainingTime = "10" //response.common?.remaingTime!!

                if (remainingTime.toInt() > 0) {
                    _shouldShowRemainingTime.value = true
                    //_remainingTime.value = response.common?.remaingTime
                    startTimer(remainingTime)
                } else {
                    _shouldShowRemainingTime.value = false
                    //_remainingTime.value = response.common?.remaingTime
                }
            } catch (e: Exception) {

            }
        }
    }


    fun onPreviousQuestionClick() {
        _onPreviousQuestionClickEvent.value = Event(true)
    }

    fun onNextQuestionClick() {
        _onNextQuestionClickEvent.value = Event(true)
    }

    fun onSubmitButtonClick() {
        _onSubmitButtonClickEvent.value = Event(isNotInterestedToSubmitChecked.value!!)
    }

    fun submitAnswerToServer() {
        prepareAnswers()
        Log.d("rakib", "$answerManagerData")
        viewModelScope.launch {
            try {
                val response = videoInterviewRepository.submitAnswerToRemote(answerManagerData)
            } catch (e:Exception){

            }
        }
    }

    private fun prepareAnswers() {
        answerManagerData = AnswerManager(
                jobId = questionCommonData.value?.jobId,
                applyId = questionCommonData.value?.applyId,
                totalAnswerCount = questionCommonData.value?.vUserTotalAnswerequestion,
                type = if (isNotInterestedToSubmitChecked.value!!) "I" else "S"
        )
    }

    private fun startTimer(remainingSeconds: String) {

        _shouldShowOneHourInfo.value = false

        timer = object : CountDownTimer(remainingSeconds.toLong().times(1000), 1000) {
            override fun onFinish() {
                _shouldShowRemainingTime.value = false
                _shouldEnableSubmitButtonAfterTimerEnd.value = true
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000

                minutes = (secondsRemaining % 3600) / 60
                seconds = secondsRemaining % 60

                _remainingTimeInString.value = "$minutes min $seconds sec"
            }

        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
}
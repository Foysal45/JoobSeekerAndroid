package com.bdjobs.app.videoResume.ui.questions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.videoInterview.util.Event
import com.bdjobs.app.videoResume.data.models.VideoResumeManager
import com.bdjobs.app.videoResume.data.models.VideoResumeQuestionList
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository
import com.loopj.android.http.AsyncHttpClient
import kotlinx.coroutines.launch
import timber.log.Timber

class VideoResumeQuestionsViewModel(
         val videoResumeRepository: VideoResumeRepository
) : ViewModel()  {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _isVideoResumeVisible = MutableLiveData<Boolean>().apply { value = false }
    val isVideoResumeVisible: MutableLiveData<Boolean> = _isVideoResumeVisible

    private val _onNextQuestionClickEvent = MutableLiveData<Event<Boolean>>()
    val onNextQuestionClickEvent: LiveData<Event<Boolean>> = _onNextQuestionClickEvent

    private val _onPreviousQuestionClickEvent = MutableLiveData<Event<Boolean>>()
    val onPreviousQuestionClickEvent: LiveData<Event<Boolean>> = _onPreviousQuestionClickEvent

    private val _questionListData = MutableLiveData<List<VideoResumeQuestionList.Data?>?>()
    val questionListData: LiveData<List<VideoResumeQuestionList.Data?>?> = _questionListData

    val _videoResumeManagerData = MutableLiveData<VideoResumeManager?>()
    var videoResumeManagerData: LiveData<VideoResumeManager?> = _videoResumeManagerData

    val _selectedItemPosition = MutableLiveData<Int>().apply {
        value = 0
    }
    val selectedItemPosition : LiveData<Int> = _selectedItemPosition

    var seconds = 0L

    val navigateToFeedbackEvent = MutableLiveData<Event<Boolean>>()

    var showVideoResumeToEmployers = MutableLiveData<Boolean>()

    private val _totalAnswered = MutableLiveData<String?>().apply {
        value = "0"
    }
    val totalAnswered: LiveData<String?> = _totalAnswered

    private val _threshold = MutableLiveData<String?>().apply {
        value = "0"
    }
    val threshold: LiveData<String?> = _threshold

    private val _isAlertOn = MutableLiveData<String?>()
    val isAlertOn: LiveData<String?> = _isAlertOn

    init {
        Timber.d("init called")
    }

    fun getQuestions() {
        _dataLoading.value = true
        viewModelScope.launch {
            try {
                val response = videoResumeRepository.getQuestionListFromRemote()
                _questionListData.value = response.data
                _dataLoading.value = false
            } catch (e: Exception) {

            }
        }
    }

    fun getStats() {
        viewModelScope.launch {
            try {
                val response = videoResumeRepository.getStatisticsFromRemote()
                val data = response.data?.get(0)

                _totalAnswered.value = data?.totalAnswered
                _threshold.value = data?.threshold
                _isAlertOn.value =  data?.resumeVisibility
                _isVideoResumeVisible.value = data?.resumeVisibility == "1"

                showVideoResumeToEmployers.value = totalAnswered.value!!.toInt() >= threshold.value!!.toInt()


            } catch (e: Exception) {
                e.printStackTrace()
                AsyncHttpClient.log.d("Salvin", "Got exception")
            }
        }
    }


    fun onPreviousQuestionClick() {
        _onPreviousQuestionClickEvent.value = Event(true)
    }

    fun onNextQuestionClick() {
        _onNextQuestionClickEvent.value = Event(true)
    }
}
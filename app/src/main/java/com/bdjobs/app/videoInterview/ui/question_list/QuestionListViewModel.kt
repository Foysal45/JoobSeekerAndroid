package com.bdjobs.app.videoInterview.ui.question_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun onSubmitButtonClick() {
        _onSubmitButtonClickEvent.value = Event(isNotInterestedToSubmitChecked.value!!)
    }

    private val _questionListData = MutableLiveData<List<VideoInterviewQuestionList.Data?>?>()
    val questionListData : LiveData<List<VideoInterviewQuestionList.Data?>?> = _questionListData

    private val _questionCommonData = MutableLiveData<VideoInterviewQuestionList.Common?>()
    val questionCommonData : LiveData<VideoInterviewQuestionList.Common?> = _questionCommonData

    val _videoManagerData = MutableLiveData<VideoManager?>()
    var videoManagerData : LiveData<VideoManager?> = _videoManagerData

    fun onDialogYesButtonClick() {

    }

    init {
        //getQuestionList()
    }

    fun getQuestionList(a : String?, b : String?){
        viewModelScope.launch {
            try {
                val response = videoInterviewRepository.getQuestionListFromRemote(a,b)
                _questionListData.value = response.data
                _questionCommonData.value = response.common
            } catch (e:Exception){

            }
        }
    }

    fun onPreviousQuestionClick(){
        _onPreviousQuestionClickEvent.value = Event(true)
    }

    fun onNextQuestionClick(){
        _onNextQuestionClickEvent.value = Event(true)
    }
}
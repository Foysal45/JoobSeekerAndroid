package com.bdjobs.app.videoInterview.ui.question_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.util.Event
import com.bdjobs.app.videoInterview.util.EventObserver

class QuestionDetailsViewModel(val videoInterviewRepository: VideoInterviewRepository) : ViewModel() {

    val _isNotInterestedToSubmitChecked = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isNotInterestedToSubmitChecked: LiveData<Boolean> = _isNotInterestedToSubmitChecked

    private val _onSubmitButtonClickEvent = MutableLiveData<Event<Boolean>>()
    val onSubmitButtonClickEvent : LiveData<Event<Boolean>> = _onSubmitButtonClickEvent

    fun onSubmitButtonClick() {
        _onSubmitButtonClickEvent.value = Event(isNotInterestedToSubmitChecked.value!!)
    }

    fun onDialogYesButtonClick(){

    }
}
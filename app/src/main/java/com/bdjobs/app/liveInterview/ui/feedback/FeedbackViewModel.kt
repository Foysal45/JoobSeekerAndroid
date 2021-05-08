package com.bdjobs.app.liveInterview.ui.feedback

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.videoInterview.util.Event
import kotlinx.coroutines.launch
import timber.log.Timber

//
// Created by Soumik on 5/8/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 BDjobs.com Ltd. All rights reserved.
//

class FeedbackViewModel(val repository: LiveInterviewRepository,val applyID:String?,val jobID:String?) : ViewModel(){
    val feedback = MutableLiveData<String>().apply {
        value = ""
    }

    val rating = MutableLiveData<Int>().apply {
        value = 0
    }

    val enableSubmitButton = MutableLiveData<Boolean>().apply {
        value = false
    }

    val navigateToListEvent = MutableLiveData<Event<Boolean>>()

    val messageButtonClickEvent = MutableLiveData<Event<Boolean>> ()

    fun afterFeedbackTextChanged(editable: Editable) {
        checkValidation()
    }

    fun onRatingChanged() {
        Timber.d("called")
        checkValidation()
    }

    fun checkValidation() {
        enableSubmitButton.value = !feedback.value.isNullOrBlank() && rating.value!!.toInt() > 0
    }

    fun onMessageEmployerButtonClick() {
        messageButtonClickEvent.postValue(Event(true))
    }

    fun onSubmitButtonClick() {
        postFeedback()
    }

    private fun postFeedback() {
        Timber.d("${rating.value} ${feedback.value}")
        Timber.d("ApplyID: $applyID :: JobID: $jobID")
        viewModelScope.launch {
            val resposne = repository.submitVideoInterviewFeedback(
                    applyId = applyID,
                    jobId = jobID,
                    feedbackComment = feedback.value,
                    rating = rating.value.toString()
            )
            if(resposne.statuscode == "4"){
                navigateToListEvent.value = Event(true)
            }
        }
    }
}
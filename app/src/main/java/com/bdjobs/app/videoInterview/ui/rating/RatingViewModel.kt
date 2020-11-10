package com.bdjobs.app.videoInterview.ui.rating

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class RatingViewModel(
        val repository: VideoInterviewRepository,
        val applyId: String,
        val jobId: String
) : ViewModel() {

    val feedback = MutableLiveData<String>().apply {
        value = ""
    }

    val rating = MutableLiveData<Int>().apply {
        value = 0
    }

    val enableSubmitButton = MutableLiveData<Boolean>().apply {
        value = false
    }

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

    }

    fun onSubmitButtonClick() {
        postFeedback()
    }

    private fun postFeedback() {
        Timber.d("${rating.value} ${feedback.value}")
        viewModelScope.launch {
            val resposne = repository.submitVideoInterviewFeedback(
                    applyId = applyId,
                    jobId = jobId,
                    feedbackComment = feedback.value,
                    rating = rating.value.toString()
            )
            if(resposne.statuscode == "4"){

            }
        }
    }

}
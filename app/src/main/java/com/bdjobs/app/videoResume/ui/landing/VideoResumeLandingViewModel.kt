package com.bdjobs.app.videoResume.ui.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.videoResume.data.models.Question
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository

class VideoResumeLandingViewModel(
        private val videoResumeRepository: VideoResumeRepository
) : ViewModel() {

    private val _isAlertOn = MutableLiveData<Boolean?>()
    val isAlertOn: LiveData<Boolean?> = _isAlertOn

    fun onCheckedChanged(checked: Boolean) {
        try {
            _isAlertOn.value = checked
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun getAllQuestions() : List<Question>{
        return videoResumeRepository.getAllQuestions()
    }

}
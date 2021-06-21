package com.bdjobs.app.resume_dashboard.ui.view_edit_resume

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.resume_dashboard.data.repositories.ResumeDashboardRepository

class ViewEditResumeViewModel(private val repository: ResumeDashboardRepository) : ViewModel() {


    private var _resumeVisibility = MutableLiveData<String>().apply {
        value = ""
    }
    val resumeVisibility: LiveData<String> get() = _resumeVisibility

    var showBdJobsResumeSteps = MutableLiveData<Boolean>()

    var showVideoResumeSteps = MutableLiveData<Boolean>()

    var isLoading = MutableLiveData<Boolean>()

    init {
        showBdJobsResumeSteps.value = false
        showVideoResumeSteps.value = false
    }

    fun onBdJobsResumeStepClicked() {
        showBdJobsResumeSteps.value = showBdJobsResumeSteps.value==false
    }

    fun onVideoResumeStepsClicked() {
        showVideoResumeSteps.value = showVideoResumeSteps.value == false
    }
}
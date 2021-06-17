package com.bdjobs.app.resume_dashboard.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private var _totalResumeCount = MutableLiveData<String>()
    val totalResumeCount : LiveData<String> get() = _totalResumeCount

    private var _totalResumeEmailed = MutableLiveData<String> ()
    val totalResumeEmailed : LiveData<String> get() = _totalResumeEmailed

    private var _bdJobsResumeViewCount = MutableLiveData<String>()
    val bdJobsResumeViewCount : LiveData<String> get() = _bdJobsResumeViewCount

    private var _personalizedResumeViewCount = MutableLiveData<String>()
    val personalizedResumeViewCount : LiveData<String> get() = _personalizedResumeViewCount

    private var _videoResumeViewCount = MutableLiveData<String>()
    val videoResumeViewCount : LiveData<String> get() = _videoResumeViewCount

    private var _bdJobsResumeEmailCount = MutableLiveData<String>()
    val bdJobsResumeEmailCount : LiveData<String> get() = _bdJobsResumeEmailCount

    private var _personalizedResumeEmailCount = MutableLiveData<String>()
    val personalizedResumeEmailCount : LiveData<String> get() = _personalizedResumeEmailCount

    private var _resumeVisibility = MutableLiveData<String>()
    val resumeVisibility: LiveData<String> get() = _resumeVisibility

    init {

        _totalResumeCount.value = "60"
        _totalResumeEmailed.value = "40"

        _bdJobsResumeViewCount.value = "50"
        _personalizedResumeViewCount.value = "-"
        _videoResumeViewCount.value = "10"

        _bdJobsResumeEmailCount.value = "30"
        _personalizedResumeEmailCount.value = "10"

        _resumeVisibility.value = "Public"
    }
}
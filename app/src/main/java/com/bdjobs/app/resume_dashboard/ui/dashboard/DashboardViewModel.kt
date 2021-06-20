package com.bdjobs.app.resume_dashboard.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.resume_dashboard.data.repositories.ResumeDashboardRepository
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: ResumeDashboardRepository) : ViewModel() {

    private var _totalResumeCount = MutableLiveData<String>().apply {
        value = ""
    }
    val totalResumeCount: LiveData<String> get() = _totalResumeCount

    private var _totalResumeEmailed = MutableLiveData<String>().apply {
        value = ""
    }
    val totalResumeEmailed: LiveData<String> get() = _totalResumeEmailed

    private var _bdJobsResumeViewCount = MutableLiveData<String>()
    val bdJobsResumeViewCount: LiveData<String> get() = _bdJobsResumeViewCount

    private var _personalizedResumeViewCount = MutableLiveData<String>()
    val personalizedResumeViewCount: LiveData<String> get() = _personalizedResumeViewCount

    private var _videoResumeViewCount = MutableLiveData<String>()
    val videoResumeViewCount: LiveData<String> get() = _videoResumeViewCount

    private var _bdJobsResumeEmailCount = MutableLiveData<String>()
    val bdJobsResumeEmailCount: LiveData<String> get() = _bdJobsResumeEmailCount

    private var _personalizedResumeEmailCount = MutableLiveData<String>()
    val personalizedResumeEmailCount: LiveData<String> get() = _personalizedResumeEmailCount

    private var _resumeVisibility = MutableLiveData<String>().apply {
        value = ""
    }
    val resumeVisibility: LiveData<String> get() = _resumeVisibility

    var isLoading = MutableLiveData<Boolean>()

    init {
        manageResumeStats()
        resumePrivacyStatus()
    }


    private fun manageResumeStats() {
        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.manageResumeStats()
                isLoading.value = false

                if (response.statuscode == "0" && response.message == "Success") {
                    val data = response.data[0]

                    _totalResumeCount.value = data?.totalViews!!
                    _bdJobsResumeViewCount.value =
                        if (data.bdjobsViews != "0") data.bdjobsViews else "-"
                    _videoResumeViewCount.value =
                        if (data.videoViews != "0") data.videoViews else "-"
                    _personalizedResumeViewCount.value =
                        if (data.personalizedViews != "0") data.personalizedViews else "-"

                    _totalResumeEmailed.value = data.totalEmailed!!
                    _bdJobsResumeEmailCount.value =
                        if (data.bdjobsEmailed != "0") data.bdjobsEmailed else "-"
                    _personalizedResumeEmailCount.value =
                        if (data.personalizedEmailed != "0") data.personalizedEmailed else "-"

                } else {
                    isLoading.value = false
                }

            } catch (e: Exception) {
                isLoading.value = false
            }

        }
    }

    private fun resumePrivacyStatus() {
        isLoading.value = true

        viewModelScope.launch {
            try {

                val response = repository.resumePrivacyStatus()

                isLoading.value = false

                if (response.statuscode == "0" && response.message == "Success") {
                    val data = response.data!![0]

                    // 1 -> public 2-> private 3-> limited

                    _resumeVisibility.value = data?.resumeVisibilityType!!

                } else {
                    isLoading.value = false
                }
            } catch (e: Exception) {
                isLoading.value = false
            }
        }

    }
}
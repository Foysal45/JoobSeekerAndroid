package com.bdjobs.app.resume_dashboard.ui.view_edit_resume

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.resume_dashboard.data.models.DataMRD
import com.bdjobs.app.resume_dashboard.data.repositories.ResumeDashboardRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class ViewEditResumeViewModel(private val repository: ResumeDashboardRepository) : ViewModel() {


    private var _resumeVisibility = MutableLiveData<String>().apply {
        value = ""
    }
    val resumeVisibility: LiveData<String> get() = _resumeVisibility

    var showBdJobsResumeSteps = MutableLiveData<Boolean>()

    var showVideoResumeSteps = MutableLiveData<Boolean>()

    var isLoading = MutableLiveData<Boolean>()

    private var _detailsResumeStat = MutableLiveData<DataMRD>()
    val detailResumeStat : LiveData<DataMRD> get() = _detailsResumeStat

    var bdJobsStatusPercentage = MutableLiveData<String>().apply { value = "" }

    init {
        showBdJobsResumeSteps.value = false
        showVideoResumeSteps.value = false

        manageResumeDetailsStat()
    }


    private fun manageResumeDetailsStat() {
        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.manageResumeDetailsStat()
                isLoading.value = false
                if (response.statuscode == "0" && response.message == "Success") {

                    val data = response.data!![0]
                    _detailsResumeStat.value = data

                    bdJobsStatusPercentage.value = data.bdjobsStatusPercentage

                } else {
                    Timber.e("Invalid response")
                }

            } catch (e:Exception) {
                Timber.e("Error while fetching details resume stat")
                isLoading.value = false
            }
        }
    }

    fun onBdJobsResumeStepClicked() {
        showBdJobsResumeSteps.value = showBdJobsResumeSteps.value==false
    }

    fun onVideoResumeStepsClicked() {
        showVideoResumeSteps.value = showVideoResumeSteps.value == false
    }
}
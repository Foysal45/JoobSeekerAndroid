package com.bdjobs.app.resume_dashboard.ui.view_edit_resume

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.resume_dashboard.data.models.DataMRD
import com.bdjobs.app.resume_dashboard.data.repositories.ResumeDashboardRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat

class ViewEditResumeViewModel(private val repository: ResumeDashboardRepository) : ViewModel() {


    private var _resumeVisibility = MutableLiveData<String>().apply {
        value = ""
    }
    val resumeVisibility: LiveData<String> get() = _resumeVisibility

    var showBdJobsResumeSteps = MutableLiveData<Boolean>()

    var showVideoResumeSteps = MutableLiveData<Boolean>()

    var isLoading = MutableLiveData<Boolean>()

    private var _detailsResumeStat = MutableLiveData<DataMRD>()
    val detailResumeStat: LiveData<DataMRD> get() = _detailsResumeStat

    var bdJobsResumeStatusPercentage = MutableLiveData<Int>().apply { value = 0 }
    var bdJobsResumeLastUpdate = MutableLiveData<String>().apply { value = "" }


    var videoResumeStatusPercentage = MutableLiveData<Int>().apply { value = 0 }
    var videoResumeLastUpdate = MutableLiveData<String>().apply { value = "" }
    var isVideoResumeShowingToEmp = MutableLiveData<Boolean>()
    var isVideoResumeAvailable = MutableLiveData<Boolean>()

    var isPersonalizedResumeAvailable = MutableLiveData<Boolean>()
    var personalizedResumeLastUpload = MutableLiveData<String>().apply { value = "" }

    var videoResumeQ1 = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ2 = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ3 = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ4 = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ5 = MutableLiveData<String>().apply { value = "" }

//    init {
//        showBdJobsResumeSteps.value = false
//        showVideoResumeSteps.value = false
//
//        resumePrivacyStatus()
//        manageResumeDetailsStat()
//    }


    @SuppressLint("SimpleDateFormat")
    fun manageResumeDetailsStat() {
        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.manageResumeDetailsStat()
                isLoading.value = false
                if (response.statuscode == "0" && response.message == "Success") {

                    val data = response.data!![0]
                    _detailsResumeStat.value = data

                    bdJobsResumeStatusPercentage.value = data.bdjobsStatusPercentage?.toInt()
                    bdJobsResumeLastUpdate.value = formatDate(data.bdjobsLastUpdateDate)

                    isVideoResumeAvailable.value = data.videoStatusPercentage != "0"
                    videoResumeStatusPercentage.value = data.videoStatusPercentage?.toInt()
                    videoResumeLastUpdate.value = formatDateVP(data.videoLastUpdateDate)
                    isVideoResumeShowingToEmp.value = data.videoResumeVisibility == "1"
                    if (isVideoResumeAvailable.value == true) videoResumeQuestionList()

                    isPersonalizedResumeAvailable.value = data.personalizefileName != ""
                    if (data.personalizeLastUpdateDate != "") personalizedResumeLastUpload.value = formatDateVP(data.personalizeLastUpdateDate)

                } else {
                    Timber.e("Invalid response")
                }

            } catch (e: Exception) {
                Timber.e("Error while fetching details resume stat")
                isLoading.value = false
            }
        }
    }

    private fun videoResumeQuestionList() {
        viewModelScope.launch {
            try {
                val response = repository.getQuestionListFromRemote()
                val data = response.data
                if (data!!.isNotEmpty()) {
                    for (i in data.indices) {
                        when(i) {
                            0-> videoResumeQ1.value=data[i]?.buttonStatus
                            1-> videoResumeQ2.value=data[i]?.buttonStatus
                            2-> videoResumeQ3.value=data[i]?.buttonStatus
                            3-> videoResumeQ4.value=data[i]?.buttonStatus
                            4-> videoResumeQ5.value=data[i]?.buttonStatus
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e("Exception while fetching video resume question: ${e.localizedMessage}")
            }
        }
    }

    fun resumePrivacyStatus() {
        isLoading.value = true

        viewModelScope.launch {
            try {

                val response = repository.resumePrivacyStatus()

                if (response.statuscode == "0" && response.message == "Success") {
                    val data = response.data!![0]

                    // 1 -> public 2-> private 3-> limited

                    _resumeVisibility.value = data?.resumeVisibilityType!!

                } else {
                    Timber.e("Invalid response")
                }
            } catch (e: Exception) {
                Timber.e("Error while fetching details resume privacy stat")
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(lastUpdate: String?): String {
        var lastUpdate1 = lastUpdate
        var formatter = SimpleDateFormat("M/dd/yyyy")
        val date = formatter.parse(lastUpdate1!!)
        formatter = SimpleDateFormat("dd MMM yyyy")
        lastUpdate1 = formatter.format(date!!)

        Timber.d("Last updated at: $lastUpdate1")

        return lastUpdate1

    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDateVP(lastUpdate: String?): String {
        var lastUpdate1 = lastUpdate
        var formatter = SimpleDateFormat("M/dd/yyyy HH:mm:ss a")
        val date = formatter.parse(lastUpdate1!!)
        formatter = SimpleDateFormat("dd MMM yyyy")
        lastUpdate1 = formatter.format(date!!)

        Timber.d("Last updated at: $lastUpdate1")

        return lastUpdate1

    }

    fun onBdJobsResumeStepClicked() {
        showBdJobsResumeSteps.value = showBdJobsResumeSteps.value == false
    }

    fun onVideoResumeStepsClicked() {
        showVideoResumeSteps.value = showVideoResumeSteps.value == false
    }
}
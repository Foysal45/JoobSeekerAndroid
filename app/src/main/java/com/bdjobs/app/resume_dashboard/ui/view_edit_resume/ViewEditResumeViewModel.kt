package com.bdjobs.app.resume_dashboard.ui.view_edit_resume

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.API.ModelClasses.UploadResume
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

    private var _downloadCVStat = MutableLiveData<UploadResume>()
    val downloadCVStat : LiveData<UploadResume> get() = _downloadCVStat

    var bdJobsResumeStatusPercentage = MutableLiveData<Int>().apply { value = 0 }
    var bdJobsResumeLastUpdate = MutableLiveData<String>().apply { value = "" }

    var showBdJobsLastUpdateDate = MutableLiveData<Boolean>()


    var videoResumeStatusPercentage = MutableLiveData<Int>().apply { value = 0 }
    var videoResumeLastUpdate = MutableLiveData<String>().apply { value = "" }
    var isVideoResumeShowingToEmp = MutableLiveData<Boolean>()
    var isVideoResumeAvailable = MutableLiveData<Boolean>()
    var isBdjobsResumeAvailable = MutableLiveData<Boolean>()

    var isPersonalizedResumeAvailable = MutableLiveData<Boolean>()
    var personalizedResumeLastUpload = MutableLiveData<String>().apply { value = "" }
    var personalizedResumeFileType = MutableLiveData<String>()

    var videoResumeQ1 = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ1EN = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ2 = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ2EN = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ3 = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ3EN = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ4 = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ4EN = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ5 = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ5EN = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ6 = MutableLiveData<String>().apply { value = "" }
    var videoResumeQ6EN = MutableLiveData<String>().apply { value = "" }


    @SuppressLint("SimpleDateFormat")
    fun manageResumeDetailsStat(isCVPosted:String) {
        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.manageResumeDetailsStat(isCVPosted)
                isLoading.value = false
                if (response.statuscode == "0" && response.message == "Success") {

                    val data = response.data!![0]
                    _detailsResumeStat.value = data

                    isBdjobsResumeAvailable.value = data.bdjobsStatusPercentage != "0"

                    showBdJobsResumeSteps.value = data.bdjobsStatusPercentage != "100"
//                    showVideoResumeSteps.value = data.videoStatusPercentage != "100"

                    bdJobsResumeStatusPercentage.value = data.bdjobsStatusPercentage?.toInt()

                    showBdJobsLastUpdateDate.value = data.bdjobsLastUpdateDate != ""

                    if (data.bdjobsLastUpdateDate != "") bdJobsResumeLastUpdate.value =
                        formatDate(data.bdjobsLastUpdateDate)

                    isVideoResumeAvailable.value = data.videoStatusPercentage != "0"
                    videoResumeStatusPercentage.value = data.videoStatusPercentage?.toInt()
                    if (data.videoLastUpdateDate != "") videoResumeLastUpdate.value =
                        formatDateVP(data.videoLastUpdateDate)
                    isVideoResumeShowingToEmp.value = data.videoResumeVisibility == "1"
//                    if (isVideoResumeAvailable.value == true) videoResumeQuestionList()

                    isPersonalizedResumeAvailable.value = data.personalizefileName != ""
                    if (data.personalizeLastUpdateDate != "") personalizedResumeLastUpload.value =
                        formatDateVP(data.personalizeLastUpdateDate)

                    personalizedResumeFileType.value = if (data.personalizefileName!!.contains("pdf") ) "1" else "2"

                } else {
                    Timber.e("Invalid response")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("Error while fetching details resume stat: ${e.localizedMessage}")
                isLoading.value = false
            }
        }
    }

    fun videoResumeQuestionList() {
        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.getQuestionListFromRemote()
                if (response.isSuccessful && response.code()==200) {
                    val data = response.body()?.data
                    if (data!!.isNotEmpty()) {
                        showVideoResumeSteps.value = _detailsResumeStat.value?.videoStatusPercentage != "100"
                        for (i in data.indices) {
                            when (i) {
                                0 -> {
                                    videoResumeQ1.value = data[i]?.buttonStatus
                                    videoResumeQ1EN.value = data[i]?.questionText
                                }
                                1 -> {
                                    videoResumeQ2.value = data[i]?.buttonStatus
                                    videoResumeQ2EN.value = data[i]?.questionText
                                }
                                2 -> {
                                    videoResumeQ3.value = data[i]?.buttonStatus
                                    videoResumeQ3EN.value = data[i]?.questionText
                                }
                                3 -> {
                                    videoResumeQ4.value = data[i]?.buttonStatus
                                    videoResumeQ4EN.value = data[i]?.questionText
                                }
                                4 -> {
                                    videoResumeQ5.value = data[i]?.buttonStatus
                                    videoResumeQ5EN.value = data[i]?.questionText
                                }
                                5 -> {
                                    videoResumeQ6.value = data[i]?.buttonStatus
                                    videoResumeQ6EN.value = data[i]?.questionText
                                }
                            }
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

    fun downloadCv(status:String) {
        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.downloadCV(status)

                if (response.statuscode=="0") {
                    _downloadCVStat.value = response
                }
            } catch (e:Exception) {
                Timber.e("Error while getting CV download Link : ${e.localizedMessage}")
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
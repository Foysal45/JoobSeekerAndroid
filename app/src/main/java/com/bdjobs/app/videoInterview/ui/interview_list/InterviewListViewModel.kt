package com.bdjobs.app.videoInterview.ui.interview_list

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.bdjobs.app.assessment.Event
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.CertificateData
import com.bdjobs.app.assessment.repositories.CertificateRepository
import com.bdjobs.app.videoInterview.data.models.InterviewListData
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import kotlinx.coroutines.launch

class InterviewListViewModel(val videoInterviewRepository: VideoInterviewRepository, application: Application) : AndroidViewModel(application) {


    private var interviewList: List<InterviewListData?>? = null

    private val _interviews = MutableLiveData<List<InterviewListData?>>()
    private val _interviewCount = MutableLiveData<String>()
    private val _status = MutableLiveData<Status>()
    private val _inviteDate = MutableLiveData<String>()
    private val _submitDate = MutableLiveData<String>()




    val interviews: LiveData<List<InterviewListData?>>
        get() = _interviews
    val status: LiveData<Status>
        get() = _status

    val interviewCount: MutableLiveData<String>
        get() = _interviewCount

    init {
        getInterviewList()
    }

   private  fun getInterviewList() {
        _status.value = Status.LOADING

        viewModelScope.launch {
            try {
                _status.value = Status.LOADING

                interviewList = videoInterviewRepository.getInterviewList().data
                _interviews.value = interviewList
                 if (interviewList.isNullOrEmpty())
                    _interviewCount.value = "0"
                else
                     _interviewCount.value = videoInterviewRepository.getInterviewList().common!!.totalVideoInterview
                Log.d("INTERVIEW_DATA", "interviewList $interviewList")




                _status.value = Status.DONE
            } catch (e: Exception) {
                _status.value = Status.ERROR
                Log.d("INTERVIEW_DATA", "Exception $interviewList")
            }
        }
    }

    fun displayInterViewDetails(jobId: String) {
        Log.d("INTERVIEW_DATA", "Click $jobId")
        /* _navigateToResultDetails.value = Event(certificateData)*/

    }


}
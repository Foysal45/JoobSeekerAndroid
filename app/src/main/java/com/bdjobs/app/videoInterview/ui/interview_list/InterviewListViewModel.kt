package com.bdjobs.app.videoInterview.ui.interview_list

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.bdjobs.app.assessment.Event
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.CertificateData
import com.bdjobs.app.assessment.repositories.CertificateRepository
import com.bdjobs.app.videoInterview.data.models.InterviewListData
import com.bdjobs.app.videoInterview.data.repository.InterviewListRepository
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import kotlinx.coroutines.launch

class InterviewListViewModel(val videoInterviewRepository: InterviewListRepository,application: Application):AndroidViewModel(application) {

    private val interviewListRepository = InterviewListRepository(application)

    private var interviewList: List<InterviewListData?>? = null

    private val _interviews = MutableLiveData<List<InterviewListData?>>()
    val certificates: LiveData<List<InterviewListData?>>
        get() = _interviews

   /* private val _navigateToResultDetails = MutableLiveData<Event<CertificateData>>()
    val navigateToResultDetails: LiveData<Event<CertificateData>>
        get() = _navigateToResultDetails*/

    private val _status = MutableLiveData<Status>()
    val status : LiveData<Status>
        get() = _status

    init {
        getInterviewList()
    }

    fun getInterviewList() {
        _status.value = Status.LOADING
        viewModelScope.launch {
            try {
              /*  _status.value = Status.LOADING*/
                interviewList = interviewListRepository.getInterviewList().data
                _interviews.value = interviewList

                Log.d("INTERVIEW_DATA","interviewList $interviewList")
               /* _status.value = Status.DONE*/
            } catch (e: Exception) {
             /*   _status.value = Status.ERROR*/
                Log.d("INTERVIEW_DATA","Exception $interviewList")
            }
        }
    }

    fun displayResultDetails(certificateData: CertificateData) {
       /* _navigateToResultDetails.value = Event(certificateData)*/
    }


}
package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import android.app.DatePickerDialog
import android.util.Log
import androidx.lifecycle.*
import com.bdjobs.app.Utilities.pickDate
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.ScheduleData
import com.bdjobs.app.assessment.models.ScheduleRequest
import com.bdjobs.app.assessment.repositories.ScheduleRepository
import kotlinx.coroutines.launch
import java.util.*

class ChooseScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var now: Calendar

    var applications = application

    private  var scheduleRepository : ScheduleRepository

    private var scheduleList: List<ScheduleData?>? = null

    var scheduleRequest: ScheduleRequest = ScheduleRequest()

    private val _status = MutableLiveData<Status>()
    val status :LiveData<Status>
        get() = _status

    private val startDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        //updateDateInView(0)
    }

    private val _schedules = MutableLiveData<List<ScheduleData?>>()
    val schedules: LiveData<List<ScheduleData?>>
        get() = _schedules


    init {
        now = Calendar.getInstance()
        scheduleRepository = ScheduleRepository(application,scheduleRequest)
        _status.value = Status.DONE
    }

    fun getScheduleList() {

        scheduleRequest.apply {
            pageNo = "1"
            pageSize = "20"
            fromDate = ""
            toDate = ""
            venue = "0"
        }

        viewModelScope.launch {
            _status.value = Status.LOADING
            try {
                _status.value = Status.LOADING
                scheduleList = scheduleRepository.getScheduleList().data
                Log.d("rakib", scheduleList?.size.toString())
                _schedules.value = scheduleList
                _status.value = Status.DONE
            } catch (e: Exception) {
                _status.value = Status.ERROR
            }
        }
    }


    fun filterScheduleList(){

        viewModelScope.launch {
            _status.value = Status.LOADING
            try {
                _status.value = Status.LOADING
                scheduleList = scheduleRepository.getScheduleList().data
                _schedules.value = scheduleList
                _status.value = Status.DONE
            } catch (e: Exception) {
                _status.value = Status.ERROR
            }
        }
    }

//    fun openStartDateDialog()
//    {
//        Log.d("rakib", "cal called")
//        pickDate(applications.applicationContext,now,startDateSetListener)
//    }


}
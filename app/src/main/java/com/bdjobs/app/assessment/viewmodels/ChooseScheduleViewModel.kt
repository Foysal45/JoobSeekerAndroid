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

    private var scheduleRepository: ScheduleRepository

    private var scheduleList: MutableList<ScheduleData?>? = null
    private var filteredScheduleList: MutableList<ScheduleData?>? = mutableListOf()

    var scheduleRequest: ScheduleRequest = ScheduleRequest()

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

//    private val startDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
//        now.set(Calendar.YEAR, year)
//        now.set(Calendar.MONTH, monthOfYear)
//        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//        //updateDateInView(0)
//    }

    private val _schedules = MutableLiveData<List<ScheduleData?>>()
    val schedules: LiveData<List<ScheduleData?>>
        get() = _schedules


    init {
//        now = Calendar.getInstance()
        scheduleRepository = ScheduleRepository(application, scheduleRequest)
        _status.value = Status.DONE
    }

    fun getScheduleList() {

        scheduleRequest.apply {
            pageNo = "1"
            pageSize = "40"
            fromDate = ""
            toDate = ""
            venue = "0"
        }

        viewModelScope.launch {
            scheduleList?.clear()
            filteredScheduleList?.clear()
            _status.value = Status.LOADING
            try {
                _status.value = Status.LOADING
                scheduleList = scheduleRepository.getScheduleList().data?.toMutableList()
                Log.d("rakib before", scheduleList?.size.toString())

                scheduleList?.forEach {
                        Log.d("rakib", "$it")
                        if (it?.strBookingStatus == "0" || it?.strBookingStatus == "2"){
                            Log.d("rakib", "got it")
                            filteredScheduleList?.add(it)
                        }
                }

                Log.d("rakib after", filteredScheduleList?.size.toString())

                _schedules.value = filteredScheduleList
                _status.value = Status.DONE
            } catch (e: Exception) {
                _status.value = Status.ERROR
            }
        }
    }


    fun filterScheduleList() {

        viewModelScope.launch {
            _status.value = Status.LOADING
            try {
                _status.value = Status.LOADING
                scheduleList = scheduleRepository.getScheduleList().data?.toMutableList()
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
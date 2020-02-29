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

    private val _schedules = MutableLiveData<List<ScheduleData?>>()
    val schedules: LiveData<List<ScheduleData?>>
        get() = _schedules


    init {
        scheduleRepository = ScheduleRepository(application, scheduleRequest)
        _status.value = Status.DONE
    }

    fun getScheduleList() {

        scheduleRequest.apply {
            pageNo = "1"
            pageSize = "100"
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

                scheduleList?.forEach {
                        if (it?.strBookingStatus == "0" || it?.strBookingStatus == "2"){
                            filteredScheduleList?.add(it)
                        }
                }

                _schedules.value = filteredScheduleList
                _status.value = Status.DONE
            } catch (e: Exception) {
                _status.value = Status.ERROR
            }
        }
    }


    fun filterScheduleList() {
        scheduleList?.clear()
        filteredScheduleList?.clear()
        viewModelScope.launch {
            _status.value = Status.LOADING
            try {
                _status.value = Status.LOADING
                scheduleList = scheduleRepository.getScheduleList().data?.toMutableList()

                scheduleList?.forEach {
                    if (it?.strBookingStatus == "0" || it?.strBookingStatus == "2"){
                        filteredScheduleList?.add(it)
                    }
                }
                _schedules.value = filteredScheduleList
                _status.value = Status.DONE
            } catch (e: Exception) {
                _status.value = Status.ERROR
            }
        }
    }

}
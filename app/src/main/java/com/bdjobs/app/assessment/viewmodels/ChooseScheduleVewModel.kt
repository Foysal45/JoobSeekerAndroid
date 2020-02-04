package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.bdjobs.app.assessment.models.ScheduleData
import com.bdjobs.app.assessment.repositories.ScheduleRepository
import kotlinx.coroutines.launch

class ChooseScheduleVewModel(application: Application) : AndroidViewModel(application) {

    private val scheduleRepository = ScheduleRepository(application)

    private var scheduleList: List<ScheduleData?>? = null

    private val _schedules = MutableLiveData<List<ScheduleData?>>()
    val schedules: LiveData<List<ScheduleData?>>
        get() = _schedules

    init {
        getScheduleList()
    }

    private fun getScheduleList() {
        viewModelScope.launch {
            try {
                scheduleList = scheduleRepository.getScheduleList().data
                _schedules.value = scheduleList
//                Log.d("rakib try", "${certificateList?.size}")
            } catch (e: Exception) {
                //Log.d("rakib catch", e.message)
            }
        }
    }

}
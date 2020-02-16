package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.HomeData
import com.bdjobs.app.assessment.repositories.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val homeRepository = HomeRepository(application)

    private var homeDatas: List<HomeData?>? = null

//    private val

    private val _homeData = MutableLiveData<HomeData?>()
    val homeData: LiveData<HomeData?>
        get() = _homeData

    private val _status = MutableLiveData<Status>()
    val status :LiveData<Status>
        get() = _status

    init {
        getHomeInfo()
    }

    fun getHomeInfo() {
        _status.value = Status.LOADING
        viewModelScope.launch {
            try {
                _status.value = Status.LOADING
                _homeData.value = homeRepository.getHomeData().data?.get(0)
                _status.value = Status.DONE
                //_schedules.value = scheduleList
            } catch (e: Exception) {
                Log.d("rakib catch", e.message)
                _status.value = Status.ERROR
            }
        }
    }

}
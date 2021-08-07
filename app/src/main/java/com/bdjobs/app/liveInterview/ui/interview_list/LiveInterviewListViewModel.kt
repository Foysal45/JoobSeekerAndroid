package com.bdjobs.app.liveInterview.ui.interview_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.databases.internal.LiveInvitation
import com.bdjobs.app.liveInterview.data.models.LiveInterviewList
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import kotlinx.coroutines.launch

class LiveInterviewListViewModel(
        private val repository: LiveInterviewRepository,
        private val activity : String) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _liveInterviewListData = MutableLiveData<List<LiveInterviewList.Data?>>()
    val liveInterviewListData: LiveData<List<LiveInterviewList.Data?>> = _liveInterviewListData

    val list = MutableLiveData<List<LiveInvitation>>()

    private val _commonData = MutableLiveData<LiveInterviewList.Common?>()
    val commonData: LiveData<LiveInterviewList.Common?> = _commonData

    init {
        getLiveInterviewList("0")
    }

    fun getLiveInterviewList(time: String) {
        _dataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getLiveInterviewListFromRemote(time)
                _liveInterviewListData.value = response.data!!
                _commonData.value = response.common
//                if (activity == "0")
//                    list.value = repository.getAllTimeLiveInterviewListFromDatabase()
//                else
//                    list.value = repository.getThisMonthLiveInterviewListFromDatabase()
                _dataLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
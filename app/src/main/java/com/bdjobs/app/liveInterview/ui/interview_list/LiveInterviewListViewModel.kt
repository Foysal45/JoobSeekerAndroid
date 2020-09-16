package com.bdjobs.app.liveInterview.ui.interview_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.liveInterview.data.models.LiveInterviewList
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.videoInterview.data.models.VideoInterviewList
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import kotlinx.coroutines.launch

class LiveInterviewListViewModel(private val repository: LiveInterviewRepository) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _liveInterviewListData = MutableLiveData<List<LiveInterviewList.Data?>>()
    val liveInterviewListData: LiveData<List<LiveInterviewList.Data?>> = _liveInterviewListData

    private val _commonData = MutableLiveData<LiveInterviewList.Common?>()
    val commonData: LiveData<LiveInterviewList.Common?> = _commonData

    init {
        //getVideoInterviewList()
    }

    fun getLiveInterviewList(time : String) {
        _dataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getLiveInterviewListFromRemote(time)
                _liveInterviewListData.value = response.data
                _commonData.value = response.common
                _dataLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
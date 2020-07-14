package com.bdjobs.app.transaction.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.transaction.data.TransactionRepository
import com.bdjobs.app.videoInterview.data.models.VideoInterviewList
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import kotlinx.coroutines.launch

class TransactionListViewModel(private val repository: VideoInterviewRepository) : ViewModel() {


    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _videoInterviewListData = MutableLiveData<List<VideoInterviewList.Data?>>()
    val videoInterviewListData: LiveData<List<VideoInterviewList.Data?>> = _videoInterviewListData

    private val _commonData = MutableLiveData<VideoInterviewList.Common?>()
    val commonData: LiveData<VideoInterviewList.Common?> = _commonData

    init {
        //getVideoInterviewList()
    }

    fun getVideoInterviewList(time : String) {
        _dataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getVideoInterviewListFromRemote(time)
                _videoInterviewListData.value = response.data
                _commonData.value = response.common
                _dataLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }




}
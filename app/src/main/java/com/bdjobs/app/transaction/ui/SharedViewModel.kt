package com.bdjobs.app.transaction.ui

import android.content.ClipData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel :ViewModel(){

    val startDate = MutableLiveData<String>()
    val endDate = MutableLiveData<String>()
    val type = MutableLiveData<String>()

    fun setStartDate(item: String) {
        startDate.value = item
    }
    fun setEndDate(item: String) {
        endDate.value = item
    }

    fun setType(item: String) {
        type.value = item
    }
}
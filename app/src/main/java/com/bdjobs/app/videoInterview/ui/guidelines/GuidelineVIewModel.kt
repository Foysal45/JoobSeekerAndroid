package com.bdjobs.app.videoInterview.ui.guidelines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GuidelineVIewModel() : ViewModel() {

    val _viewInBangla = MutableLiveData<Boolean>().apply {
        value = true
    }
    val viewInBangla: LiveData<Boolean> = _viewInBangla

    val _viewInEnglish = MutableLiveData<Boolean>()
    val viewInEnglish: LiveData<Boolean> = _viewInEnglish

}
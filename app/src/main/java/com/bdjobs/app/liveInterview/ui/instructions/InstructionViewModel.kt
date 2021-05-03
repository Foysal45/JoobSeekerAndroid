package com.bdjobs.app.liveInterview.ui.instructions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//
// Created by Soumik on 5/3/2021.
// piyal.developer@gmail.com
//

class InstructionViewModel:ViewModel() {


    val _viewInBengali = MutableLiveData<Boolean>().apply {
        value = true
    }
    var viewInBengali: LiveData<Boolean> = _viewInBengali

    val _viewInEnglish = MutableLiveData<Boolean>()
    var viewInEnglish: LiveData<Boolean> = _viewInEnglish

}
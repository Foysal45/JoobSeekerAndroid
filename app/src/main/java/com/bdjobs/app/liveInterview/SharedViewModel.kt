package com.bdjobs.app.liveInterview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

//
// Created by Soumik on 6/3/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

class SharedViewModel :ViewModel() {


    private var _savedVideoFile = MutableLiveData<File?>()

    fun storeVideoFile(file:File?) {
        _savedVideoFile.value = file
    }

    fun getVideoFile() : File? {
        return _savedVideoFile.value
    }
}
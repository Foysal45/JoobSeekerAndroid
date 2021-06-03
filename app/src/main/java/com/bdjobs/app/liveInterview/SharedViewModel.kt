package com.bdjobs.app.liveInterview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//
// Created by Soumik on 6/3/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

class SharedViewModel :ViewModel() {

    val receivedChatData = MutableLiveData<Array<Any?>?>()

    fun receivedData(args:Array<Any?>?) {
        receivedChatData.postValue(args)
    }
}
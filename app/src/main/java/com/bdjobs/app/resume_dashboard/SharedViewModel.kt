package com.bdjobs.app.resume_dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//
// Created by Soumik on 6/20/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

class SharedViewModel :ViewModel() {

    var resumeVisibility = MutableLiveData<String> ().apply { value = "" }

}
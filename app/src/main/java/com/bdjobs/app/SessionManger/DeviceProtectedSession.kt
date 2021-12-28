package com.bdjobs.app.SessionManger

import android.content.Context
import androidx.core.content.ContextCompat
import com.bdjobs.app.utilities.Constants

//
// Created by Soumik on 7/12/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

class DeviceProtectedSession (context: Context?) {

    private var pref = ContextCompat.createDeviceProtectedStorageContext(context!!)?.getSharedPreferences(Constants.name_sharedPref,Context.MODE_PRIVATE)
    private var editor = pref?.edit()

    var isLoggedIn : Boolean?
    get() = pref?.getBoolean(Constants.session_key_loggedIn,false)
    set(value) {editor?.putBoolean(Constants.session_key_loggedIn,value!!)?.apply()}
}
package com.bdjobs.app.videoInterview.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

//
// Created by Soumik on 5/17/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

object DateUtils {

    val currentTime:String
        @SuppressLint("SimpleDateFormat")
        get() {
            val c = Calendar.getInstance()
            val df = SimpleDateFormat("HH:mm:ss")
            return df.format(c.time)
        }


    val currentDate: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val c = Calendar.getInstance()
            val df = SimpleDateFormat("dd MMM yyyy")
            return df.format(c.time)
        }
}
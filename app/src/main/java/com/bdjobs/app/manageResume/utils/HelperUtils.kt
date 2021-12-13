package com.bdjobs.app.manageResume.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat

//
// Created by Soumik on 6/24/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@SuppressLint("SimpleDateFormat")
fun formatDateVP(lastUpdate: String): String {
    var lastUpdate1 = lastUpdate
    var formatter = SimpleDateFormat("M/dd/yyyy HH:mm:ss a")
    val date = formatter.parse(lastUpdate1)
    formatter = SimpleDateFormat("dd MMM yyyy")
    lastUpdate1 = formatter.format(date!!)

    Timber.d("Last updated at: $lastUpdate1")

    return lastUpdate1

}

@SuppressLint("SimpleDateFormat")
fun formatDateVP(lastUpdate: String?,format: SimpleDateFormat= SimpleDateFormat("M/dd/yyyy HH:mm:ss a")): String {
    var lastUpdate1 = lastUpdate
    var formatter = format
    val date = formatter.parse(lastUpdate1!!)
    formatter = SimpleDateFormat("dd MMM yyyy")
    lastUpdate1 = formatter.format(date!!)

    Timber.d("Last updated at: $lastUpdate1")

    return lastUpdate1

}

fun getRootDirPath(context: Context): String {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val file: File = ContextCompat.getExternalFilesDirs(
            context.applicationContext,
            null
        )[0]
        file.absolutePath
    } else {
        context.applicationContext.filesDir.absolutePath
    }
}
package com.bdjobs.app.videoInterview.util

import android.annotation.SuppressLint
import timber.log.Timber
import java.sql.Time
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


    @SuppressLint("SimpleDateFormat")
    fun getTimeDifference(time: String): String {

        val time1 = Calendar.getInstance().time
        Timber.d("$time1")
        val f = SimpleDateFormat("HH:mm:ss")
        val timeString2 = f.format(time1)
        Timber.d( timeString2)

        val fractions1 = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val fractions2 = timeString2.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val hours1 = Integer.parseInt(fractions1[0])
        val hours2 = Integer.parseInt(fractions2[0])
        val minutes1 = Integer.parseInt(fractions1[1])
        val minutes2 = Integer.parseInt(fractions2[1])

//        if (hours2>12) hours2 -= 6
        Timber.d("h1: $hours1;;h2: $hours2;;min1: $minutes1;;min2: $minutes2")

        val start = Time(hours1, minutes1, 0)
        val stop = Time(hours2, minutes2, 0)

        val diff = difference(stop, start)

        Timber.d("HH:mm:ss-> ${diff.hours}:${diff.minutes}:${diff.seconds}")

        val hDiff = diff.hours
        val mDiff = diff.minutes
        val sDiff = diff.seconds


        return mDiff.toString()
    }


    private fun difference(start: Time, stop: Time): Time {
        val diff = Time(0, 0, 0)

        if (stop.seconds > start.seconds) {
            --start.minutes
            start.seconds += 60
        }

        diff.seconds = start.seconds - stop.seconds
        if (stop.minutes > start.minutes) {
            --start.hours
            start.minutes += 60
        }

        diff.minutes = start.minutes - stop.minutes
        diff.hours = start.hours - stop.hours

        return diff
    }

    @SuppressLint("SimpleDateFormat")
    fun diffTime(time: String): Long {
        var min: Long = 0
        val difference: Long
        try {
            val simpleDateFormat = SimpleDateFormat("HH:mm:ss") // for 12-hour system, hh should be used instead of HH
            // There is no minute different between the two, only 8 hours difference. We are not considering Date, So minute will always remain 0
            val date1 = simpleDateFormat.parse(time)
            val date2 = simpleDateFormat.parse(simpleDateFormat.format(Calendar.getInstance().time))
            difference = (date2?.time!! - date1?.time!!) / 1000
            val hours = difference % (24 * 3600) / 3600 // Calculating Hours
            val minute = difference % 3600 / 60 // Calculating minutes if there is any minutes difference
            min = minute + hours * 60 // This will be our final minutes. Multiplying by 60 as 1 hour contains 60 mins
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return min
    }
}
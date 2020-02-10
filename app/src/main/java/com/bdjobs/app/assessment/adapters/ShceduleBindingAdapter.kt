package com.bdjobs.app.assessment.adapters

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.ParseException
import java.text.SimpleDateFormat

@BindingAdapter("time")
fun bindScheduleTime(textView: TextView, time:String?){
    time?.let {
        var dateFormat = SimpleDateFormat("h aa")
        val dateFormat2 = SimpleDateFormat("hh:mm aa")
        try {
            val date = dateFormat.parse(time)
            val out = dateFormat2.format(date)
            textView.text = out
            Log.e("Time", out)
        } catch (e: ParseException) {
            dateFormat = SimpleDateFormat("h:mm aa")
            val date = dateFormat.parse(time)
            val out = dateFormat2.format(date)
            textView.text = out
            Log.d("Time", "parse")
        }
    }
}
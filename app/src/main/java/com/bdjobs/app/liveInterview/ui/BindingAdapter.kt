package com.bdjobs.app.liveInterview.ui

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("liveInterviewItemStatusCode")
fun bindLiveInterItemStatusCode(textview: MaterialTextView, statusCode: String?) {
    statusCode?.let {
        when (it) {
            "1", "2" -> {
                textview.apply {
                    setTextColor(ContextCompat.getColor(this.context, R.color.colorOrange))
                    setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_schedule_24, 0, 0, 0)
                    compoundDrawablePadding = 6
                }
            }
            "3" -> {
                textview.apply {
                    setTextColor(ContextCompat.getColor(this.context, R.color.green))
                }
            }
            "4" -> {
                textview.apply {
                    setTextColor(ContextCompat.getColor(this.context, R.color.warning_red))
                    setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_warning_24, 0, 0, 0)
                    compoundDrawablePadding = 4
                }
            }
            else -> {

            }
        }
    }
}

@BindingAdapter("timeToAMPM")
fun bindLiveInterviewTime(textView: TextView, time: String?) {
    // Get date from string
    time?.let {
        try {
            val dateFormatter = SimpleDateFormat("HH:mm:ss")
            val date: Date = dateFormatter.parse(time)

            // Get time from date
            val timeFormatter = SimpleDateFormat("h:mm a")
            textView.text = timeFormatter.format(date)
        } catch (e:Exception){
            e.printStackTrace()
        }

    }

}
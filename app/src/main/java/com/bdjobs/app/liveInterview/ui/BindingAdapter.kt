package com.bdjobs.app.liveInterview.ui

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.equalIgnoreCase
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
                    setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_check_circle_14, 0, 0, 0)
                    compoundDrawablePadding = 4
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

@BindingAdapter("liveInterviewDate", "liveInterviewTime")
fun bindLiveInterviewDateAndTime(textView: TextView, date: String?, time: String?) {
    date?.let {
        time?.let {
            textView.text = "Do you want to present in the live interview on $date, at ${getTimeAsAMPM(time)}?"
        }
    }
}


@BindingAdapter("timeToAMPM")
fun bindLiveInterviewTime(textView: TextView, time: String?) {
    // Get date from string
    time?.let {
        textView.text = getTimeAsAMPM(time)
    }
}

fun getTimeAsAMPM(time : String) : String{
    try {
        val dateFormatter = SimpleDateFormat("HH:mm:ss")
        val date: Date = dateFormatter.parse(time)

        // Get time from date
        val timeFormatter = SimpleDateFormat("h:mm a")
        return timeFormatter.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return time
}

@BindingAdapter("interviewConfirmationStatus","previousScheduleDate")
fun bindLiveInterviewConfirmationStatus(textView: TextView, status : String?, previousScheduleDate : String?){
    status?.let {
        if (status.equalIgnoreCase("1")) {
            textView.apply {
                text = "Confirmed"
                setTextColor(Color.parseColor("#13A10E"))
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_confirm_ic, 0, 0, 0)
            }

        }
        if (status.equalIgnoreCase("1") && !TextUtils.isEmpty(previousScheduleDate)) {
            textView.text = "Reschedule Confirmed"
            textView.setTextColor(Color.parseColor("#13A10E"))
            //holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_confirm_ic, 0, 0, 0)
        }
        if (status.equalIgnoreCase("2")) {
            textView.text = "Not Confirmed"
            textView.setTextColor(Color.parseColor("#FF3144"))
            //holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_cancel_ic, 0, 0, 0)
        }
        if (status.equalIgnoreCase("2") && !TextUtils.isEmpty(previousScheduleDate)) {
            textView.text = "Reschedule Not Confirmed"
            textView.setTextColor(Color.parseColor("#FF3144"))
            //holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_cancel_ic, 0, 0, 0)
        }
        if (status.equalIgnoreCase("3")) {
            textView.text = "Reschedule Request"
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_resch_ic, 0, 0, 0)
            textView.setTextColor(Color.parseColor("#FF6F00"))
            //holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
        }
        if (status.equalIgnoreCase("4")) {
            textView.apply {
                text = "Reschedule Rejected"
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_cancel_ic, 0, 0, 0)
                setTextColor(Color.parseColor("#FF3144"))
            }
            //holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
        }

        if (status.equalIgnoreCase("6")) {
            textView.apply {
                text = "Expired"
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_inv_expired_ic, 0, 0, 0)
                setTextColor(Color.parseColor("#FF3144"))
            }
            //holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
        }
    }
}
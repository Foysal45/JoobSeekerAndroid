package com.bdjobs.app.liveInterview.ui

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.utilities.equalIgnoreCase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@BindingAdapter("icon")
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

@BindingAdapter("date", "time", "status")
fun bindLiveInterviewTimer(textView: TextView, date: String?, time: String?, statusCode: String?) {

    statusCode?.let {
        when (it) {
            "1", "2" -> {
                var interviewDateTime = "$date $time"

                var remainingDays = ""
                var remainingHours = ""
                var remainingMinutes = ""
                var remainingSeconds = ""

                Timber.tag("LI").d("came here $interviewDateTime")
                val start_calendar: Calendar = Calendar.getInstance()
                val end_calendar: Calendar = Calendar.getInstance()

                val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
                end_calendar.time = simpleDateFormat.parse(interviewDateTime)

                //end_calendar.set(2020, 8, 15) // 10 = November, month start at 0 = January

                val start_millis: Long = start_calendar.timeInMillis //get the start time in milliseconds

                val end_millis: Long = end_calendar.timeInMillis //get the end time in milliseconds

                val total_millis = end_millis - start_millis //total time in milliseconds

                Timber.tag("LI").d("came here total ${total_millis}")

                //1000 = 1 second interval

                //1000 = 1 second interval

                var millisUntilFinished = total_millis
                val days: Long = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days)
                val hours: Long = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)
                val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

                remainingDays = DecimalFormat("0").format(days).toString()
                remainingHours = DecimalFormat("0").format(hours).toString()
                remainingMinutes = DecimalFormat("0").format(minutes).toString()
                remainingSeconds = DecimalFormat("0").format(seconds).toString()

                Timber.d("${remainingDays} ${remainingHours} ${remainingMinutes} ${remainingSeconds}")

                if (remainingDays.toInt() > 1) {
                    textView.text = "$remainingDays days remaining"
                } else if (remainingDays.toInt() == 1) {
                    textView.text = "$remainingDays day remaining"
                } else if (remainingDays.toInt() < 1) {
                    if (remainingHours.toInt() < 1) {
                        if (remainingMinutes.toInt() > 1) {
                            textView.text = "${remainingMinutes.toInt().plus(1)} mins remaining"
                        } else {
//                            textView.text = "${remainingMinutes.toInt().plus(1)} min remaining"
                            textView.visibility = View.GONE
                        }
                    } else if (remainingHours.toInt() == 1) {
                        if (remainingMinutes.toInt() > 1) {
                            textView.text = "$remainingHours hr ${remainingMinutes.toInt().plus(1)} mins remaining"
                        } else {
                            textView.text = "$remainingHours hr ${remainingMinutes.toInt().plus(1)} min remaining"
                        }
                    } else {
                        if (remainingMinutes.toInt() > 1) {
                            textView.text = "$remainingHours hrs ${remainingMinutes.toInt().plus(1)} mins remaining"
                        } else {
                            textView.text = "$remainingHours hrs ${remainingMinutes.toInt().plus(1)} min remaining"
                        }
                    }

                } else {
                    if (remainingMinutes.toInt() > 1) {
                        textView.text = "$remainingHours hrs $remainingMinutes mins remaining"
                    } else {
                        textView.text = "$remainingHours hrs $remainingMinutes min remaining"
                    }
                }
            }
            "4" -> {
                textView.text = "Expired"
            }
            "3" -> {
                textView.text = "Completed"
            }
        }
    }


    //textView.text = "$remainingHours hours $remainingMinutes minutes remaining"

//    val timer = object : CountDownTimer(total_millis, 1000) {
//
//        override fun onTick(millisUntilFinished: Long) {
//            Timber.tag("live").d("came here tick")
//
//            var millisUntilFinished = millisUntilFinished
//            val days: Long = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
//            millisUntilFinished -= TimeUnit.DAYS.toMillis(days)
//            val hours: Long = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
//            millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)
//            val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
//            millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)
//            val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
//
//            remainingDays = DecimalFormat("00").format(days).toString()
//            remainingHours = DecimalFormat("00").format(hours).toString()
//            remainingMinutes = DecimalFormat("00").format(minutes).toString()
//            remainingSeconds = DecimalFormat("00").format(seconds).toString()
//
//            Timber.d("${remainingDays} ${remainingHours} ${remainingMinutes} ${remainingSeconds}")
//
//            textView.text = "$remainingDays $remainingHours $remainingMinutes $remainingHours"
//
////                tv_countdown.setText("$days:$hours:$minutes:$seconds") //You can compute the millisUntilFinished on hours/minutes/seconds
//        }
//
//        override fun onFinish() {
//            //tv_countdown.setText("Finish!")
//            Timber.tag("live").d("came here finish")
//            remainingDays = "00"
//            remainingHours = "00"
//            remainingMinutes = "00"
//            remainingSeconds = "00"
//
//
//        }
//    }.start()

}

private fun setTimer(interviewDateTime: String) {


}

@BindingAdapter("timeToAMPM")
fun bindLiveInterviewTime(textView: TextView, time: String?) {
    // Get date from string
    time?.let {
        textView.text = getTimeAsAMPM(time)
    }
}

fun getTimeAsAMPM(time: String): String {
    if (time != "") {
        try {
            val dateFormatter = SimpleDateFormat("HH:mm:ss")
            val date: Date = dateFormatter.parse(time)

            // Get time from date
            val timeFormatter = SimpleDateFormat("h:mm a")
            return timeFormatter.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return time
}

@BindingAdapter("interviewConfirmationStatus", "previousScheduleDate")
fun bindLiveInterviewConfirmationStatus(textView: TextView, status: String?, previousScheduleDate: String?) {
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

        if (status.equalIgnoreCase("7")) {
            textView.apply {
                text = "Completed"
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_confirm_ic, 0, 0, 0)
                setTextColor(Color.parseColor("#13A10E"))
            }
        }
    }
}


@BindingAdapter("setBackgroundColor")
fun MaterialButton.bindBackground(color: Int) {
    val bgColor = if (color == 1) {
        resources.getColor(R.color.btn_green)
    } else resources.getColor(R.color.btn_ash)
    this.run {
        background.setTint(bgColor)
    }
}

@BindingAdapter(value = ["setAdapter"])
fun RecyclerView.bindRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
    this.run {
        this.setHasFixedSize(true)
        this.adapter = adapter
    }
}


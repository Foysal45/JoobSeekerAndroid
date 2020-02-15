package com.bdjobs.app.assessment.adapters

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bdjobs.app.assessment.enums.Status
import com.facebook.shimmer.ShimmerFrameLayout
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

@BindingAdapter("scheduleShimmer")
fun bindScheduleShimmer(shimmerFrameLayout: ShimmerFrameLayout, status : Status?){

    status?.let {
        when (status) {
            Status.LOADING ->  {
                Log.d("rakib", "loading")
                shimmerFrameLayout.visibility = View.VISIBLE
                shimmerFrameLayout.startShimmer()
            }
            Status.DONE -> {
                Log.d("rakib", "not loading")
                shimmerFrameLayout.visibility = View.GONE
                shimmerFrameLayout.stopShimmer()
            }
            Status.ERROR -> {
                shimmerFrameLayout.visibility = View.GONE
                shimmerFrameLayout.stopShimmer()
            }
        }
    }
}


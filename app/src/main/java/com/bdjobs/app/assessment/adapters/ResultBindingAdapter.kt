package com.bdjobs.app.assessment.adapters

import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.bdjobs.app.assessment.enums.Status
import com.facebook.shimmer.ShimmerFrameLayout
import java.text.ParseException
import java.text.SimpleDateFormat

@BindingAdapter("resultShimmerVisibility")
fun bindResultShimmer(shimmerFrameLayout: ShimmerFrameLayout?, status : Status?){

    status?.let {
        when (status) {
            Status.LOADING ->  {

                shimmerFrameLayout?.visibility = View.VISIBLE
                shimmerFrameLayout?.startShimmer()
            }
            Status.DONE -> {
                shimmerFrameLayout?.visibility = View.GONE
                shimmerFrameLayout?.stopShimmer()
            }
            Status.ERROR -> {
                shimmerFrameLayout?.visibility = View.GONE
                shimmerFrameLayout?.stopShimmer()
            }
        }
    }
}


@BindingAdapter("resultScrollViewVisibility")
fun bindResultScrollView(scrollView: ScrollView?, status : Status?){

    status?.let {
        when (status) {
            Status.LOADING ->  {
                scrollView?.visibility = View.INVISIBLE
            }
            Status.DONE -> {
                scrollView?.visibility = View.VISIBLE
            }
            Status.ERROR -> {
                scrollView?.visibility = View.GONE
            }
        }
    }
}

@BindingAdapter("resultDate")
fun bindTestDate(textView: TextView, date:String?){
    date?.let {
        var dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val dateFormat2 = SimpleDateFormat("dd MMM, yyyy")
        try {
            val date = dateFormat.parse(date)
            val out = dateFormat2.format(date)
            textView.text = out
            //Log.e("Time", out)
        } catch (e: ParseException) {
            //Log.d("Time", e.toString())
        }
    }
}

@BindingAdapter("resultButtonVisibility")
fun bindButtonView(constraintLayout: ConstraintLayout?, status : Status?){

    status?.let {
        when (status) {
            Status.LOADING ->  {
                constraintLayout?.visibility = View.INVISIBLE
            }
            Status.DONE -> {
                constraintLayout?.visibility = View.VISIBLE
            }
            Status.ERROR -> {
                constraintLayout?.visibility = View.GONE
            }
        }
    }
}
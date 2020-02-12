package com.bdjobs.app.assessment.adapters

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.bdjobs.app.assessment.enums.Status

@BindingAdapter("paymentProgressBarStatus")
fun bindProgressBarStatus(progressBar: ProgressBar, status: Status?) {

        status?.let {
            when (status) {
                Status.LOADING ->  {
                    Log.d("rakib", "loading")
                    progressBar.visibility = View.VISIBLE
                }
                Status.DONE -> {
                    Log.d("rakib", "not loading")
                    progressBar.visibility = View.GONE
                }
                Status.ERROR -> progressBar.visibility = View.GONE
            }
        }
}
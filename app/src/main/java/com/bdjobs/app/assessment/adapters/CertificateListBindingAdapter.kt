package com.bdjobs.app.assessment.adapters

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.CertificateData
import com.facebook.shimmer.ShimmerFrameLayout

//@BindingAdapter("certificateEmptyView")
//fun bindCertificateEmptyView(constraintLayout: ConstraintLayout, certificateData: List<CertificateData?>?){
//    if (certificateData.isNullOrEmpty()){
//        constraintLayout.visibility = View.VISIBLE
//    } else
//        constraintLayout.visibility = View.GONE
//}

@BindingAdapter("certificateShimmer")
fun bindProgressBarStatus(shimmerFrameLayout: ShimmerFrameLayout, status: Status?) {

    status?.let {
        when (status) {
            Status.LOADING ->  {
                shimmerFrameLayout.visibility = View.VISIBLE
                shimmerFrameLayout.startShimmer()
            }
            Status.DONE -> {
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

@BindingAdapter("visibility")
fun bindTextViewVisibility(textView: TextView,status: Status?) {
    status?.let {
        when (status) {
            Status.LOADING ->  {
                textView.visibility = View.GONE
            }
            Status.DONE -> {
                textView.visibility = View.VISIBLE
            }
            Status.ERROR -> {
                textView.visibility = View.GONE
            }
        }
    }
}
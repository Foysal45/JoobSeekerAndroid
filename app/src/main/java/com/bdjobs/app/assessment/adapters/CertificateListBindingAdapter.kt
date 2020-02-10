package com.bdjobs.app.assessment.adapters

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.bdjobs.app.assessment.models.CertificateData

@BindingAdapter("certificateEmptyView")
fun bimdCertificateEmptyView(constraintLayout: ConstraintLayout, certificateData: List<CertificateData?>?){
    if (certificateData.isNullOrEmpty()){
        constraintLayout.visibility = View.VISIBLE
    } else
        constraintLayout.visibility = View.GONE


}
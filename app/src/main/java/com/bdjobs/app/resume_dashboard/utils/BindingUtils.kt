package com.bdjobs.app.resume_dashboard.utils

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.layout_bdjobs_resume_steps.*

//
// Created by Soumik on 6/17/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@SuppressLint("SetTextI18n")
@BindingAdapter("totalViewCountText")
fun MaterialTextView.setTotalViewCountText(value: String) {
    this.run {
        this.text = "Total Views of Resume ($value)"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("totalEmailCountText")
fun MaterialTextView.setTotalEmailCountText(value: String) {
    this.run {
        this.text = "Total Emailed Resume ($value)"
    }
}

@BindingAdapter("textAndDrawable")
fun MaterialTextView.setTextAndDrawable(value: String?) {
    if (value!=null) {
        this.run {
            this.text = when (value) {
                "1" -> "Public"
                "2" -> "Private"
                else -> "Limited"
            }

            when(value) {
                "1" -> this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_public,0,0,0)
                "2" -> this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_private,0,0,0)
                else -> this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_limited,0,0,0)
            }

        }
    }
}

@BindingAdapter("switchDrawable")
fun MaterialTextView.switchDrawable(value:Boolean) {
    this.run {
        if (!value) this.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_down_arrow_resume,0)
        else this.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_up_arrow_resume,0)
    }
}

@BindingAdapter("checkedDrawable")
fun MaterialTextView.checkedStatusDrawable(value:Int?) {
    this.run{
        if (value == 1) this.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_sign_resume, 0, R.drawable.ic_back_arrow_resume, 0)
        else this.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cross_sign_resume, 0,  R.drawable.ic_back_arrow_resume, 0)
    }
}
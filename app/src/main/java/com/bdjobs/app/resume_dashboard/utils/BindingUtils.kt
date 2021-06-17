package com.bdjobs.app.resume_dashboard.utils

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView

//
// Created by Soumik on 6/17/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@SuppressLint("SetTextI18n")
@BindingAdapter("totalViewCountText")
fun MaterialTextView.setTotalViewCountText(value:String) {
    this.run {
        this.text = "Total Views of Resume ($value)"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("totalEmailCountText")
fun MaterialTextView.setTotalEmailCountText(value:String) {
    this.run {
        this.text = "Total Emailed Resume ($value)"
    }
}
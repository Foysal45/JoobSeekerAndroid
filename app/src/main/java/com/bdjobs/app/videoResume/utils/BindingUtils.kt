package com.bdjobs.app.videoResume.utils

import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.google.android.material.textview.MaterialTextView
import org.jetbrains.anko.textColor

//
// Created by Soumik on 6/9/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@BindingAdapter("backgroundAppearanceYes")
fun MaterialTextView.setBackgroundAppearanceYes(isSelected: Boolean) {

    if (isSelected) {
        this.run {
            background = ContextCompat.getDrawable(context, R.drawable.bg_btn_5_dp)
            background.setTint(resources.getColor(R.color.green))
            textColor = resources.getColor(R.color.white)
            setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_check_sign_visible,
                0,
                0,
                0
            )
        }
    } else {
        this.run {
            background = ContextCompat.getDrawable(context, R.drawable.bg_btn_5_dp)
            background.setTint(resources.getColor(android.R.color.transparent))
            textColor = resources.getColor(R.color.btn_ash)
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
}

@BindingAdapter("backgroundAppearanceNo")
fun MaterialTextView.setBackgroundAppearanceNo(isSelected: Boolean) {

    if (isSelected) {
        this.run {
            background = ContextCompat.getDrawable(context, R.drawable.bg_btn_5_dp_end)
            background.setTint(resources.getColor(R.color.btn_red))
            textColor = resources.getColor(R.color.white)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross_visible, 0, 0, 0)
        }
    } else {
        this.run {
            background = ContextCompat.getDrawable(context, R.drawable.bg_btn_5_dp_end)
            background.setTint(resources.getColor(android.R.color.transparent))
            textColor = resources.getColor(R.color.btn_ash)
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
}
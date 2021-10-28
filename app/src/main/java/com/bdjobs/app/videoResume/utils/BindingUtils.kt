package com.bdjobs.app.videoResume.utils

import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.google.android.material.card.MaterialCardView
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
            background.setTint(ContextCompat.getColor(context,R.color.green))
            textColor = ContextCompat.getColor(context,R.color.white)
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
            background.setTint(ContextCompat.getColor(context,android.R.color.transparent))
            textColor = ContextCompat.getColor(context,R.color.btn_ash)
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
}

@BindingAdapter("backgroundAppearanceNo")
fun MaterialTextView.setBackgroundAppearanceNo(isSelected: Boolean) {

    if (isSelected) {
        this.run {
            background = ContextCompat.getDrawable(context, R.drawable.bg_btn_5_dp_end)
            background.setTint(ContextCompat.getColor(context,R.color.btn_red))
            textColor = ContextCompat.getColor(context,R.color.white)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross_visible, 0, 0, 0)
        }
    } else {
        this.run {
            background = ContextCompat.getDrawable(context, R.drawable.bg_btn_5_dp_end)
            background.setTint(ContextCompat.getColor(context,android.R.color.transparent))
            textColor = ContextCompat.getColor(context,R.color.btn_ash)
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
}


@BindingAdapter("videoResumeTextDrawable")
fun MaterialTextView.setVideoResumeTextDrawable(value: Boolean?) {
    if (value != null) {
        this.run {
            this.text = when (value) {
                true -> "Yes"
                false -> "No"
            }

            when (value) {
                true -> {
                    this.textColor = ContextCompat.getColor(context,R.color.green)
//                    this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_show, 0, 0, 0)
                }
                false -> {
                    this.textColor = ContextCompat.getColor(context,R.color.btn_red)
//                    this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_hide, 0, 0, 0)
                }
            }

        }
    }
}

@BindingAdapter("cardBackground")
fun MaterialCardView.setBackgroundColor(value: Boolean?) {
    if (value!=null) {
        when(value) {
            true -> {
                this.background.setTint(ContextCompat.getColor(context,R.color.video_resume_visible))
            }
            false -> {
                this.background.setTint(ContextCompat.getColor(context,R.color.video_resume_hide))
            }
        }

    }
}

@BindingAdapter("visibilityIcon")
fun AppCompatImageView.setVisibilityIcon(value: Boolean?) {
    if (value!=null) {
        when (value) {
            true -> this.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_visibility_show))
            false -> this.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_visibility_hide))
        }
    }
}

package com.bdjobs.app.resume_dashboard.utils

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.layout_bdjobs_resume_steps.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

//
// Created by Soumik on 6/17/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Bdjobs.com Ltd. All rights reserved.
//

@SuppressLint("SetTextI18n")
@BindingAdapter("totalViewCountText")
fun MaterialTextView.setTotalViewCountText(value: String) {
    this.run {
        this.text = "Total No. Viewed Resume ($value)"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("totalEmailCountText")
fun MaterialTextView.setTotalEmailCountText(value: String) {
    this.run {
        this.text = "Total No. of Emailed Resume ($value)"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("showingToEmployerText")
fun MaterialTextView.setShowingToEmployerText(value: Boolean) {
    val showStatus = if (value) "Yes" else "No"
    this.run {
        this.text = "Showing my Video Resume to Employer(s): $showStatus"
        this.textColor =
            if (value) resources.getColor(R.color.green) else resources.getColor(R.color.btn_red)
    }
}

@BindingAdapter("textAndDrawable")
fun MaterialTextView.setTextDrawableAndBackground(value: String?) {
    if (value != null) {
        this.run {
            this.text = when (value) {
                "1" -> "Public"
                "2" -> "Private"
                "3" -> "Limited"
                else -> "Public"
            }

            when (value) {
                "1" -> {
                    this.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_visibility_public,
                        0,
                        0,
                        0
                    )

//                    this.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.privacy_public))

                }
                "2" -> {
                    this.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_visibility_private,
                        0,
                        0,
                        0
                    )
//                    this.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.privacy_private))

                }
                "3" -> {
                    this.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_visibility_limited,
                        0,
                        0,
                        0
                    )
//                    this.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.privacy_limited))

                }
                else -> {
                    this.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_visibility_public,
                        0,
                        0,
                        0
                    )

//                    this.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.privacy_public))
                }
            }

        }
    }
}

@BindingAdapter("setCardBackground")
fun MaterialCardView.setCardBackground(value: String?) {
    if (value!=null) {
        this.run {
            when(value) {
                "2" -> this.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.privacy_private))
                "3" -> this.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.privacy_limited))
                else -> this.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.privacy_public))
            }
        }
    }
}

@BindingAdapter("fileTypeDrawable")
fun MaterialTextView.fileTypeDrawable(value: String?) {
    if (value == "2") {
        this.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_ms_word,
            0,
            0,
            0
        )
    } else {
        this.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_pdf_personalized_resume,
            0,
            0,
            0
        )
    }

}

@BindingAdapter("switchDrawable")
fun MaterialTextView.switchDrawable(value: Boolean) {
    this.run {
        if (!value) this.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_down_arrow_resume,
            0
        )
        else this.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow_resume, 0)
    }
}

@BindingAdapter("checkedDrawable")
fun MaterialTextView.checkedStatusDrawable(value: Int?) {
    this.run {
        if (value == 1) this.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_check_sign_resume,
            0,
            R.drawable.ic_back_arrow_resume,
            0
        )
        else this.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_cross_sign_resume,
            0,
            R.drawable.ic_back_arrow_resume,
            0
        )
    }
}


@BindingAdapter("checkedDrawableVideo")
fun MaterialTextView.checkedStatusDrawableVideo(value: String?) {
    this.run {
        if (value == "2") this.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_check_sign_resume,
            0,
            R.drawable.ic_back_arrow_resume,
            0
        )
        else this.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_cross_sign_resume,
            0,
            R.drawable.ic_back_arrow_resume,
            0
        )
    }
}
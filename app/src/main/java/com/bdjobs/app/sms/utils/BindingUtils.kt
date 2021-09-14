package com.bdjobs.app.sms.utils

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.google.android.material.textview.MaterialTextView
import org.jetbrains.anko.textColor

@SuppressLint("SetTextI18n")
@BindingAdapter("remainingSmsCount")
fun MaterialTextView.setRemainingSMSCount(value: Int) {
    this.run {
        this.text = "Remaining SMS: $value"

        when {
            value <= 0 -> {
                this.textColor = resources.getColor(R.color.remaining_sms_0)
            }
            value in 1..9 -> {
                this.textColor = resources.getColor(R.color.remaining_sms_1_9)
            }
            else -> {
                this.textColor = resources.getColor(R.color.remaining_sms_10_more)
            }
        }
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("smsAlertTextBackgroundAndColor")
fun MaterialTextView.setSmsAlertTextBackgroundAndColor(value: Boolean) {
    this.run {
        if (value) {
            text = "ON"
            textColor = ContextCompat.getColor(context, R.color.white)
            background = ContextCompat.getDrawable(context, R.drawable.bg_on_sms_alert)
            background.setTint(ContextCompat.getColor(context, R.color.sms_alert_on))
        } else {
            text = "OFF"
            textColor = ContextCompat.getColor(context, R.color.white)
            background = ContextCompat.getDrawable(context, R.drawable.bg_on_sms_alert)
            background.setTint(ContextCompat.getColor(context, R.color.sms_alert_off))
        }
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("smsAlertLabelTextAndDrawable")
fun MaterialTextView.setSmsAlertLabelTextAndDrawable(value: Int) {
    this.run {
        if (value==0) {
            text = "SMS Job Alert Ended"
            textColor = ContextCompat.getColor(context,R.color.remaining_sms_0)
            setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_information_sms_alert_end,
                0,
                0,
                0
            )
        } else {
            text = "SMS Job Alert"
            textColor = ContextCompat.getColor(context,R.color.sms_text_color)
            setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_sms_alert_bell,
                0,
                0,
                0
            )
        }
    }
}
package com.bdjobs.app.training.utils

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.google.android.material.textview.MaterialTextView
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

@BindingAdapter("backgroundAppearance")
fun MaterialTextView.setBackgroundAppearance(isSelected: Boolean) {
    if (isSelected) {
        this.run {
            textColor = ContextCompat.getColor(context,android.R.color.white)
            backgroundResource = R.drawable.pending_active_bg
            setPadding(16,8,16,8)
        }
    } else {
        this.run {
            textColor = Color.parseColor("#424242")
            backgroundResource = R.drawable.success_inactive_bg
            setPadding(16,8,16,8)
        }
    }
}


//allTV.setTextColor(Color.parseColor("#FFFFFF"))
//allTV.setBackgroundResource(R.drawable.pending_active_bg)
//matchedTV.setTextColor(Color.parseColor("#424242"))
//matchedTV.setBackgroundResource(R.drawable.success_inactive_bg)
//matchedTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//allTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
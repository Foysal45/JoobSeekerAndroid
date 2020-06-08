package com.bdjobs.app.videoInterview.util

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.bdjobs.app.videoInterview.data.models.VideoInterviewDetails

@BindingAdapter("submitButtonStatus")
fun bindSubmitButton(button: Button, videoDetails: VideoInterviewDetails.Data?) {
    videoDetails?.let {
        if (it.vStatuCode == "4") {
            button.visibility = View.GONE
        } else {
            if (it.vButtonName.isNullOrEmpty()) {
                button.visibility = View.GONE
            } else {
                button.visibility = View.VISIBLE
                button.text = it.vButtonName
            }
        }
    }
}

@BindingAdapter("viewButtonStatus")
fun bindViewButton(button: Button, videoDetails: VideoInterviewDetails.Data?) {
    videoDetails?.let {
        if (it.vStatuCode == "4") {
            if (it.vButtonName.isNullOrEmpty()) {
                button.visibility = View.GONE
            } else {
                button.visibility = View.VISIBLE
                button.text = it.vButtonName
            }
        } else {
            button.visibility = View.GONE
        }
    }
}

@BindingAdapter("status")
fun bindStatus(view: View, videoDetails: VideoInterviewDetails.Data?) {
    videoDetails?.let {
        when (videoDetails.vStatuCode) {
            "0" -> {
                when(view){
                    is ConstraintLayout -> view.visibility = View.GONE
                }
            }
            "1" -> {
                when(view){
                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                    }
                    is ImageView ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_warning)
                    }
                    is TextView ->{
                        view.text = videoDetails.vStatus
                        view.setTextColor(Color.parseColor("#B71C1C"))
                    }
                }
            }
            "2" -> {
                when(view){
                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                    }
                    is ImageView ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_clock_red)
                    }
                    is TextView ->{
                        view.text = videoDetails.vStatus
                        view.setTextColor(Color.parseColor("#B71C1C"))
                    }
                }
            }
            "3" -> {
                when(view){
                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                    }
                    is ImageView ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_warning)
                    }
                    is TextView ->{
                        view.text = videoDetails.vStatus
                        view.setTextColor(Color.parseColor("#B71C1C"))
                    }
                }
            }
            "4" -> {
                when(view){
                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.background_green)
                    }
                    is ImageView ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_check_circle)
                    }
                    is TextView ->{
                        view.text = videoDetails.vStatus
                        view.setTextColor(Color.parseColor("#1B5E20"))
                    }
                }
            }
            "5" ->{
                when(view){
                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                    }
                    is ImageView ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_delete_red)
                    }
                    is TextView ->{
                        view.text = videoDetails.vStatus
                        view.setTextColor(Color.parseColor("#B71C1C"))
                    }
                }
            }
            "6"->{
                when(view){
                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                    }
                    is ImageView ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_warning)
                    }
                    is TextView ->{
                        view.text = videoDetails.vStatus
                        view.setTextColor(Color.parseColor("#B71C1C"))
                    }
                }
            }
            "7"->{
                when(view){
                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                    }
                    is ImageView ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_clock_red)
                    }
                    is TextView ->{
                        view.text = videoDetails.vStatus
                        view.setTextColor(Color.parseColor("#B71C1C"))
                    }
                }
            }
        }
    }
}
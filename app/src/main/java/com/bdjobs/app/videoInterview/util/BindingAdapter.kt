package com.bdjobs.app.videoInterview.util

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.bdjobs.app.utilities.equalIgnoreCase
import com.bdjobs.app.utilities.hide
import com.bdjobs.app.utilities.show
import com.bdjobs.app.videoInterview.data.models.VideoInterviewDetails
import com.bdjobs.app.videoInterview.data.models.VideoInterviewList
import com.bdjobs.app.videoResume.data.models.VideoResumeQuestionList
import com.google.android.material.button.MaterialButton

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
        if (videoDetails.vStatus.isNullOrEmpty()) {
            view.visibility = View.GONE
        } else {
            when (videoDetails.vStatuCode) {
                "0" -> {
                    when (view) {
                        is ConstraintLayout -> view.visibility = View.GONE
                    }
                }
                "1" -> {
                    when (view) {
                        is ConstraintLayout -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                        }
                        is ImageView -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_warning)
                        }
                        is TextView -> {
                            view.text = videoDetails.vStatus
                            view.setTextColor(Color.parseColor("#B71C1C"))
                        }
                    }
                }
                "2" -> {
                    when (view) {
                        is ConstraintLayout -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                        }
                        is ImageView -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_clock_red)
                        }
                        is TextView -> {
                            view.text = videoDetails.vStatus
                            view.setTextColor(Color.parseColor("#B71C1C"))
                        }
                    }
                }
                "3" -> {
                    when (view) {
                        is ConstraintLayout -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                        }
                        is ImageView -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_warning)
                        }
                        is TextView -> {
                            view.text = videoDetails.vStatus
                            view.setTextColor(Color.parseColor("#B71C1C"))
                        }
                    }
                }
                "4" -> {
                    when (view) {
                        is ConstraintLayout -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.background_green)
                        }
                        is ImageView -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_check_circle)
                        }
                        is TextView -> {
                            view.text = videoDetails.vStatus
                            view.setTextColor(Color.parseColor("#1B5E20"))
                        }
                    }
                }
                "5" -> {
                    when (view) {
                        is ConstraintLayout -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                        }
                        is ImageView -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_delete_red)
                        }
                        is TextView -> {
                            view.text = videoDetails.vStatus
                            view.setTextColor(Color.parseColor("#B71C1C"))
                        }
                    }
                }
                "6" -> {
                    when (view) {
                        is ConstraintLayout -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                        }
                        is ImageView -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_warning)
                        }
                        is TextView -> {
                            view.text = videoDetails.vStatus
                            view.setTextColor(Color.parseColor("#B71C1C"))
                        }
                    }
                }
                "7" -> {
                    when (view) {
                        is ConstraintLayout -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.background_red)
                        }
                        is ImageView -> {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_clock_red)
                        }
                        is TextView -> {
                            view.text = videoDetails.vStatus
                            view.setTextColor(Color.parseColor("#B71C1C"))
                        }
                    }
                }
            }
        }
    }
}

@BindingAdapter("questionSubmitButtonVisibility")
fun bindQuestionRecordButtonVisibility(button: MaterialButton, status: String) {
    if (status == "1") {
        button.show()
    } else
        button.hide()
}

@BindingAdapter("questionViewButtonVisibility")
fun bindQuestionViewButtonVisibility(button: MaterialButton, status: String) {
    if (status == "2") {
        button.show()
    } else
        button.hide()
}

@BindingAdapter("questionNotAnsweredButtonVisibility")
fun bindQuestionNotAnsweredButtonVisibility(button: MaterialButton, status: String) {
    if (status == "3") {
        button.show()
    } else
        button.hide()
}

@BindingAdapter("submitButtonTotalAnswers", "submitButtonIsNotInterested", "submitButtonEnableAfterTimer", "submitButtonTotalQuestions", "submitButtonRemainingTime")
fun bindSubmitButton(button: MaterialButton, totalAnswers: String?, isNotInterested: Boolean, enableAfterTimer: Boolean, totalQuestions: Int?, remainingTime: String?) {
    if (isNotInterested){
        button.alpha = 1f
        button.isEnabled = true
    } else{
        totalAnswers?.let {
            totalQuestions?.let {
                //Log.d("rakib", "total answers $totalAnswers total questions $totalQuestions")
                try {
                    if (totalAnswers.toInt() >= totalQuestions) {
                        button.alpha = 1f
                        button.isEnabled = true
                    } else {
//                    button.isEnabled = (isInterested || enableAfterTimer).also { enabled ->
//                        //Log.d("rakib", "is Interested $isInterested enable after timer $enableAfterTimer")
//                        if (enabled)
//                            button.alpha = 1f
//                        else
//                            button.alpha = .5f
//
//                    }
                        button.alpha = .5f
                        button.isEnabled = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }
//    remainingTime?.let {
//        if (it.toInt() == 0 && totalAnswers!!.toInt() > 0) {
//            button.alpha = 1f
//            button.isEnabled = true
//        }
//    }
}

// Video Interview List
@BindingAdapter("videoItemCardBackground")
fun bindVideoItemCardBackground(constraintLayout: ConstraintLayout, userSeenInterview: String) {
    if (userSeenInterview.equalIgnoreCase("True")) {
        constraintLayout.background = ContextCompat.getDrawable(constraintLayout.context, R.drawable.interview_invitatiion_card_unseen)
    } else {
        constraintLayout.background = ContextCompat.getDrawable(constraintLayout.context, R.drawable.ic_home_card)
    }
}

@BindingAdapter("newBadgeVisibility")
fun bindVideoItemCardColor(imageView: ImageView, userSeenInterview: String) {
    if (userSeenInterview.equalIgnoreCase("True")) {
        imageView.hide()
    } else {
        imageView.show()
    }
}

@BindingAdapter("inviteStatus")
fun bindInviteStatus(view: View, videoInvitationData: VideoInterviewList.Data) {
    if (videoInvitationData.dateStringForSubmission != "") {
        when (view) {
            is ImageView -> {
                view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_submitted)
            }
            is TextView -> {
                view.text = "Submitted on: ${videoInvitationData.dateStringForSubmission}"
                view.setTextColor(Color.parseColor("#388E3C"))
            }
        }
    } else if (videoInvitationData.dateStringForInvitaion != "") {
        when (view) {
            is ImageView -> {
                view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_calender_interview_invitation)
            }
            is TextView -> {
                view.text = "Invited on: ${videoInvitationData.dateStringForInvitaion}"
                view.setTextColor(Color.parseColor("#393939"))
            }
        }
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("employerSeenStatus")
fun bindEmployerSeenStatus(view: View, employerSeenDate: String) {
    if (employerSeenDate == "") {
        when (view) {
            is ImageView -> {
                view.hide()
            }
            is TextView -> {
                view.hide()
            }
        }
    } else {
        when (view) {
            is ImageView -> {
                view.show()
            }
            is TextView -> {
                view.show()
                view.text = "Seen: $employerSeenDate"
                view.setTextColor(Color.parseColor("#393939"))
            }
        }
    }
}

@BindingAdapter("interviewProcessStatus")
fun bindInterviewProcessStatus(view: View, videoInterviewData: VideoInterviewList.Data) {
    when (videoInterviewData.videoStatusCode) {
        "2" ->
            when (view) {
                is ImageView -> {
                    view.show()
                    view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_due_submission)
                }
                is TextView -> {
                    view.show()
                    view.text = videoInterviewData.videoStatus
                    view.setTextColor(Color.parseColor("#B71C1C"))
                }
            }
        "1" ->
            when (view) {
                is ImageView -> {
                    view.show()
                    view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_on_process)
                }
                is TextView -> {
                    view.show()
                    view.text = videoInterviewData.videoStatus
                    view.setTextColor(Color.parseColor("#F57F17"))
                }
            }
        "0", "4", "" ->
            when (view) {
                is ImageView -> {
                    view.hide()
                }
                is TextView -> {
                    view.hide()
                }
            }
    }

}

@BindingAdapter("visibilityWhenTimerShowing", "visibilityWhenSubmitted")
fun bindOneHourInfoVisibility(view: ConstraintLayout, remainingTime: String?, userSubmittedAnswer: String?) {
    userSubmittedAnswer?.let {
        if (userSubmittedAnswer.equalIgnoreCase("True")) {
            view.visibility = View.GONE
        } else {
            remainingTime?.let {
                if (it.toInt() > 0) {
                    view.visibility = View.GONE
                } else {
                    view.visibility = View.VISIBLE
                }
            }
        }
    }
}

@BindingAdapter("videoResumeButtonStatus")
fun bindVRButton(button: Button, videoResumeQuestionList: VideoResumeQuestionList.Data) {
    videoResumeQuestionList.let {
        if (it.buttonStatus == "1") {
            button.setBackgroundColor(Color.parseColor("#9D0191"))
        } else {
            button.setBackgroundColor(Color.parseColor("#000000"))
        }
    }
}
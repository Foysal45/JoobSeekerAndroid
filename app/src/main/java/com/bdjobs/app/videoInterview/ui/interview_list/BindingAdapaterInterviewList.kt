package com.bdjobs.app.videoInterview.ui.interview_list

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.assessment.enums.Status

import com.bdjobs.app.videoInterview.data.models.VideoInterviewList
import com.facebook.shimmer.ShimmerFrameLayout
import org.jetbrains.anko.backgroundColor

@BindingAdapter("InterviewList")
fun bindInterviewListRecyclerView(recyclerView: RecyclerView, data: List<VideoInterviewList.Data?>?) {
    val adapter = recyclerView.adapter as InterviewListAdapter

    Log.d("INTERVIEW_DATA", "bindInterviewListRecyclerView size ${data!!.size}")
    Log.d("INTERVIEW_DATA", "bindInterviewListRecyclerView data $data")

    data.let {
        adapter.submitList(data)

    }
}


@BindingAdapter("newBannerVisibility")
fun bindBannerNew(imageView: ImageView, status: String) {

    if (status == "True")
        imageView.visibility = View.VISIBLE
    else
        imageView.visibility = View.GONE


}


@BindingAdapter("interviewEmptyView", "interviewStatus")
fun bindInterviewEmptyView(constraintLayout: ConstraintLayout, data: List<VideoInterviewList.Data?>?, status: Status?) {

    //Log.d("rakib", "empty view called $data")

    when (status) {
        Status.LOADING -> {
            constraintLayout.visibility = View.GONE
        }

        Status.DONE -> {
            if (data.isNullOrEmpty()) {
                constraintLayout.visibility = View.VISIBLE
            } else {
                constraintLayout.visibility = View.GONE
            }
        }

        Status.ERROR -> {
            constraintLayout.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("interviewShimmer")
fun bindInterviewListShimmer(shimmerFrameLayout: ShimmerFrameLayout, status: Status?) {

    status?.let {
        when (status) {
            Status.LOADING -> {
                //Log.d("rakib", "loading")
                shimmerFrameLayout.visibility = View.VISIBLE
                shimmerFrameLayout.startShimmer()
            }
            Status.DONE -> {
                //Log.d("rakib", "not loading")
                shimmerFrameLayout.visibility = View.GONE
                shimmerFrameLayout.stopShimmer()
            }
            Status.ERROR -> {
                shimmerFrameLayout.visibility = View.GONE
                shimmerFrameLayout.stopShimmer()
            }
        }
    }


}


@BindingAdapter("interviewCLVisibility")
fun bindInterviewListShimmer(constraintLayout: ConstraintLayout, status: Status?) {

    status?.let {
        when (status) {
            Status.LOADING -> {
                //Log.d("rakib", "loading")
                constraintLayout.visibility = View.GONE

            }
            Status.DONE -> {
                //Log.d("rakib", "not loading")
                constraintLayout.visibility = View.VISIBLE

            }
            Status.ERROR -> {
                constraintLayout.visibility = View.VISIBLE
            }
        }
    }
}


@BindingAdapter("interviewNoDataVisibility")
fun bindNoData(linearLayout: LinearLayout, data: List<VideoInterviewList.Data?>?) {

    data?.let {
        if (data.isEmpty()) {
            linearLayout.visibility = View.VISIBLE
        } else
            linearLayout.visibility = View.GONE
    }
}

@BindingAdapter("inviteAndSubmit")
fun bindInviteAndSubmitView(view: View, data: VideoInterviewList.Data) {
   /* when (view) {
        is ImageView -> {
            if (data.invitationSubmitedDate.isNullOrEmpty()) {
                //Log.d("rakib", "empty view called $data")
                view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_calender_interview_invitation)
            } else if (data.invitationSubmitedDate.isNotEmpty()) {
                view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_submitted)
            }
        }


        is TextView -> {
            if (data.invitationSubmitedDate.isNullOrEmpty()) {
                view.text = "InvitedOn: ${data.inviteDate}"
                view.setTextColor(Color.parseColor("#393939"))
            } else if (data.invitationSubmitedDate.isNotEmpty()) {
                view.text = "SubmittedOn: ${data.invitationSubmitedDate}"
                view.setTextColor(Color.parseColor("#388E3C"))
            }
        }*/

        /* Status.ERROR -> {
             constraintLayout.visibility = View.VISIBLE
         }
    }*/
}




@BindingAdapter("interviewItemBackground")
fun bindInterviewItemBackground(view: View, data: VideoInterviewList.Data) {
   /* when (view) {
        is ImageView -> {
            if (data.dateString.isNullOrEmpty()) {
                //Log.d("rakib", "empty view called $data")
                view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_calender_interview_invitation)
            } else if (data.dateString.isNotEmpty()) {
                view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_submitted)
            }
        }


        is TextView -> {
            if (data.dateString.isNullOrEmpty()) {
                view.text = "InvitedOn: ${data.dateString}"
                view.setTextColor(Color.parseColor("#393939"))
            } else if (data.dateString.isNotEmpty()) {
                view.text = "SubmittedOn: ${data.dateString}"
                view.setTextColor(Color.parseColor("#388E3C"))
            }
        }
        *//* Status.ERROR -> {
             constraintLayout.visibility = View.VISIBLE
         }*//*
    }*/
}


@BindingAdapter("seenVisibility")
fun bindIconVisibility(view: View, invitationDate: String) {

    invitationDate?.let {
        if (invitationDate.isEmpty()) {
            view.visibility = View.GONE
        } else if (invitationDate.isNotEmpty())
            view.visibility = View.VISIBLE
    }
}


@BindingAdapter("onProcessSubmission")
fun bindOnProcessSubmission(view: View, statusCode: String) {

    Log.d("ksflsknfjdfndjnfj", "In  bindOnProcessSubmission ${statusCode}")
    statusCode?.let {

        when (statusCode) {
            "4" -> {
                when (view) {
                    is ImageView -> {
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_on_process)

                    }
                    is TextView -> {

                        view.text = "On Process"
                        view.setTextColor(Color.parseColor("#F57F17"))
                    }


                    is CardView -> {

                        view.backgroundColor = Color.parseColor("#F8F6EA")
                    }

                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_shape_yello)


                    }
                }


            }
            "5" -> {
                when (view) {
                    is ImageView -> {
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_due_submission)

                    }
                    is TextView -> {

                        view.text = "Deu Submisssion"
                        view.setTextColor(Color.parseColor("#B71C1C"))
                    }

                    is CardView -> {

                        view.backgroundColor = Color.parseColor("#F8F6EA")
                    }

                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_shape_yello)


                    }
                }
            }
            "1" -> {
                when (view) {
                    is ImageView -> {
                        view.visibility = View.INVISIBLE

                    }
                    is TextView -> {

                        view.visibility = View.INVISIBLE
                    }


                    is CardView -> {

                        view.backgroundColor = Color.parseColor("#FFFFFF")
                    }

                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_shape_white)


                    }
                }
            }
            "2" -> {


                Log.d("ksflsknfjdfndjnfj", "In  condition 2")
                when (view) {
                    is ImageView -> {
                        view.visibility = View.INVISIBLE

                    }
                    is TextView -> {

                        view.visibility = View.INVISIBLE
                    }

                    is CardView -> {

                        view.backgroundColor = Color.parseColor("#F8F6EA")
                    }

                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_shape_yello)


                    }
                }
            }
            "3" -> {
                when (view) {
                    is ImageView -> {
                        view.visibility = View.INVISIBLE

                    }
                    is TextView -> {

                        view.visibility = View.INVISIBLE
                    }


                    is CardView -> {

                        view.backgroundColor = Color.parseColor("#F8F6EA")
                    }

                    is ConstraintLayout ->{
                        view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_shape_yello)


                    }
                }
            }


        }

    }

}

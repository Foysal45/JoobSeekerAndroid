package com.bdjobs.app.videoInterview.ui.interview_list

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.ScheduleData

import com.bdjobs.app.videoInterview.data.models.InterviewListData
import com.facebook.shimmer.ShimmerFrameLayout

@BindingAdapter("InterviewList")
fun bindInterviewListRecyclerView(recyclerView: RecyclerView, data: List<InterviewListData?>?) {
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
fun bindInterviewEmptyView(constraintLayout: ConstraintLayout, data: List<InterviewListData?>?, status: Status?) {

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
fun bindInterviewListShimmer(shimmerFrameLayout: ShimmerFrameLayout, status : Status?){

    status?.let {
        when (status) {
            Status.LOADING ->  {
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
fun bindInterviewListShimmer(constraintLayout: ConstraintLayout, status : Status?){

    status?.let {
        when (status) {
            Status.LOADING ->  {
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
fun bindNoData(linearLayout: LinearLayout, data: List<InterviewListData?>?){

    data?.let {
       if (data.isEmpty()){
           linearLayout.visibility = View.VISIBLE
       } else
           linearLayout.visibility = View.GONE
    }
}

@BindingAdapter("inviteAndSubmit")
fun bindInviteAndSubmitView(view: View, data:InterviewListData) {



    when (view) {
        is ImageView -> {
            if (data.invitationSubmitedDate.isNullOrEmpty()){
                //Log.d("rakib", "empty view called $data")
                view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_calender_interview_invitation)
            } else {
                view.background = ContextCompat.getDrawable(view.context, R.drawable.ic_submitted)
            }
        }


       is TextView -> {
           if (data.invitationSubmitedDate.isNullOrEmpty()){
               view.text = "InvitedOn: ${data.inviteDate}"
               view.setTextColor(Color.parseColor("#393939"))
           } else {
               view.text = "SubmittedOn: ${data.invitationSubmitedDate}"
               view.setTextColor(Color.parseColor("#388E3C"))
           }
        }

       /* Status.ERROR -> {
            constraintLayout.visibility = View.VISIBLE
        }*/
    }
}


/*
@BindingAdapter("inviteSubmitVisibility")
fun bindNoData(view: View, date: String){

    data?.let {
       if (data.isEmpty()){
           linearLayout.visibility = View.VISIBLE
       } else
           linearLayout.visibility = View.GONE
    }
}
*/

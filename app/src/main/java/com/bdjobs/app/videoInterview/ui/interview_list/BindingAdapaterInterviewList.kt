package com.bdjobs.app.videoInterview.ui.interview_list

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

import com.bdjobs.app.videoInterview.data.models.InterviewListData

@BindingAdapter("InterviewList")
fun bindInterviewListRecyclerView(recyclerView: RecyclerView, data: List<InterviewListData?>?) {
    val adapter = recyclerView.adapter as InterviewListAdapter

    Log.d("INTERVIEW_DATA","bindInterviewListRecyclerView size ${data!!.size}")
    Log.d("INTERVIEW_DATA","bindInterviewListRecyclerView data $data")

    data.let {
        adapter.submitList(data)

    }
}
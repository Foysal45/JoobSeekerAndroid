package com.bdjobs.app.videoInterview.ui.interview_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.databinding.ItemVedioInterviewListBinding
import com.bdjobs.app.videoInterview.data.models.InterviewListData


class InterviewListAdapter(val context: Context):
            ListAdapter<InterviewListData,InterviewListAdapter.InterviewViewHolder>(DiffUserCallback)

{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterviewViewHolder {
        return InterviewViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: InterviewViewHolder, position: Int) {
        val certificate = getItem(position)
        holder.bind(certificate)
    }


    companion object DiffUserCallback : DiffUtil.ItemCallback<InterviewListData>() {
        override fun areItemsTheSame(oldItem: InterviewListData, newItem: InterviewListData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: InterviewListData, newItem: InterviewListData): Boolean {
            return oldItem?.jobId == newItem?.jobId
        }
    }





    class InterviewViewHolder(private var binding: ItemVedioInterviewListBinding) :
            RecyclerView.ViewHolder(binding.root) {


        companion object {

            fun from(parent: ViewGroup): InterviewViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemVedioInterviewListBinding.inflate(layoutInflater, parent, false)
                return InterviewViewHolder(binding)
            }
        }


        fun bind(
                interview: InterviewListData

        ) {
            binding.interview = interview

        }
    }


}

class ClickListenerInterViewList(val clickListener: (id: InterviewListData) -> Unit) {
    fun onClick(interviewData: InterviewListData) = clickListener(interviewData)
}


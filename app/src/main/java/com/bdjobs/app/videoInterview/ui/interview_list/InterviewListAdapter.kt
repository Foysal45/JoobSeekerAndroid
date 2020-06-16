package com.bdjobs.app.videoInterview.ui.interview_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.bdjobs.app.databinding.ItemVedioInterviewListBinding
import com.bdjobs.app.videoInterview.data.models.VideoInterviewList


class InterviewListAdapter(val context: Context, val clickListener: ClickListenerInterViewList):
            ListAdapter<VideoInterviewList.Data,InterviewListAdapter.InterviewViewHolder>(DiffUserCallback)

{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterviewViewHolder {
        return InterviewViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: InterviewViewHolder, position: Int) {
        val interView = getItem(position)
        holder.bind(interView,clickListener)
    }


    companion object DiffUserCallback : DiffUtil.ItemCallback<VideoInterviewList.Data>() {
        override fun areItemsTheSame(oldItem: VideoInterviewList.Data, newItem: VideoInterviewList.Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: VideoInterviewList.Data, newItem: VideoInterviewList.Data): Boolean {
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
                interview: VideoInterviewList.Data,
                clickListener: ClickListenerInterViewList

        ) {
            binding.interview = interview
            binding.executePendingBindings()
            binding.clickListener = clickListener
          /*  binding.certificateNameTv.text = interview.companyName*/

        }
    }


}
class ClickListenerInterViewList(val clickListener: (jobId: String, jobTitle : String) -> Unit) {
    fun onClick(jobId: String, jobTitle: String) = clickListener(jobId,jobTitle)
}



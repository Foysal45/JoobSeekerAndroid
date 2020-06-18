package com.bdjobs.app.videoInterview.ui.interview_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.databinding.ItemVedioInterviewListBinding
import com.bdjobs.app.videoInterview.data.models.VideoInterviewList

class VideoInterviewListAdapter(val context: Context, val clickListener: ClickListener) :
        ListAdapter<VideoInterviewList.Data, VideoInterviewListAdapter.VideoInterviewHolder>(
                DiffUtilCallback
        ) {


    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): VideoInterviewHolder {
        return VideoInterviewHolder.from(
                parent
        )
    }

    override fun onBindViewHolder(holder: VideoInterviewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<VideoInterviewList.Data>() {
        override fun areItemsTheSame(oldItem: VideoInterviewList.Data, newItem: VideoInterviewList.Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: VideoInterviewList.Data, newItem: VideoInterviewList.Data): Boolean {
            return oldItem.jobId == newItem.jobId
        }
    }

    class VideoInterviewHolder(private var binding: ItemVedioInterviewListBinding) :
            RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): VideoInterviewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemVedioInterviewListBinding.inflate(layoutInflater, parent, false)
                return VideoInterviewHolder(binding)
            }
        }

        fun bind(videoInterviewData: VideoInterviewList.Data, clickListener: ClickListener) {
            binding.videoInterview = videoInterviewData
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }
    }
}


class ClickListener(val clickListener: (videoInterviewData: VideoInterviewList.Data) -> Unit) {
    fun onClick(videoInterviewData: VideoInterviewList.Data) {
        clickListener(videoInterviewData)
    }
}



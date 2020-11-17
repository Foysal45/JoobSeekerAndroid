package com.bdjobs.app.liveInterview.ui.interview_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.databases.internal.LiveInvitation
import com.bdjobs.app.databinding.LayoutItemLiveInterviewBinding
import com.bdjobs.app.liveInterview.data.models.LiveInterviewList

class LiveInterviewListAdapter(val context: Context, val clickListener: ClickListener) :
        ListAdapter<LiveInvitation, LiveInterviewListAdapter.LiveInterviewHolder>(
                DiffUtilCallback
        ) {


    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): LiveInterviewHolder {
        return LiveInterviewHolder.from(
                parent
        )
    }

    override fun onBindViewHolder(holder: LiveInterviewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<LiveInvitation>() {
        override fun areItemsTheSame(oldItem: LiveInvitation, newItem: LiveInvitation): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LiveInvitation, newItem: LiveInvitation): Boolean {
            return oldItem.jobId == newItem.jobId
        }
    }

    class LiveInterviewHolder(private var binding: LayoutItemLiveInterviewBinding) :
            RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): LiveInterviewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutItemLiveInterviewBinding.inflate(layoutInflater, parent, false)
                return LiveInterviewHolder(binding)
            }
        }

        fun bind(liveInterviewData: LiveInvitation, clickListener: ClickListener) {
            binding.data = liveInterviewData
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
}


class ClickListener(val clickListener: (liveInterviewData: LiveInvitation) -> Unit) {
    fun onClick(liveInterviewData: LiveInvitation) {
        clickListener(liveInterviewData)
    }
}



package com.bdjobs.app.transaction.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.databinding.ItemTransactionListBinding

import com.bdjobs.app.videoInterview.data.models.VideoInterviewList

class TransactionListAdapter(val context: Context) :
        ListAdapter<VideoInterviewList.Data, TransactionListAdapter.TransactionListViewHolder>(
                DiffUtilCallback
        ) {


    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): TransactionListViewHolder {
        return TransactionListViewHolder.from(
                parent
        )
    }

    override fun onBindViewHolder(holder: TransactionListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<VideoInterviewList.Data>() {
        override fun areItemsTheSame(oldItem: VideoInterviewList.Data, newItem: VideoInterviewList.Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: VideoInterviewList.Data, newItem: VideoInterviewList.Data): Boolean {
            return oldItem.jobId == newItem.jobId
        }
    }

    class TransactionListViewHolder(private var binding: ItemTransactionListBinding) :
            RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): TransactionListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTransactionListBinding.inflate(layoutInflater, parent, false)
                return TransactionListViewHolder(binding)
            }
        }

        fun bind(videoInterviewData: VideoInterviewList.Data) {
            binding.videoInterview = videoInterviewData
            binding.executePendingBindings()

        }
    }
}


/*
class ClickListener(val clickListener: (videoInterviewData: VideoInterviewList.Data) -> Unit) {
    fun onClick(videoInterviewData: VideoInterviewList.Data) {
        clickListener(videoInterviewData)
    }
}*/

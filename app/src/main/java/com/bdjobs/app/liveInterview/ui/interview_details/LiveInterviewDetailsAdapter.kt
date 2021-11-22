package com.bdjobs.app.liveInterview.ui.interview_details

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.databinding.LayoutItemLiveInterviewDetailsBinding
import com.bdjobs.app.liveInterview.data.models.LiveInterviewDetails

class LiveInterviewDetailsAdapter(val session: BdjobsUserSession, val context: Context, val clickListener: ClickListener) :
        ListAdapter<LiveInterviewDetails.Data, LiveInterviewDetailsAdapter.LiveInterviewDetailsHolder>(
                DiffUtilCallback
        ) {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): LiveInterviewDetailsHolder {
        return LiveInterviewDetailsHolder.from(
                parent
        )
    }

    override fun onBindViewHolder(holder: LiveInterviewDetailsHolder, position: Int) {
        holder.bind(session, getItem(position), clickListener)
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<LiveInterviewDetails.Data>() {
        override fun areItemsTheSame(oldItem: LiveInterviewDetails.Data, newItem: LiveInterviewDetails.Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LiveInterviewDetails.Data, newItem: LiveInterviewDetails.Data): Boolean {
            return oldItem.invitationId == newItem.invitationId
        }


    }

    class LiveInterviewDetailsHolder(private var binding: LayoutItemLiveInterviewDetailsBinding) :
            RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): LiveInterviewDetailsHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutItemLiveInterviewDetailsBinding.inflate(layoutInflater, parent, false)
                return LiveInterviewDetailsHolder(binding)
            }
        }

        fun bind(session: BdjobsUserSession, liveInterviewDetailsData: LiveInterviewDetails.Data, clickListener: ClickListener) {

            val showUndo = liveInterviewDetailsData.invitationId?.let {
                session.getliveInterviewConfirmStatus(
                    it
                )
            }

            Log.e("viewModel adapter", ""+liveInterviewDetailsData.showUndo)

            if (showUndo == "1"){
                Log.e("viewModel adapter2", ""+showUndo)
                liveInterviewDetailsData.showUndo = "0"

                Log.e("viewModel adapter3", ""+liveInterviewDetailsData.showUndo)
            }
            binding.data = liveInterviewDetailsData
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }


    }


}

class ClickListener(val clickListener: (liveInterviewDetailsData: LiveInterviewDetails.Data) -> Unit) {
    fun onClick(liveInterviewDetailsData: LiveInterviewDetails.Data) {
        clickListener(liveInterviewDetailsData)
    }
}



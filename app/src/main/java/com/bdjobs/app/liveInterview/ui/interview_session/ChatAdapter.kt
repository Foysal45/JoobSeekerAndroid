package com.bdjobs.app.liveInterview.ui.interview_session

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.databinding.ItemChatMessagesBinding
import com.bdjobs.app.databinding.ItemRemoteChatMessageBinding
import com.bdjobs.app.liveInterview.data.models.Messages
import com.bdjobs.app.liveInterview.utils.DiffUtilItemCallback

class ChatAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val LOCAL_MESSAGE = 0
        private const val REMOTE_MESSAGE = 1
    }



    val differ = AsyncListDiffer(this,DiffUtilItemCallback<Messages>())

    inner class RemoteHolder(private val binding:ItemChatMessagesBinding):RecyclerView.ViewHolder(binding.root) {

        fun bind(messages:Messages?) {
            binding.model = messages

            binding.tvSenderMessage.text = messages?.message
            binding.tvSenderName.text = messages?.userName
            binding.tvSentTime.text = messages?.messageTime

            binding.executePendingBindings()
        }
    }

    inner class LocalHolder(private val binding:ItemRemoteChatMessageBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(messages: Messages?) {
            binding.model = messages


            binding.tvLocalMessage.text = messages?.message
            binding.tvSentTimeLocal.text = messages?.messageTime

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder: RecyclerView.ViewHolder? = when(viewType) {
            LOCAL_MESSAGE -> {
                LocalHolder(ItemRemoteChatMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            else -> {
                RemoteHolder(ItemChatMessagesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
        }

        return holder!!
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(differ.currentList[position].itemType) {
            LOCAL_MESSAGE -> {
                val localHolder = holder as LocalHolder

                localHolder.bind(differ.currentList[position])
            }
            else -> {
                val remoteHolder = holder as RemoteHolder
                remoteHolder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return differ.currentList[position].itemType!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}
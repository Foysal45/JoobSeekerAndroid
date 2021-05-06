package com.bdjobs.app.liveInterview.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.databinding.ItemChatMessagesBinding
import com.bdjobs.app.liveInterview.data.models.Messages
import com.bdjobs.app.liveInterview.utils.DiffUtilItemCallback

class ChatAdapter:RecyclerView.Adapter<ChatAdapter.Holder>() {

    val differ = AsyncListDiffer(this,DiffUtilItemCallback<Messages>())

    inner class Holder(private val binding:ItemChatMessagesBinding):RecyclerView.ViewHolder(binding.root) {

        fun bind(messages:Messages?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemChatMessagesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(differ.currentList[position])
    }
}
package com.bdjobs.app.transaction.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.databinding.ItemTransactionListBinding
import com.bdjobs.app.transaction.data.model.TransactionData
import com.bdjobs.app.transaction.data.model.TransactionList

import com.bdjobs.app.videoInterview.data.models.VideoInterviewList

class TransactionListAdapter(val context: Context) :
        ListAdapter<TransactionData, TransactionListAdapter.TransactionListViewHolder>(
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

    companion object DiffUtilCallback : DiffUtil.ItemCallback<TransactionData>() {
        override fun areItemsTheSame(oldItem: TransactionData, newItem: TransactionData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TransactionData, newItem: TransactionData): Boolean {
            return oldItem.packageName == newItem.packageName
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

        fun bind(transactionData: TransactionData) {
            binding.transaction = transactionData
            binding.executePendingBindings()

        }
    }
}




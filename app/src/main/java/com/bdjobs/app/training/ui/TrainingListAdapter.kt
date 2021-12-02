package com.bdjobs.app.training.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bdjobs.app.common.CommonAdapter
import com.bdjobs.app.databinding.ItemTrainingListBinding
import com.bdjobs.app.training.data.models.TrainingListData

class TrainingListAdapter(private val callback: ((TrainingListData) -> Unit)?) :
    CommonAdapter<TrainingListData, ItemTrainingListBinding>(
        diffCallback = object : DiffUtil.ItemCallback<TrainingListData>() {
            override fun areItemsTheSame(
                oldItem: TrainingListData,
                newItem: TrainingListData
            ): Boolean {
                return oldItem.topic == newItem.topic
            }

            override fun areContentsTheSame(
                oldItem: TrainingListData,
                newItem: TrainingListData
            ): Boolean {
                return oldItem == newItem
            }

        }
    ) {
    override fun createBinding(parent: ViewGroup): ItemTrainingListBinding {
        return ItemTrainingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bind(binding: ItemTrainingListBinding, item: TrainingListData) {
        binding.apply {
            tvTopic.text = item.topic
            tvVenue.text = item.venue
            tvDate.text = item.date

            root.setOnClickListener {
                callback?.invoke(item)
            }
        }
    }
}
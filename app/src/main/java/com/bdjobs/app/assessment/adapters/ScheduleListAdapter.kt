package com.bdjobs.app.assessment.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.assessment.models.ScheduleData
import com.bdjobs.app.databinding.ItemScheduleBinding

class ScheduleListAdapter(val context: Context, val clickListener: ScheduleClickListener) :
        ListAdapter<ScheduleData, ScheduleListAdapter.ScheduleViewHolder>(DiffUserCallback) {

    private var selectedItemViewHolder: ScheduleViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = getItem(position)

        holder.bind(schedule, ScheduleClickListener {
            // Deselect last selected item
            selectedItemViewHolder?.apply {

                deselect(context)

            }
            // Select current item
            holder.select(context)

            // Save current item to variable
            selectedItemViewHolder = holder


            // Call the other click listeners
            clickListener.onClick(it)
        })

        if (schedule.actionType == "U" && position == 0){
            Log.d("rakib", "${schedule.testTime}")
            holder.select(context)
        }
    }


    companion object DiffUserCallback : DiffUtil.ItemCallback<ScheduleData>() {
        override fun areItemsTheSame(oldItem: ScheduleData, newItem: ScheduleData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ScheduleData, newItem: ScheduleData): Boolean {
            return oldItem?.schlId == newItem?.schlId
        }
    }


    class ScheduleViewHolder(private var binding: ItemScheduleBinding) :
            RecyclerView.ViewHolder(binding.root) {


        companion object {

            fun from(parent: ViewGroup): ScheduleViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemScheduleBinding.inflate(layoutInflater, parent, false)
                return ScheduleViewHolder(binding)
            }
        }


        fun bind(
                schedule: ScheduleData,
                clickListener: ScheduleClickListener
        ) {
            binding.schedule = schedule
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }


        fun select(context: Context) {

            binding.apply {
                scheduleCl.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                scheduleDateTv.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                scheduleTimeTv.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                scheduleLocationTv.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                img1.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite))
                img2.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite))
                img3.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite))
            }
        }

        fun deselect(context: Context) {

            binding.apply {
                scheduleCl.setBackgroundColor(ContextCompat.getColor(context, R.color.unselected))
                scheduleDateTv.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                scheduleTimeTv.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                scheduleLocationTv.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                img1.setColorFilter(ContextCompat.getColor(context, R.color.colorBlack))
                img2.setColorFilter(ContextCompat.getColor(context, R.color.colorBlack))
                img3.setColorFilter(ContextCompat.getColor(context, R.color.colorBlack))
            }
        }
    }
}

class ScheduleClickListener(val clickListener: (id: ScheduleData) -> Unit) {
    fun onClick(scheduleData: ScheduleData) = clickListener(scheduleData)
}


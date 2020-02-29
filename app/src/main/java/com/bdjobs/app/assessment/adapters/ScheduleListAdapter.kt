package com.bdjobs.app.assessment.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.assessment.models.ScheduleData
import com.bdjobs.app.databinding.ItemScheduleBinding
import timber.log.Timber

class ScheduleListAdapter(val context: Context, private val lifecycleOwner: LifecycleOwner, val clickListener: ScheduleClickListener) :
        ListAdapter<ScheduleData, ScheduleListAdapter.ScheduleViewHolder>(DiffUserCallback) {

//    private var selectedItemViewHolder: ScheduleViewHolder? = null

    private val selectedItemIdLive: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = getItem(position)

        holder.bind(
                position,
                schedule,
                context,
                lifecycleOwner,
                //selectedItemIdLive,
                ScheduleClickListener {
                    // Deselect last selected item
//                    selectedItemViewHolder?.apply {

                    //selectedItemIdLive.value = schedule.schlId ?: "-1"

                    clickListener.onClick(it)

                    //deselect(context)

//                    }
                    // Select current item
                    //holder.select(context)

                    // Save current item to variable
                    //selectedItemViewHolder = holder


                    // Call the other click listeners
                    //clickListener.onClick(it)
                })

//        if (schedule.actionType == "U" && position == 0) {
//            Log.d("rakib", "${schedule.testTime}")
//            holder.select(context)
//        } else
//            holder.deselect(context)
    }

    public fun selectItem(id: String) {
        selectedItemIdLive.value = id
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
                position: Int,
                schedule: ScheduleData,
                context: Context,
                lifecycleOwner: LifecycleOwner,
                //liveData: LiveData<String>,
                clickListener: ScheduleClickListener
        ) {
            binding.schedule = schedule
            binding.executePendingBindings()
            binding.clickListener = clickListener

//            schedule.strBookingStatus?.let {
//                //binding.scheduleCl.isClickable = !it.equalIgnoreCase("False")
//                if (it.equalIgnoreCase("False"))
//                    select(context)
//                else
//                    deselect(context)
//            }


            if(schedule.strBookingStatus=="2") {
                select(context)
            } else {
                deselect(context)
            }

            // liveData.observe(lifecycleOwner, Observer {


//                if (it == schedule.schlId) {
//                    select(context)
//                } else {
//                    deselect(context)
//                }

//                if (position == 0 ) {
//                    select(context)
//                } else {
//                    deselect(context)
//                }
//
            //  })
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


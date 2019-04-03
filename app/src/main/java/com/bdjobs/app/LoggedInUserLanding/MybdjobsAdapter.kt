package com.bdjobs.app.LoggedInUserLanding

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MybdjobsData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.logException

class MybdjobsAdapter(val context: Context) : RecyclerView.Adapter<MyBdjobsViewHolder>() {

    val activity = context as Activity
    open var mybdjobsItems: ArrayList<MybdjobsData>? = ArrayList()
    private val communicator = activity as HomeCommunicator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBdjobsViewHolder {
        return MyBdjobsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_grid, parent, false))


    }

    override fun getItemCount(): Int {
        return return if (mybdjobsItems == null) 0 else mybdjobsItems!!.size

    }

    override fun onBindViewHolder(holder: MyBdjobsViewHolder, position: Int) {

        try {
            holder?.itemName?.text = mybdjobsItems?.get(position)?.itemName
            holder?.itemValue?.text = mybdjobsItems?.get(position)?.itemID
            mybdjobsItems?.get(position)?.backgroundID?.let { holder?.backgroundRRL?.setBackgroundResource(it) }
            mybdjobsItems?.get(position)?.resourceID?.let { holder?.item_icon?.setBackgroundResource(it) }
            //holder.itemName[position]
            holder?.item_Card.setOnClickListener {
                if (mybdjobsItems?.get(position)?.itemID?.toInt()!! > 0) {
                    when (mybdjobsItems?.get(position)?.itemName) {
                        Constants.session_key_mybdjobscount_jobs_applied -> communicator?.goToAppliedJobs()
                        Constants.session_key_mybdjobscount_employers_followed -> communicator?.goToFollowedEmployerList("follow")
                        Constants.session_key_mybdjobscount_interview_invitation -> communicator?.goToInterviewInvitation("mybdjobs")
                        Constants.session_key_mybdjobscount_employers_viwed_resume -> {
                            if (Constants.myBdjobsStatsLastMonth) {
                                communicator?.setTime("1")
                            } else {
                                communicator?.setTime("0")
                            }
                            communicator?.goToEmployerViewedMyResume("vwdMyResume")
                        }
                        Constants.session_key_mybdjobscount_times_emailed_resume -> communicator?.gotoTimesEmailedResume(Constants.timesEmailedResumeLast)
                        Constants.session_key_mybdjobscount_message_by_employers -> communicator?.goToMessageByEmployers("employerMessageList")
                        else -> { // Note the block
                            print("not found")
                        }

                    }
                }

            }
        } catch (e: Exception) {
            logException(e)
        }


    }

    fun add(r: MybdjobsData) {
        mybdjobsItems?.add(r)
        notifyItemInserted(mybdjobsItems!!.size - 1)
    }

    fun addCount() {
        mybdjobsItems?.get(0)?.itemID = "222"
        mybdjobsItems?.get(1)?.itemID = "111"
        mybdjobsItems?.get(2)?.itemID = "333"
        // mybdjobsItems?.add(r.itemID)
        //  notifyItemInserted(mybdjobsItems!!.size - 1)
        //   mybdjobsItems?.get()
        notifyDataSetChanged()
    }

    fun addCountAll(moveResults: List<MybdjobsData>) {
        for (result in moveResults) {
            // addCount(result.itemID)
        }
    }

    fun addAll(moveResults: List<MybdjobsData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun removeAll() {
        mybdjobsItems?.clear()
        notifyDataSetChanged()
    }

}

class MyBdjobsViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    var itemName: TextView = itemView?.findViewById(R.id.item_name_TV)
    var itemValue: TextView = itemView?.findViewById(R.id.item_value_TV)
    var backgroundRRL: RelativeLayout = itemView?.findViewById(R.id.background_RRL)
    var item_icon: ImageView = itemView?.findViewById(R.id.iv_item_icon)
    var item_Card: CardView = itemView?.findViewById(R.id.mybdjobsStatsCard)


}
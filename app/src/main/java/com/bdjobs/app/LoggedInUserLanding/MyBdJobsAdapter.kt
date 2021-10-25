@file:Suppress("SpellCheckingInspection")

package com.bdjobs.app.LoggedInUserLanding

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MybdjobsData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.ajkerDeal.ui.home.page_home.HomeNewFragment

class MyBdJobsAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val NORMAL = 0
        private const val AJKER_DEAL = 1
    }

    val activity = context as Activity
    var myBdJobsItems: ArrayList<MybdjobsData>? = ArrayList()
    private val communicator = activity as HomeCommunicator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when(viewType) {
            NORMAL -> {
                viewHolder = MyBdJobsViewHolder(inflater.inflate(R.layout.item_grid, parent, false))
            }
            AJKER_DEAL -> {
                viewHolder = AjkerDealLiveVH(inflater.inflate(R.layout.item_ajker_deal_live,parent,false))
            }
        }

        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return if (myBdJobsItems!=null && myBdJobsItems!!.size>0)  myBdJobsItems!!.size else 0

    }

    override fun getItemViewType(position: Int): Int {
        return  NORMAL
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        try {


            when(getItemViewType(position)) {
                NORMAL -> {
                    val holderMyBdJobs = holder as MyBdJobsViewHolder
                    holderMyBdJobs.itemName.text = myBdJobsItems?.get(position)?.itemName
                    holderMyBdJobs.itemValue.text = myBdJobsItems?.get(position)?.itemID
                    myBdJobsItems?.get(position)?.backgroundID?.let { holderMyBdJobs.backgroundRRL.setBackgroundResource(it) }
                    myBdJobsItems?.get(position)?.resourceID?.let { holderMyBdJobs.itemIcon.setBackgroundResource(it) }
                    holderMyBdJobs.itemCard.setOnClickListener {
                        try {
                            if (myBdJobsItems?.get(position)?.itemID?.toInt()!! > 0) {
                                when (myBdJobsItems?.get(position)?.itemName) {
                                    Constants.session_key_mybdjobscount_jobs_applied -> communicator.goToAppliedJobs()
                                    Constants.session_key_mybdjobscount_employers_followed -> communicator.goToFollowedEmployerList("follow")
                                    Constants.session_key_mybdjobscount_interview_invitation -> communicator.goToInterviewInvitation("mybdjobs")
                                    Constants.session_key_mybdjobscount_video_invitation -> communicator.goToVideoInvitation("mybdjobs")
                                    Constants.session_key_mybdjobscount_live_invitation -> communicator.goToLiveInvitation("mybdjobs")

                                    Constants.session_key_mybdjobscount_employers_viwed_resume -> {
                                        if (Constants.myBdjobsStatsLastMonth) {
                                            communicator.setTime("1")
                                        } else {
                                            communicator.setTime("0")
                                        }
                                        communicator.goToEmployerViewedMyResume("vwdMyResume")
                                    }
                                    Constants.session_key_mybdjobscount_times_emailed_resume -> communicator.gotoTimesEmailedResume(Constants.timesEmailedResumeLast)
                                    Constants.session_key_mybdjobscount_message_by_employers -> communicator.goToMessageByEmployers("employerMessageList")
                                    else -> { // Note the block
                                        print("not found")
                                    }

                                }
                            }
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                }
            }

        } catch (e: Exception) {
            logException(e)
        }
    }

/*
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {

        if (holder is AjkerDealLiveVH) {
            holder.fragment(HomeNewFragment())
        }

        super.onViewAttachedToWindow(holder)
    }
*/

    fun add(r: MybdjobsData) {
        myBdJobsItems?.add(r)
        notifyItemInserted(myBdJobsItems!!.size - 1)
    }

    fun addAll(moveResults: List<MybdjobsData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAll() {
        myBdJobsItems?.clear()
        notifyDataSetChanged()
    }

    inner class AjkerDealLiveVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var container: FrameLayout = itemView.findViewById(R.id.live_container)

        fun fragment(fragment: Fragment) {

            (context as AppCompatActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(container.id, fragment)
                .commitNowAllowingStateLoss()
        }
    }

}

class MyBdJobsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var itemName: TextView = itemView.findViewById(R.id.item_name_TV)
    var itemValue: TextView = itemView.findViewById(R.id.item_value_TV)
    var backgroundRRL: RelativeLayout = itemView.findViewById(R.id.background_RRL)
    var itemIcon: ImageView = itemView.findViewById(R.id.iv_item_icon)
    var itemCard: CardView = itemView.findViewById(R.id.mybdjobsStatsCard)


}
package com.bdjobs.app.LoggedInUserLanding

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MoreHorizontalData
import com.bdjobs.app.R
import com.bdjobs.app.editResume.EditResLandingActivity
import org.jetbrains.anko.startActivity

class HorizontalAdapter(val context: Context) : RecyclerView.Adapter<HorizontalViewHolder>() {

    private var moreItems: ArrayList<MoreHorizontalData>? = ArrayList()
    private val homeCommunicator = context as HomeCommunicator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        return HorizontalViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_data_horizontal_view, parent, false))

    }

    override fun getItemCount(): Int {
        return return if (moreItems == null) 0 else moreItems!!.size
      }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
       // holder.resourceID_Value.background = context.getDrawable(moreItems!![position].resourceID)
        holder.resourceID_Value.setBackgroundResource(moreItems!![position].resourceID)
        holder.resourceName_Value.text = moreItems!![position].resourceName
        holder.itemView.setOnClickListener {
            when(moreItems!![position].resourceName){

                "Favorite\nSearch"->{
                    homeCommunicator.goToFavSearchFilters()
                }
                "Applied\nJobs"->{
                    homeCommunicator.setTime("0")
                    homeCommunicator.goToAppliedJobs()
                }
                "Followed\nEmployers"->{
                    homeCommunicator.goToFollowedEmployerList("follow")
                }
                "Employer\nList"->{
                    homeCommunicator.goToFollowedEmployerList("employer")
                }

                "Manage\nResume"->{
                    context.startActivity<EditResLandingActivity>()
                }
            }
        }




       }

    fun add(r: MoreHorizontalData) {
        moreItems?.add(r)
        notifyItemInserted(moreItems!!.size - 1)
    }

    fun addAll(moveResults: List<MoreHorizontalData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun removeAll() {
        moreItems?.clear()
        notifyDataSetChanged()
    }
}
class HorizontalViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    var resourceID_Value: View = itemView.findViewById(R.id.resourceID)
    var resourceName_Value: TextView = itemView.findViewById(R.id.resourceName)


}
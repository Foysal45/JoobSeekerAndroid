package com.bdjobs.app.LoggedInUserLanding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.HotJobsJobTitle
import com.bdjobs.app.R

class HotjobsTitlesAdapter (private val context : Context) : RecyclerView.Adapter<HotjobsTitlesAdapterViewHolder>() {
    private var hotJobsJobTitleList = ArrayList<HotJobsJobTitle>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotjobsTitlesAdapterViewHolder {
        return HotjobsTitlesAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_hot_joblist_company, parent, false))

    }

    override fun getItemCount(): Int {
        return return if (hotJobsJobTitleList == null) 0 else hotJobsJobTitleList!!.size
    }

    override fun onBindViewHolder(holder: HotjobsTitlesAdapterViewHolder, position: Int) {
        holder?.companyTV.text = hotJobsJobTitleList!![position].jobTitle
    }

    fun add(r: HotJobsJobTitle) {
        hotJobsJobTitleList?.add(r)
        notifyItemInserted(hotJobsJobTitleList!!.size - 1)
    }

    fun addAll(moveResults: List<HotJobsJobTitle>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun removeAll() {
        hotJobsJobTitleList?.clear()
        notifyDataSetChanged()
    }
}
class HotjobsTitlesAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val companyTV = view?.findViewById(R.id.companyNameTV) as TextView
    /*
     val trainingVenue = view?.findViewById(R.id.companyNameTV) as TextView
     val trainingDate = view?.findViewById(R.id.appliedDateTV) as TextView*/

}
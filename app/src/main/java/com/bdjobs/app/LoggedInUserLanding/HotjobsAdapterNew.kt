package com.bdjobs.app.LoggedInUserLanding

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.HotJobsData
import com.bdjobs.app.API.ModelClasses.HotJobsJobTitle
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.loadImageFromUrl

class HotjobsAdapterNew(private val context : Context) : RecyclerView.Adapter<HotJobsViewHolder>() {

    private var hotjoblists = ArrayList<HotJobsData>()
    private var hotjobsTitlesAdapter: HotjobsTitlesAdapter? = null
    //private var hotjobsTitlesAdapter: HotjobsTitlesAdapter? = HotjobsTitlesAdapter(context!!)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotJobsViewHolder {
        return HotJobsViewHolder(LayoutInflater.from(context).inflate(R.layout.hotjobs_item, parent, false))

    }

    override fun getItemCount(): Int {
        return return if (hotjoblists == null) 0 else hotjoblists!!.size
    }

    override fun onBindViewHolder(holder: HotJobsViewHolder, position: Int) {
        holder?.companyTV.text = hotjoblists!![position].companyName
        holder?.companyLogo_IV.loadImageFromUrl(hotjoblists?.get(position)?.logoSource?.trim()!!)
        Log.d("valuev", "==="+hotjoblists?.get(position)?.jobTitles as List<HotJobsJobTitle>)

        hotjobsTitlesAdapter = HotjobsTitlesAdapter(context,hotjoblists?.get(position)?.jobTitles as List<HotJobsJobTitle> )


        holder?.hotjobtitles_RV.adapter = hotjobsTitlesAdapter
        holder?.hotjobtitles_RV?.setHasFixedSize(true)
        //Log.d("initPag", response.body()?.data?.size.toString())
        holder?.hotjobtitles_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
       // hotjobsTitlesAdapter?.addAll(hotjoblists!![position].jobTitles as List<HotJobsJobTitle>)

    }

    fun add(r: HotJobsData) {
        hotjoblists?.add(r)
        notifyItemInserted(hotjoblists!!.size - 1)
    }

    fun addAll(moveResults: List<HotJobsData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun removeAll() {
        hotjoblists?.clear()
        notifyDataSetChanged()
    }


}

class HotJobsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
   val companyTV = view?.findViewById(R.id.companyTV) as TextView
    val hotjobtitles_RV = view?.findViewById(R.id.hotjobtitles_RV) as RecyclerView
    val companyLogo_IV = view?.findViewById(R.id.companyLogo_IV) as ImageView
   /*
    val trainingVenue = view?.findViewById(R.id.companyNameTV) as TextView
    val trainingDate = view?.findViewById(R.id.appliedDateTV) as TextView*/

}
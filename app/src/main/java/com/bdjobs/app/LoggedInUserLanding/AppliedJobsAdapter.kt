package com.bdjobs.app.LoggedInUserLanding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.AppliedJobsData
import com.bdjobs.app.R

class AppliedJobsAdapter(private val context: Context) : RecyclerView.Adapter<AppliedjobsViewHolder>() {
    private var appliedJobsLists: ArrayList<AppliedJobsData>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppliedjobsViewHolder {
        return AppliedjobsViewHolder(LayoutInflater.from(context).inflate(R.layout.applied_jobs, parent, false))
    }

    override fun getItemCount(): Int {
        return return if (appliedJobsLists == null) 0 else appliedJobsLists!!.size
    }

    override fun onBindViewHolder(holder: AppliedjobsViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class AppliedjobsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    /* val employerCompany = view.findViewById(R.id.employers_company_TV) as TextView
     val offeringJobs = view.findViewById(R.id.offering_jobs_number_TV) as TextView
     val followUunfollow = view.findViewById(R.id.follow_unfollow_BTN) as MaterialButton
     val followemployersCard = view.findViewById(R.id.follwEmp_cardview) as CardView*/


}
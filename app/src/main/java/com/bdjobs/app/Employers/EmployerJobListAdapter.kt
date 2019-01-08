package com.bdjobs.app.Employers

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.EmployerJobListsModel
import com.bdjobs.app.API.ModelClasses.EmployerJobListsModelData
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FollowedEmployer
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.google.android.material.button.MaterialButton
import org.jetbrains.anko.startActivity

class EmployerJobListAdapter (private val context: Context) : RecyclerView.Adapter<EmployerJobListViewHolder>() {

    val activity = context as Activity
    val dataStorage = DataStorage(context)
    val bdjobsUserSession = BdjobsUserSession(context)
    private var employerJobList: ArrayList<EmployerJobListsModelData>? = null
    private lateinit var bdjobsDB: BdjobsDB
    private lateinit var company_ID: String
    private lateinit var company_name: String
    private var undoButtonPressed: Boolean = false
    lateinit var employersCommunicator: EmployersCommunicator

    init {
        employerJobList = ArrayList()
        // bdjobsDB = BdjobsDB.getInstance(activity)
        //  employersCommunicator = activity as EmployersCommunicator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployerJobListViewHolder {
        return EmployerJobListViewHolder(LayoutInflater.from(context).inflate(R.layout.employer_joblist, parent, false))

    }
    override fun onBindViewHolder(holder: EmployerJobListViewHolder, position: Int) {
        holder?.employerCompany?.text = employerJobList!![position].jobtitle
        holder?.deadline?.text = employerJobList!![position].deadline
        holder?.itemView?.setOnClickListener {
            val jobids = ArrayList<String>()
            val lns = ArrayList<String>()
            employerJobList?.forEach { data->
                jobids.add(data.jobid)
                lns.add(data.ln)
            }
            context?.startActivity<JobBaseActivity>("from" to "employer","jobids" to jobids,"lns" to lns,"position" to position)
        }

    }
    override fun getItemCount(): Int {
        return return if (employerJobList == null) 0 else employerJobList!!.size
    }

    fun add(r: EmployerJobListsModelData) {
        employerJobList?.add(r)
        notifyItemInserted(employerJobList!!.size - 1)
    }

    fun addAll(moveResults: List<EmployerJobListsModelData>) {
        for (result in moveResults) {
            add(result)
        }
    }

}

class EmployerJobListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    /* val employerCompany = view.findViewById(R.id.employers_company_TV) as TextView
     val offeringJobs = view.findViewById(R.id.offering_jobs_number_TV) as TextView
     val followUunfollow = view.findViewById(R.id.follow_unfollow_BTN) as MaterialButton
     val followemployersCard = view.findViewById(R.id.follwEmp_cardview) as CardView*/

    val employerCompany = view.findViewById(R.id.employers_company_TV) as TextView
    val deadline = view.findViewById(R.id.deadline_TV) as TextView

}


package com.bdjobs.app.Employers

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.EmployerJobListsModelData
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.toSimpleDateString
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
        holder.employerCompany.text = employerJobList?.get(position)?.jobtitle
        val deadlineValue = employerJobList?.get(position)?.deadline
        holder.deadline.text = formatDate(deadlineValue)
        holder.itemView.setOnClickListener {
            val jobids = ArrayList<String>()
            val lns = ArrayList<String>()
            val deadline = ArrayList<String>()
            employerJobList?.forEach { data->
                data.jobid?.let { it1 -> jobids.add(it1) }
                data.ln?.let { it1 -> lns.add(it1) }
                data.deadline?.let { it -> deadline.add(it) }
            }
            //Log.d("hello", employerJobList?.get(position)?.jobid)
            context.startActivity(context.intentFor<JobBaseActivity>("from" to "employer", "jobids" to jobids, "lns" to lns, "position" to position, "deadline" to deadline).singleTop())
        }

    }

    private fun formatDate(deadline: String?):String {
        return if (deadline!=null && deadline!="") {
            SimpleDateFormat("MMM dd, yyyy", Locale.US).parse(deadline)!!.toSimpleDateString()
        } else deadline?:""

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
    val employerCompany = view.findViewById(R.id.employers_company_TV) as TextView
    val deadline = view.findViewById(R.id.deadline_TV) as TextView

}


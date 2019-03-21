package com.bdjobs.app.Employers

import android.app.Activity
import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.BackgroundJob.FollowUnfollowJob
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FollowedEmployer
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.logException
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.toast

/*=============================================================================
 |
 |Author :  Firoz Hasan
 |Date : 22 Dec 2018
 |BASIC IDEA : IN THIS ADAPTER FIRST IN INIT WE RECEIVE THE ARRAYLIST & ON onBindViewHolder
 |             WE SET ONCLICKLISTENER OF UNFOLLOW BUTTON
 |
 |
 |
 +-----------------------------------------------------------------------------
 */

class FollowedEmployersAdapter(private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    val activity = context as Activity
    val dataStorage = DataStorage(context)
    val bdjobsUserSession = BdjobsUserSession(context)
    private var followedEmployerList: ArrayList<FollowedEmployer>? = ArrayList()
    private val bdjobsDB: BdjobsDB = BdjobsDB.getInstance(activity)
    private lateinit var company_ID: String
    private lateinit var company_name: String
    private var undoButtonPressed: Boolean = false
    private val employersCommunicator = activity as EmployersCommunicator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.followed_employers, parent, false))
    }

    override fun getItemCount(): Int {
        return return if (followedEmployerList == null) 0 else followedEmployerList!!.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder?.employerCompany.text = followedEmployerList?.get(position)?.CompanyName
            holder?.offeringJobs.text = followedEmployerList?.get(position)?.JobCount
            company_name = followedEmployerList?.get(position)?.CompanyName!!
            company_ID = followedEmployerList?.get(position)?.CompanyID!!
            holder.followUunfollow.setOnClickListener {
                /*
                here we start removing the unfollow item
                 */

                try {
                    undoButtonPressed = false
                    removeItem(holder.adapterPosition, it)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            var jobCount = followedEmployerList?.get(position)?.JobCount
            if (jobCount == null) {
                jobCount = "0"
            }
            var jobCountint = jobCount?.toInt()
            //      Log.d("crash", "crash $jobCount")


            holder?.itemView?.setOnClickListener {
                if (jobCountint > 0) {
                    try {
                        if (position < followedEmployerList?.size!!) {

                            Log.d("flwd", "position = ${position} list = ${followedEmployerList!!.size}")
                            var company_name_1 = followedEmployerList?.get(position)?.CompanyName!!
                            var company_ID_1 = followedEmployerList?.get(position)?.CompanyID!!
                            employersCommunicator?.gotoJobListFragment(company_ID_1, company_name_1)
                            Log.d("companyid", company_ID_1)
                            Log.d("companyid", company_name_1)
                        }
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            }

        } catch (e: Exception) {
            logException(e)
        }


    }

    fun removeItem(position: Int, view: View) {

        try {
            if (followedEmployerList?.size != 0) {
                val deletedItem = followedEmployerList?.get(position)
                val companyid = followedEmployerList?.get(position)?.CompanyID
                val companyName = followedEmployerList?.get(position)?.CompanyName
                Log.d("werywirye", "companyid = $companyid companyname = $companyName")
                followedEmployerList?.removeAt(position)
                notifyItemRemoved(position)
                try {
                    val deleteJobID = FollowUnfollowJob.scheduleAdvancedJob(companyid!!, companyName!!)
                    undoRemove(view, deletedItem, position, deleteJobID)
                    employersCommunicator.decrementCounter()
                } catch (e: Exception) {

                }

            } else {
                context.toast("No items left here!")
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun undoRemove(v: View, deletedItem: FollowedEmployer?, deletedIndex: Int, deleteJobID: Int) {
        // here we show snackbar and undo option

        val msg = Html.fromHtml("<font color=\"#ffffff\"> This item has been removed! </font>")
        val snack = Snackbar.make(v, "$msg", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    FollowUnfollowJob.cancelJob(deleteJobID)
                    restoreMe(deletedItem!!, deletedIndex)
                    employersCommunicator?.scrollToUndoPosition(deletedIndex)
                    Log.d("comid", "comid = ${deletedItem} ccc = ${deletedIndex}")
                }

        snack?.show()
        Log.d("swipe", "dir to LEFT")
    }

    private fun restoreMe(item: FollowedEmployer, pos: Int) {
        followedEmployerList?.add(pos, item)
        notifyItemInserted(pos)
        undoButtonPressed = true
    }

    fun add(r: FollowedEmployer) {
        followedEmployerList?.add(r)
        notifyItemInserted(followedEmployerList!!.size - 1)
    }

    fun addAll(moveResults: List<FollowedEmployer>) {
        for (result in moveResults) {
            add(result)
        }
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val employerCompany = view?.findViewById(R.id.employers_company_TV) as TextView
    val offeringJobs = view?.findViewById(R.id.offering_jobs_number_TV) as TextView
    val followUunfollow = view?.findViewById(R.id.follow_unfollow_BTN) as MaterialButton
    val followemployersCard = view?.findViewById(R.id.follwEmp_cardview) as CardView


}
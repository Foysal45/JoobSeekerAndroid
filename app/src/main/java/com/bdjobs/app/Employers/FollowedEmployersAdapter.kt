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
import com.bdjobs.app.API.ModelClasses.FollowEmployerListData
import com.bdjobs.app.BackgroundJob.FollowUnfollowJob
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.logException
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton


class FollowedEmployersAdapter(private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    val activity = context as Activity
    val dataStorage = DataStorage(context)
    val bdjobsUserSession = BdjobsUserSession(context)
    private var followedEmployerList: ArrayList<FollowEmployerListData>? = ArrayList()
    private lateinit var company_ID: String
    private lateinit var company_name: String
    private var undoButtonPressed: Boolean = false
    private val employersCommunicator = activity as EmployersCommunicator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.followed_employers, parent, false))
    }

    override fun getItemCount(): Int {
        return if (followedEmployerList == null) 0 else followedEmployerList!!.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder?.employerCompany.text = followedEmployerList?.get(position)?.companyName
            holder?.offeringJobs.text = followedEmployerList?.get(position)?.jobCount
            company_name = followedEmployerList?.get(position)?.companyName!!
            company_ID = followedEmployerList?.get(position)?.companyID!!
            holder.followUunfollow?.setOnClickListener {

                try {
                    activity?.alert("Are you sure you want to unfollow this company?", "Confirmation") {
                        yesButton {
                            undoButtonPressed = false
                            removeItem(holder.adapterPosition)

                        }
                        noButton { dialog ->
                            dialog.dismiss()
                        }
                    }.show()

                } catch (e: Exception) {
                    logException(e)
                }
            }

            var jobCount = followedEmployerList?.get(position)?.jobCount
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
                            var company_name_1 = followedEmployerList?.get(position)?.companyName!!
                            var company_ID_1 = followedEmployerList?.get(position)?.companyID!!
                            employersCommunicator?.gotoJobListFragment(company_ID_1, company_name_1)
                            employersCommunicator?.positionClicked(position)
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

    fun removeItem(position: Int) {

        try {
            if (followedEmployerList?.size != 0) {
                val deletedItem = followedEmployerList?.get(position)
                val companyid = followedEmployerList?.get(position)?.companyID
                val companyName = followedEmployerList?.get(position)?.companyName
                Log.d("werywirye", "companyid = $companyid companyname = $companyName")
                followedEmployerList?.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeRemoved(position, followedEmployerList?.size!!)
                try {
                    val deleteJobID = FollowUnfollowJob.scheduleAdvancedJob(companyid!!, companyName!!)
                    bdjobsUserSession?.deccrementFollowedEmployer()
                    //undoRemove(view, deletedItem, position, deleteJobID)
                    employersCommunicator.decrementCounter(position)
                } catch (e: Exception) {

                }

            } else {
                context.toast("No items left here!")
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun undoRemove(v: View, deletedItem: FollowEmployerListData?, deletedIndex: Int, deleteJobID: Int) {
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

    private fun restoreMe(item: FollowEmployerListData, pos: Int) {
        followedEmployerList?.add(pos, item)
        notifyItemInserted(pos)
        undoButtonPressed = true
    }

    fun add(r: FollowEmployerListData) {
        followedEmployerList?.add(r)
        notifyItemInserted(followedEmployerList!!.size - 1)
        employersCommunicator.setFollowedEmployerList(followedEmployerList)
    }

    fun addAll(moveResults: List<FollowEmployerListData>) {
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
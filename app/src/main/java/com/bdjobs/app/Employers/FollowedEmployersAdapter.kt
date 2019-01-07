package com.bdjobs.app.Employers

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.FollowedEmployer
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import android.R.attr.data
import android.app.PendingIntent.getActivity
import android.graphics.Color
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.Gravity
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FollowUnfollowModelClass
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        holder?.employerCompany.text = followedEmployerList!![position].CompanyName
        holder?.offeringJobs.text = followedEmployerList!![position].JobCount
        company_name = followedEmployerList!![position].CompanyName!!
        company_ID = followedEmployerList!![position].CompanyID!!
        holder.followUunfollow.setOnClickListener {
            /*
            here we start removing the unfollow item
             */
            undoButtonPressed = false
            rmv(holder.adapterPosition, it)
        }

        var jobCount = followedEmployerList!![position].JobCount
        var jobCountint = jobCount?.toInt()

        if (jobCountint!! > 0) {
            holder?.followemployersCard?.setOnClickListener {
                var company_name_1 = followedEmployerList!![position].CompanyName!!
                var company_ID_1 = followedEmployerList!![position].CompanyID!!
                employersCommunicator?.gotoJobListFragment(company_ID_1, company_name_1)
                Log.d("companyid", company_ID_1)
                Log.d("companyid", company_name_1)

            }
        }


    }

    fun rmv(position: Int, view: View) {
        if (followedEmployerList?.size != 0) {
            val deletedItem = followedEmployerList?.get(position)
            followedEmployerList?.removeAt(position)
            notifyItemRemoved(position)
            undoRemove(view, deletedItem, position)
        } else {
            context.toast("No items left here!")
        }
    }

    private fun undoRemove(v: View, deletedItem: FollowedEmployer?, deletedIndex: Int) {
        // here we show snackbar and undo option

        val msg = Html.fromHtml("<font color=\"#ffffff\"> This item has been removed! </font>")
        val snack = Snackbar.make(v, "$msg", Snackbar.LENGTH_INDEFINITE)
                .setAction("UNDO") {
                    //    "Applicant $name has been restored successfully!".toast(activity!!)
                    // call?.doNothing(page, applyIDs(), applicantStatus())
                    //  Log.d("checkingUndo", "${applyIDs()} and ${applicantStatus()}")

                    restoreMe(deletedItem!!, deletedIndex)
                    Log.d("comid", "comid")
                    // restore the undo item
                }
        snack?.setActionTextColor(context.resources.getColor(R.color.undo))
        snack?.duration = 5000
        val view = snack.view
        //view.layoutParams.height = 100
        val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        view?.setBackgroundColor(Color.DKGRAY)
        tv?.setTextColor(Color.WHITE)
        tv?.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv?.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        snack?.show()
        Log.d("swipe", "dir to LEFT")
        snack?.addCallback(object : Snackbar.Callback() {
            override fun onShown(snackbar: Snackbar?) {
                //  on show
                //    Log.d("comid", "shown")
            }

            override fun onDismissed(snackbar: Snackbar?, event: Int) {
                //   Log.d("comid", "dismissed")
                Log.d("comid", "$undoButtonPressed")
                if (undoButtonPressed == false) {
                    // deleting the item from db and server
                    deleteFromServer(company_ID, company_name)
                    deleteFromDB(company_ID)
                }

            }

        })
    }

    private fun deleteFromServer(companyid: String, companyName: String) {
        ApiServiceJobs.create().getUnfollowMessage(id = companyid, name = company_name, userId = bdjobsUserSession.userId, encoded = Constants.ENCODED_JOBS, actType = "fed", decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<FollowUnfollowModelClass> {
            override fun onFailure(call: Call<FollowUnfollowModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FollowUnfollowModelClass>, response: Response<FollowUnfollowModelClass>) {

                try {
                    var statuscode = response.body()?.statuscode
                    var message = response.body()?.data?.get(0)?.message
//                Log.d("msg", message)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    private fun deleteFromDB(companyid: String) {
        Log.d("comid", "comid")
        Log.d("comid", "$companyid")
        doAsync {

            bdjobsDB.followedEmployerDao().deleteFollowedEmployerByCompanyID(companyid)

        }
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
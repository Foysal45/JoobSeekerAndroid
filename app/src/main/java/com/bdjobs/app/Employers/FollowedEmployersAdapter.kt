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
import com.bdjobs.app.Databases.Internal.BdjobsDB
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


class FollowedEmployersAdapter (private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    val activity = context as Activity
    val dataStorage = DataStorage(context)
    val bdjobsUserSession = BdjobsUserSession(context)
    private var followedEmployerList: ArrayList<FollowedEmployer>? = null
    private lateinit var bdjobsDB: BdjobsDB
    private lateinit var companyIDFmDB: String
    private  var undo : Boolean = false

    init {
        followedEmployerList = ArrayList()
        bdjobsDB = BdjobsDB.getInstance(activity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.followed_employers, parent, false))
    }

    override fun getItemCount(): Int {
     return followedEmployerList!!.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.employerCompany.text = followedEmployerList!![position].CompanyName
        holder.offeringJobs.text = followedEmployerList!![position].JobCount

        holder.followUunfollow.setOnClickListener {
            /*Toast.makeText(context, holder.adapterPosition.toString(),
                    Toast.LENGTH_LONG).show()*/
            companyIDFmDB = followedEmployerList!![position].CompanyID!!

            Toast.makeText(context, companyIDFmDB,
                    Toast.LENGTH_LONG).show()
            rmv(position,it)
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

    private fun undoRemove(v: View,
                           deletedItem: FollowedEmployer?,
                           deletedIndex: Int
    ) {

        val msg = Html.fromHtml("<font color=\"#ffffff\"> This item has been removed! </font>")



        val snack = Snackbar.make(v, "$msg", Snackbar.LENGTH_INDEFINITE)
                .setAction("UNDO") {
                    //    "Applicant $name has been restored successfully!".toast(activity!!)
                   // call?.doNothing(page, applyIDs(), applicantStatus())
                  //  Log.d("checkingUndo", "${applyIDs()} and ${applicantStatus()}")
                    restoreMe(deletedItem!!, deletedIndex)
                    Log.d("comid", "comid")
                }
        snack.setActionTextColor(context.resources.getColor(R.color.undo))
        snack.duration = 5000


        val view = snack.view
        //view.layoutParams.height = 100
        val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        view.setBackgroundColor(Color.DKGRAY)
        tv.setTextColor(Color.WHITE)
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        snack.show()
        Log.d("swipe", "dir to LEFT")

        delayDeleteFollowedEmployer()



    }

    private fun delayDeleteFollowedEmployer(){
        Handler().postDelayed({

            //bdjobsDB.followedEmployerDao().deleteFollowedEmployerByCompanyID(companyIDFmDB)
            doAsync {
                uiThread {
                    if(undo == false){
                        Log.d("comid", "comid")
                        bdjobsDB.followedEmployerDao().deleteFollowedEmployerByCompanyID(companyIDFmDB)
                    }
                }

            }




        }, 5000)
    }

    private fun restoreMe(item: FollowedEmployer, pos: Int) {
        followedEmployerList?.add(pos, item)
        notifyItemInserted(pos)
        undo = true
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
    val employerCompany = view.findViewById(R.id.employers_company_TV) as TextView
    val offeringJobs = view.findViewById(R.id.offering_jobs_number_TV) as TextView
    val followUunfollow = view.findViewById(R.id.follow_unfollow_BTN) as MaterialButton
    val followemployersCard = view.findViewById(R.id.follwEmp_cardview) as CardView


}
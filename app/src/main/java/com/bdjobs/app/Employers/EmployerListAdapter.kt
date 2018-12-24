package com.bdjobs.app.Employers

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.EmployerJobListsModelData
import com.bdjobs.app.API.ModelClasses.EmployerListModelClass
import com.bdjobs.app.API.ModelClasses.EmployerListModelData
import com.bdjobs.app.API.ModelClasses.FollowUnfollowModelClass
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployerListAdapter(private var context: Context) : RecyclerView.Adapter<EmployerListViewHolder>() {

    private var employerList: ArrayList<EmployerListModelData>? = ArrayList()
    val bdjobsUserSession = BdjobsUserSession(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployerListViewHolder {

        return EmployerListViewHolder(LayoutInflater.from(context).inflate(R.layout.employers_list, parent, false))

    }

    override fun getItemCount(): Int {
        return return if (employerList == null) 0 else employerList!!.size

    }

    override fun onBindViewHolder(holder: EmployerListViewHolder, position: Int) {

        holder.employerCompany.text = employerList!![position].companyname
        holder.offeringJobs.text = employerList!![position].totaljobs
        holder.followUnfollow.setOnClickListener {
            Log.d("hhh", holder.followUnfollow.text as String?)
           var getFollow = holder.followUnfollow.text

            if (getFollow == "Follow")
            {
                holder.followUnfollow.text = "UNFOLLOW"
                var company_name_1 = employerList!![position].companyname!!
                var company_ID_1 = employerList!![position].companyid!!
                callFollowApi(company_ID_1, company_name_1)
            }
                else if (getFollow == "UNFOLLOW"){
                holder.followUnfollow.text = "Follow"
                var company_name_2 = employerList!![position].companyname!!
                var company_ID_2 = employerList!![position].companyid!!
                callUnFollowApi(company_ID_2, company_name_2)
            }
          /*  if ( holder.followUnfollow.text == "Follow"){
                holder.followUnfollow.text = "Unfollow"
                var company_name_1 = employerList!![position].companyname!!
                var company_ID_1 = employerList!![position].companyid!!
                callFollowApi(company_ID_1, company_name_1)
            }
            else if ( holder.followUnfollow.text == "UnFollow"){
                holder.followUnfollow.text = "Follow"
                var company_name_2 = employerList!![position].companyname!!
                var company_ID_2 = employerList!![position].companyid!!
                callUnFollowApi(company_ID_2, company_name_2)
            }*/

        }

    }

    fun add(r: EmployerListModelData) {
        employerList?.add(r)
        notifyItemInserted(employerList!!.size - 1)
    }

    fun addAll(moveResults: List<EmployerListModelData>) {
        for (result in moveResults) {
            add(result)
        }
    }
    private fun callFollowApi(companyid: String, companyName: String) {
        ApiServiceJobs.create().getUnfollowMessage(id = companyid, name = companyName, userId = bdjobsUserSession.userId, encoded = Constants.ENCODED_JOBS, actType = "fei", decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<FollowUnfollowModelClass> {
            override fun onFailure(call: Call<FollowUnfollowModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FollowUnfollowModelClass>, response: Response<FollowUnfollowModelClass>) {

                var statuscode = response.body()?.statuscode
                var message = response.body()?.data?.get(0)?.message
//                Log.d("msg", message)
                Toast.makeText(context, statuscode + "---" + message, Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun callUnFollowApi(companyid: String, companyName: String) {
        ApiServiceJobs.create().getUnfollowMessage(id = companyid, name = companyName, userId = bdjobsUserSession.userId, encoded = Constants.ENCODED_JOBS, actType = "fed", decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<FollowUnfollowModelClass> {
            override fun onFailure(call: Call<FollowUnfollowModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FollowUnfollowModelClass>, response: Response<FollowUnfollowModelClass>) {

                var statuscode = response.body()?.statuscode
                var message = response.body()?.data?.get(0)?.message
//                Log.d("msg", message)
                Toast.makeText(context, statuscode + "---" + message, Toast.LENGTH_LONG).show()
            }

        })
    }
}

class EmployerListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    /* val employerCompany = view.findViewById(R.id.employers_company_TV) as TextView
     val offeringJobs = view.findViewById(R.id.offering_jobs_number_TV) as TextView
     val followUunfollow = view.findViewById(R.id.follow_unfollow_BTN) as MaterialButton
     val followemployersCard = view.findViewById(R.id.follwEmp_cardview) as CardView*/

    val employerCompany = view.findViewById(R.id.employers_company_TV) as TextView
    val offeringJobs = view.findViewById(R.id.offering_jobs_number_TV) as TextView
    val followUnfollow = view.findViewById(R.id.follownfollow_BTN) as MaterialButton

}
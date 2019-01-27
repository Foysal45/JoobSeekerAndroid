package com.bdjobs.app.Employers

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.EmployerListModelData
import com.bdjobs.app.API.ModelClasses.FollowUnfollowModelClass
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FollowedEmployer
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import com.google.android.material.button.MaterialButton
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class EmployerListAdapter(private var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var employerList: ArrayList<EmployerListModelData>? = ArrayList()
    val bdjobsUserSession = BdjobsUserSession(context)
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    val activity = context as Activity
    private val bdjobsDB: BdjobsDB = BdjobsDB.getInstance(activity)
    private val employersCommunicator = activity as EmployersCommunicator

    companion object {
        // View Types
        private val ITEM = 0
        private val LOADING = 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val itemHolder = holder as EmployerListViewHolder
                bindViews(itemHolder, position)

            }
            LOADING -> {
                val loadingVH = holder as LoadingVH

                if (retryPageLoad) {
                    loadingVH?.mErrorLayout?.visibility = View.VISIBLE
                    loadingVH?.mProgressBar?.visibility = View.GONE

                    loadingVH?.mErrorTxt?.text = if (errorMsg != null)
                        errorMsg
                    else
                        context?.getString(R.string.error_msg_unknown)

                } else {
                    loadingVH?.mErrorLayout?.visibility = View.GONE
                    loadingVH?.mProgressBar?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == employerList!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    private fun bindViews(holder: EmployerListViewHolder, position: Int) {

        holder?.employerCompany?.text = employerList!![position].companyname
        holder?.offeringJobs?.text = employerList!![position].totaljobs

        var jobCount = employerList!![position].totaljobs
        var jobCountint = jobCount?.toInt()

        if (jobCountint!! > 0) {
            holder.employersListCard.setOnClickListener {
                val company_name_1 = employerList!![position].companyname!!
                val company_ID_1 = employerList!![position].companyid!!
                employersCommunicator.gotoJobListFragment(company_ID_1, company_name_1)
                Log.d("companyid", company_ID_1)
                Log.d("companyid", company_name_1)

            }
        }

        doAsync {
            val company_ID = employerList!![position].companyid!!
            val companyName=employerList!![position].companyname!!
            val isitFollowed = bdjobsDB.followedEmployerDao().isItFollowed(company_ID,companyName)

            uiThread {
                if (isitFollowed) {
                    holder?.followUnfollow?.text = "UNFOLLOW"
                } else {
                    holder?.followUnfollow?.text = "FOLLOW"
                }
            }
        }
        holder?.followUnfollow?.setOnClickListener {
            followUnfollowEmployer(position)
        }
    }

    fun followUnfollowEmployer(position: Int) {
        doAsync {
            val company_ID = employerList!![position].companyid!!
            val company_NAME = employerList!![position].companyname!!
            val jobcount = employerList!![position].totaljobs!!

            val isitFollowed = bdjobsDB.followedEmployerDao().isItFollowed(company_ID,company_NAME)

            Log.d("companyinfo", "companyid: $company_ID \n companyname: $company_NAME \n isitFollowed: $isitFollowed")


            uiThread {
                if (isitFollowed) {
                    //do_work_to_unfollow_a_employer
                    callUnFollowApi(company_ID, company_NAME)
                    // notifyDataSetChanged()
                } else {
                    //do_work_to_follow_a_employer
                    callFollowApi(company_ID, company_NAME)
                    //  notifyDataSetChanged()
                }
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> {
                val viewItem = inflater.inflate(R.layout.employers_list, parent, false)
                viewHolder = EmployerListViewHolder(viewItem)
            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress_1, parent, false)
                viewHolder = LoadingVH(viewLoading)
            }
        }

        return viewHolder!!


    }

    override fun getItemCount(): Int {
        return return if (employerList == null) 0 else employerList!!.size

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

    fun removeAll() {
        employerList?.clear()
        notifyDataSetChanged()
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
        // add(EmployerListModelData())
    }

    private fun getItem(position: Int): EmployerListModelData? {
        return employerList!![position]
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = employerList!!.size - 1
        val result = getItem(position)
        notifyItemRemoved(position)

        if (result != null) {
            employerList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun callFollowApi(companyid: String, companyname: String) {
        ApiServiceJobs.create().getUnfollowMessage(id = companyid, name = companyname, userId = bdjobsUserSession.userId, encoded = Constants.ENCODED_JOBS, actType = "fei", decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<FollowUnfollowModelClass> {
            override fun onFailure(call: Call<FollowUnfollowModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FollowUnfollowModelClass>, response: Response<FollowUnfollowModelClass>) {
                try {
                    val statuscode = response?.body()?.statuscode
                    val message = response?.body()?.data?.get(0)?.message
                    Log.d("jobCount", "jobCount: ${response.body()?.data?.get(0)?.jobcount}")

                    doAsync {
                        val followedEmployer = FollowedEmployer(
                                CompanyID = companyid,
                                CompanyName = companyname,
                                JobCount = response.body()?.data?.get(0)?.jobcount,
                                FollowedOn = Date()
                        )

                        bdjobsDB.followedEmployerDao().insertFollowedEmployer(followedEmployer)

                        uiThread {
                            notifyDataSetChanged()
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }

                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    private fun callUnFollowApi(companyid: String, companyName: String) {
        ApiServiceJobs.create().getUnfollowMessage(id = companyid, name = companyName, userId = bdjobsUserSession.userId, encoded = Constants.ENCODED_JOBS, actType = "fed", decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<FollowUnfollowModelClass> {
            override fun onFailure(call: Call<FollowUnfollowModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FollowUnfollowModelClass>, response: Response<FollowUnfollowModelClass>) {

                try {
                    var statuscode = response.body()?.statuscode
                    var message = response.body()?.data?.get(0)?.message
                    Log.d("msg", message)
                    doAsync {
                        bdjobsDB.followedEmployerDao().deleteFollowedEmployerByCompanyID(companyid,companyName)

                        uiThread {
                            notifyDataSetChanged()
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }

        })
    }
}

class EmployerListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val employerCompany = view?.findViewById(R.id.employers_company_TV) as TextView
    val offeringJobs = view?.findViewById(R.id.offering_jobs_number_TV) as TextView
    val followUnfollow = view?.findViewById(R.id.follownfollow_BTN) as MaterialButton
    val employersListCard = view?.findViewById(R.id.empList_cardview) as CardView

}

class LoadingVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
    val mProgressBar: ProgressBar = itemView?.findViewById(R.id.loadmore_progress_1) as ProgressBar
    val mRetryBtn: ImageButton = itemView?.findViewById(R.id.loadmore_retry_1) as ImageButton
    val mErrorTxt: TextView = itemView?.findViewById(R.id.loadmore_errortxt_1) as TextView
    val mErrorLayout: LinearLayout = itemView?.findViewById(R.id.loadmore_errorlayout_1) as LinearLayout

    init {

        mRetryBtn?.setOnClickListener(this)
        mErrorLayout?.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.loadmore_retry, R.id.loadmore_errorlayout -> {
                /*  adapter?.showRetry(false, null)
                  mCallback?.retryPageLoad()*/
            }
        }
    }
}
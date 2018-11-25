package com.bdjobs.app.Login

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LoginSessionModel
import com.bdjobs.app.API.ModelClasses.SocialLoginAccountListData
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.base_url_mybdjobs_photo
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SocialRVadapter(private val items: List<SocialLoginAccountListData?>?, val context: Context, private val loadingProgressBar: ProgressBar? = null) : RecyclerView.Adapter<ViewHolder>() {

    val activity = context as Activity
    val loginCommunicator = context as LoginCommunicator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.social_account_list_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTV.text = items?.get(position)?.fullName
        holder.emailTV.text = items?.get(position)?.email

        Log.d("CheckPhotoUrl", "Url: ${items?.get(position)?.photo} ")

        val photoUrl = base_url_mybdjobs_photo + items?.get(position)?.photo
        holder.profilePicIMGV.loadCircularImageFromUrl(photoUrl)
        holder.mainCL.setOnClickListener {
            doLogin(items?.get(position))
        }

    }

    override fun getItemCount(): Int {
        return items?.size!!
    }

    private fun doLogin(mappedAccount: SocialLoginAccountListData?) {
        loadingProgressBar?.let { pbar ->
            activity.showProgressBar(pbar)
        }
        ApiServiceMyBdjobs.create().doLogin(socialMediaId = mappedAccount?.socialMediaId,
                socialMediaName = mappedAccount?.socialMediaName,
                susername = mappedAccount?.susername,
                email = mappedAccount?.email,
                fullName = mappedAccount?.fullName,
                userId = mappedAccount?.userId,
                isMap = mappedAccount?.isMap,
                smId = mappedAccount?.smId
        ).enqueue(object : Callback<LoginSessionModel> {

            override fun onFailure(call: Call<LoginSessionModel>, t: Throwable) {
                error("onFailure", t)
                loadingProgressBar?.let { pbar ->
                    activity.stopProgressBar(pbar)
                }
            }

            override fun onResponse(call: Call<LoginSessionModel>, response: Response<LoginSessionModel>) {
                if (response.body()?.statuscode!!.equalIgnoreCase(Constants.api_request_result_code_ok)) {
                    response.body()?.data?.get(0)?.let { sessionData ->
                        val bdjobsUserSession = BdjobsUserSession(activity)
                        bdjobsUserSession.createSession(sessionData)
                        loginCommunicator.goToHomePage(progressBar = loadingProgressBar)
                    }

                } else {
                    loadingProgressBar?.let { pbar ->
                        activity.stopProgressBar(pbar)
                    }
                    activity.toast(response.body()?.message!!)
                }
            }
        })

    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val nameTV = view.findViewById(R.id.nameTV) as TextView
    val emailTV = view.findViewById(R.id.emailTV) as TextView
    val profilePicIMGV = view.findViewById(R.id.profilePicIMGV) as ImageView
    val mainCL = view.findViewById(R.id.mainCL) as ConstraintLayout
}
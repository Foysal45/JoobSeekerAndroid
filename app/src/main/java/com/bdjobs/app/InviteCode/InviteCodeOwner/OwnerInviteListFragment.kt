package com.bdjobs.app.InviteCode.InviteCodeOwner

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.InviteCodeUserStatusModel
import com.bdjobs.app.API.ModelClasses.OwnerInviteListModel
import com.bdjobs.app.API.ModelClasses.OwnerInviteListModelData
import com.bdjobs.app.InviteCode.InviteCodeCommunicator
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.invite_code_owner_invite_list_fragment.*
import org.jetbrains.anko.indeterminateProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OwnerInviteListFragment : Fragment() {

    private var topBottomPadding: Int = 0
    private var leftRightPadding: Int = 0
    private var state: Int = 0
    private var verifyStatus = "1"
    private var bdjobsUserSession: BdjobsUserSession? = null
    private var inviteCodeCommunicator: InviteCodeCommunicator? = null

    private var dataArray : ArrayList<OwnerInviteListModelData>?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.invite_code_owner_invite_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity!!)
        inviteCodeCommunicator = activity as InviteCodeCommunicator

        onClickListeners()

        val scale = resources.displayMetrics.density
        topBottomPadding = (8 * scale + 0.5f).toInt()
        leftRightPadding = (16 * scale + 0.5f).toInt()

    }


    private fun onClickListeners() {
        successTV.setOnClickListener(View.OnClickListener {
            if (state != 0) {
                successSelected()
            }
        })
        pendingTV.setOnClickListener(View.OnClickListener {
            if (state != 1) {
                pendingSelected()
            }
        })

        accountLV.onItemClickListener = AdapterView.OnItemClickListener { arg0, arg1, position, arg3 ->

             val invitedUserId = dataArray?.get(position)?.userID
             Log.d("SendingData",
                     "invitedUserId " + invitedUserId +
                     "\n position " + position
             )
             getUserStatus(bdjobsUserSession?.userId!!, bdjobsUserSession?.decodId!!, invitedUserId!!)
        }

    }

    private fun pendingSelected() {
        pendingTV.setTextColor(Color.parseColor("#FFFFFF"))
        pendingTV.setBackgroundResource(R.drawable.pending_active_bg)
        successTV.setTextColor(Color.parseColor("#424242"))
        successTV.setBackgroundResource(R.drawable.success_inactive_bg)
        successTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
        pendingTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
        state = 1
        verifyStatus = "0"
        getInviteList(bdjobsUserSession?.userId!!, bdjobsUserSession?.decodId!!, inviteCodeCommunicator?.getInviteCodepcOwnerID()!!, verifyStatus)
    }

    private fun getInviteList(userId: String, decodeId: String, pcOwnerId: String, verifyStatus: String) {
        accountLV.hide()
        shimmer_view_container_JobList.show()
        shimmer_view_container_JobList.startShimmerAnimation()

        ApiServiceMyBdjobs.create().getOwnerInviteList(
                userID = userId,
                decodeID = decodeId,
                pcOwnerId = pcOwnerId,
                verify_status = verifyStatus

        ).enqueue(object : Callback<OwnerInviteListModel> {
            override fun onFailure(call: Call<OwnerInviteListModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<OwnerInviteListModel>, response: Response<OwnerInviteListModel>) =
                    try {

                        shimmer_view_container_JobList.hide()
                        shimmer_view_container_JobList.stopShimmerAnimation()

                        if (!response.isSuccessful) {

                        } else {
                            var account_data = response.body()!!.common.totalInvitation.toBanglaDigit()
                            if (verifyStatus.equals("0", ignoreCase = true)) {

                                try {
                                    account_data += " (পেন্ডিং : ${response.body()?.data?.size?.toString()?.toBanglaDigit()})"
                                } catch (e: Exception) {

                                    e.printStackTrace()
                                    account_data += " (পেন্ডিং : ০)"
                                }

                            } else if (verifyStatus.equals("1", ignoreCase = true)) {

                                try {
                                    account_data += " (সফল : ${response.body()?.data?.size?.toString()?.toBanglaDigit()})"
                                } catch (e: Exception) {

                                    e.printStackTrace()
                                    account_data += " (সফল : ০)"
                                }

                            }
                            numberTV.setText(account_data)


                            if (response.body() != null && response.body()!!.data.isNotEmpty()) {
                                dataArray = response?.body()?.data as ArrayList<OwnerInviteListModelData>
                                val accountListAdapter = InvitecodeInviteListAdapter(activity!!, response?.body()?.data as ArrayList<OwnerInviteListModelData>, state)
                                accountLV.adapter = accountListAdapter
                                accountLV.show()

                            } else {
                                if (verifyStatus.equals("1", ignoreCase = true)) {
                                    Toast.makeText(activity, "আপনার সফল ইনভাইট লিস্টে কোন তথ্য নেই।", Toast.LENGTH_LONG).show()
                                } else if (verifyStatus.equals("0", ignoreCase = true)) {
                                    Toast.makeText(activity, "আপনার পেন্ডিং ইনভাইট লিস্টে কোন তথ্য নেই।", Toast.LENGTH_LONG).show()
                                } else {

                                }
                            }
                        }
                    } catch (e: Exception) {
                        logException(e)
                    }
        })


    }

    private fun successSelected() {
        successTV.setTextColor(Color.parseColor("#FFFFFF"))
        successTV.setBackgroundResource(R.drawable.success_active_bg)
        pendingTV.setTextColor(Color.parseColor("#424242"))
        pendingTV.setBackgroundResource(R.drawable.pending_inactive_bg)
        successTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
        pendingTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
        state = 0
        verifyStatus = "1"

        getInviteList(bdjobsUserSession?.userId!!, bdjobsUserSession?.decodId!!, inviteCodeCommunicator?.getInviteCodepcOwnerID()!!, verifyStatus)
    }

    override fun onResume() {
        super.onResume()

        Log.d("status_code", state.toString())

        if (state == 0) {
            successSelected()
        }
        if (state == 1) {
            pendingSelected()
        }
    }


    private fun getUserStatus(userId:String,decodeId:String,invitedUserId:String){
        val loadingDialog = activity?.indeterminateProgressDialog("তথ্য আনা হচ্ছে")
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
        ApiServiceMyBdjobs.create().getInviteCodeUserStatus(
                userID = userId,
                decodeID = decodeId,
                invited_user_id = invitedUserId
        ).enqueue(
                object :Callback<InviteCodeUserStatusModel>{
                    override fun onFailure(call: Call<InviteCodeUserStatusModel>, t: Throwable) {
                        error("onFailure",t)
                        loadingDialog?.dismiss()
                    }

                    override fun onResponse(call: Call<InviteCodeUserStatusModel>, response: Response<InviteCodeUserStatusModel>) {
                        try {
                            loadingDialog?.dismiss()
                            if (response.isSuccessful) {

                                showCategoryDialog(
                                        response.body()!!.data[0].name,
                                        response.body()!!.data[0].category,
                                        response.body()!!.data[0].photoUrl,
                                        response.body()!!.data[0].personalInfo,
                                        response.body()!!.data[0].educationInfo,
                                        response.body()!!.data[0].photoInfo,
                                        response.body()!!.data[0].createdDate
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }
        )



    }

    private fun showCategoryDialog(name: String, category: String, photoUrl: String, personalInfo: String, educationInfo: String, photoInfo: String, createdDate: String) {

        Log.d("showCategoryDialog","name: $name")

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.invite_user_details_layout)


        val nameTV = dialog.findViewById(R.id.nameTV) as TextView
        val categoryTv = dialog.findViewById(R.id.categoryTv) as TextView
        val categoryDateTv = dialog.findViewById(R.id.categoryDateTv) as TextView
        val profilePicIV = dialog.findViewById(R.id.profilePicIMGV) as ImageView
        val personalImageView = dialog.findViewById(R.id.personalIMGV) as ImageView
        val educationaImageView = dialog.findViewById(R.id.educationIMGV) as ImageView
        val photInfoImageView = dialog.findViewById(R.id.pictureIMGV) as ImageView


        nameTV.text = name
        categoryTv.text = category
        categoryDateTv.text = createdDate.toBanglaDigit()


        profilePicIV.loadCircularImageFromUrl(photoUrl)


        if (educationInfo.equals("True", ignoreCase = true)) {

            personalImageView.setBackgroundResource(R.drawable.acount_right_icon)

        } else {

            personalImageView.setBackgroundResource(R.drawable.account_cross_icon)
        }

        if (personalInfo.equals("True", ignoreCase = true)) {

            educationaImageView.setBackgroundResource(R.drawable.acount_right_icon)

        } else {

            educationaImageView.setBackgroundResource(R.drawable.account_cross_icon)

        }

        if (photoInfo.equals("True", ignoreCase = true)) {

            photInfoImageView.setBackgroundResource(R.drawable.acount_right_icon)

        } else {

            photInfoImageView.setBackgroundResource(R.drawable.account_cross_icon)

        }




        val cancelIconImgv = dialog.findViewById(R.id.cancelIconImgv) as ImageView

        dialog.setCancelable(true)
        dialog.show()



        cancelIconImgv.setOnClickListener {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
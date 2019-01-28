package com.bdjobs.app.InviteCode.InviteCodeOwner

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.OwnerInviteListModel
import com.bdjobs.app.API.ModelClasses.OwnerInviteListModelData
import com.bdjobs.app.InviteCode.InviteCodeCommunicator
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.invite_code_owner_invite_list_fragment.*
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

            /* val invitedUserId = dataArray.get(position).getUserID()
             Log.d("SendingData", " userId " + userId +
                     "\n decodeId " + decodeId +
                     "\n invitedUserId " + invitedUserId +
                     "\n position " + position
             )
             getUserStatus(userId, decodeId, invitedUserId)*/
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

}
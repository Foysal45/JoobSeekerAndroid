package com.bdjobs.app.InviteCode.InviteCodeOwner

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.InviteCodeOwnerStatementModel
import com.bdjobs.app.API.ModelClasses.InviteCodeOwnerStatementModelData
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.InviteCode.InviteCodeCommunicator
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.invite_code_owner_invite_code_fragment.*
import kotlinx.android.synthetic.main.invite_code_owner_statement_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class OwnerStatementFragment :Fragment(), ConnectivityReceiver.ConnectivityReceiverListener {
    private var state = -1
    private var topBottomPadding = 0
    private var leftRightPadding = 0
    private var bdjobsUserSession: BdjobsUserSession? = null
    private var cashInstatementList = ArrayList<InviteCodeOwnerStatementModelData>()
    private var cashOutstatementList = ArrayList<InviteCodeOwnerStatementModelData>()
    private var allStatement = ArrayList<InviteCodeOwnerStatementModelData>()
    private var inviteCodeCommunicator: InviteCodeCommunicator? = null
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.invite_code_owner_statement_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity!!)
        inviteCodeCommunicator = activity as InviteCodeCommunicator

    }

    private fun getStatement(userId: String, decodeId: String, ownerId: String) {
        shimmer_view_container_JobList.show()
        shimmer_view_container_JobList.startShimmerAnimation()
        statementLV.hide()
        ApiServiceMyBdjobs.create().getOwnerStatement(
                userID= userId,
                decodeID = decodeId,
                pcOwnerId = ownerId
        ).enqueue(
                object :Callback<InviteCodeOwnerStatementModel>{
                    override fun onFailure(call: Call<InviteCodeOwnerStatementModel>, t: Throwable) {
                        error("onFailure",t)
                    }
                    override fun onResponse(call: Call<InviteCodeOwnerStatementModel>, response: Response<InviteCodeOwnerStatementModel>) {
                        try {
                            if (response.isSuccessful) {
                                shimmer_view_container_JobList.hide()
                                shimmer_view_container_JobList.stopShimmerAnimation()
                                statementLV.show()
                                try {
                                    allStatement = response.body()?.data as ArrayList<InviteCodeOwnerStatementModelData>

                                    for (i in allStatement.indices) {
                                        if (allStatement[i].type.equalIgnoreCase("In")) {
                                            cashInstatementList.add(allStatement[i])
                                        } else if (allStatement[i].type.equalIgnoreCase("Out")) {
                                            cashOutstatementList.add(allStatement[i])
                                        }
                                    }

                                    resetButton()

                                    Log.d("Statement", "allStatement = " + allStatement.size.toString()
                                            + "\n cashInstatementList = " + cashInstatementList.size.toString()
                                            + "\n cashOutstatementList = " + cashOutstatementList.size.toString())

                                } catch (e: Exception) {

                                    e.printStackTrace()
                                }

                            } else {

                            }
                        } catch (e: Exception) {

                            e.printStackTrace()
                        }


                    }
                }
        )}

    private fun onClickListeners() {
        val scale = resources.displayMetrics.density
        topBottomPadding = (8 * scale + 0.5f).toInt()
        leftRightPadding = (16 * scale + 0.5f).toInt()

        cashInTV.setOnClickListener(View.OnClickListener {
            if (state != 0) {
                state = 0
                cashInTV.setTextColor(Color.parseColor("#FFFFFF"))
                cashOutTV.setTextColor(Color.parseColor("#B92D00"))
                cashInTV.setBackgroundResource(R.drawable.cashin_active)
                cashOutTV.setBackgroundResource(R.drawable.cashout_inactive)
                cashInTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
                cashOutTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
                val statementListAdapter = OwnerStatementAdapter(activity!!, cashInstatementList)
                statementLV.adapter = statementListAdapter
                titleTV.text = "ক্যাশ ইন ট্রান্সজেকশন"
                try {
                    numberTV.text = cashInstatementList.size.toString().toBanglaDigit() + " টি"
                } catch (e: Exception) {
                    e.printStackTrace()
                    numberTV.text = "০ টি"
                }


            } else if (state == 0) {
                resetButton()
            }
        })
        cashOutTV.setOnClickListener(View.OnClickListener {
            if (state != 1) {
                state = 1
                cashOutTV.setTextColor(Color.parseColor("#FFFFFF"))
                cashInTV.setTextColor(Color.parseColor("#13A10E"))
                cashInTV.setBackgroundResource(R.drawable.cashin_inactive)
                cashOutTV.setBackgroundResource(R.drawable.cashout_active)
                cashInTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
                cashOutTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
                val statementListAdapter = OwnerStatementAdapter(activity!!, cashOutstatementList)
                statementLV.adapter = statementListAdapter
                titleTV.text = "ক্যাশ আউট ট্রান্সজেকশন"
                try {
                    numberTV.text = cashOutstatementList.size.toString().toBanglaDigit() + " টি"
                } catch (e: Exception) {
                    e.printStackTrace()
                    numberTV.text = "০ টি"
                }

            } else if (state == 1) {
                resetButton()

            }
        })
    }

    private fun resetButton() {
        state = -1
        cashInTV.setTextColor(Color.parseColor("#13A10E"))
        cashInTV.setBackgroundResource(R.drawable.cashin_inactive)
        cashOutTV.setTextColor(Color.parseColor("#B92D00"))
        cashOutTV.setBackgroundResource(R.drawable.cashout_inactive)
        cashInTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
        cashOutTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
        val statementListAdapter = OwnerStatementAdapter(activity!!, allStatement)
        statementLV.adapter = statementListAdapter
        titleTV.text = "মোট ট্রান্সজেকশন"
        try {
            numberTV.text = allStatement.size.toString().toBanglaDigit() + " টি"
        } catch (e: Exception) {
            e.printStackTrace()
            numberTV.text = "০ টি"
        }

    }


    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onPause() {
        super.onPause()
        activity!!.unregisterReceiver(internetBroadCastReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSnackBar?.dismiss()
        activity!!.registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            try {
                mSnackBar = Snackbar
                        .make(owner_statement_list, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.turn_on_wifi)) {
                            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                        }
                        .setActionTextColor(resources.getColor(R.color.colorWhite))
                mSnackBar?.show()
            } catch (e: Exception) {
            }
        } else{
            mSnackBar?.dismiss()
            //setupData()
            onClickListeners()
            getStatement(bdjobsUserSession?.userId!!, bdjobsUserSession?.decodId!!, inviteCodeCommunicator?.getInviteCodepcOwnerID()!!)            //onClicks()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSnackBar?.dismiss()
    }

}
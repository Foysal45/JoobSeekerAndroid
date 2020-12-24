package com.bdjobs.app.InviteCode.InviteCodeUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.PaymentTypeInsertModel
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.InviteCodeInfo
import com.bdjobs.app.InviteCode.InviteCodeCommunicator
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.fragment_invite_code_submit.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InviteCodeSubmitFragment : Fragment() {
    private var bdjobsUserSession: BdjobsUserSession? = null
    private var inviteCodeCommunicator: InviteCodeCommunicator? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_invite_code_submit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inviteCodeCommunicator = activity as InviteCodeCommunicator
        bdjobsUserSession = BdjobsUserSession(activity!!)
        onClick()

    }

    private fun onClick() {
        backImageView.setOnClickListener {
            inviteCodeCommunicator?.backButtonClicked()
        }

        promoCodeEditText.easyOnTextChangedListener { txt ->
            validatePromoCode(txt.toString())
        }

        nextButton.setOnClickListener {
            if (validatePromoCode(promoCodeEditText.getString())) {

                activity?.showProgressBar(loadingProgressBar)
                ApiServiceMyBdjobs.create().insertInviteCode(
                        userID = bdjobsUserSession?.userId,
                        decodeID = bdjobsUserSession?.decodId,
                        mobileNumber = bdjobsUserSession?.userName,
                        catId = activity?.getBlueCollarUserId()?.toString(),
                        deviceID = activity?.getDeviceID(),
                        promoCode = promoCodeEditText.getString()
                ).enqueue(object : Callback<PaymentTypeInsertModel> {
                    override fun onFailure(call: Call<PaymentTypeInsertModel>, t: Throwable) {
                        error("onFailure", t)
                        toast("দয়াকরে আবার চেষ্টা করুন")
                        activity?.stopProgressBar(loadingProgressBar)
                    }

                    override fun onResponse(call: Call<PaymentTypeInsertModel>, response: Response<PaymentTypeInsertModel>) {
                        activity?.stopProgressBar(loadingProgressBar)
                        if (response?.body()?.data?.get(0)?.inserted?.equalIgnoreCase("True")!!) {
                            promoCodeTIL.hideError()
                            val inviteCodeInfo = InviteCodeInfo(
                                    userId = bdjobsUserSession?.userId,
                                    userType = "U",
                                    pcOwnerID = "",
                                    inviteCodeStatus = "1"
                            )
                            val bdjobsDB = BdjobsDB.getInstance(activity!!)
                            /*Log.d("inviteCodeUserInfoReg", "userID = ${inviteCodeInfo.userId},\n" +
                                    "userType = ${inviteCodeInfo.userType},\n" +
                                    "inviteCodeStatus = ${inviteCodeInfo.inviteCodeStatus}")*/
                            doAsync {
                                bdjobsDB.inviteCodeUserInfoDao().insertInviteCodeUserInformation(inviteCodeInfo)
                                inviteCodeCommunicator?.goToPaymentMethod(fromInviteCodeSubmitPage = true)
                            }
                        } else {
                            promoCodeTIL.showError(response?.body()?.message)
                        }
                    }
                })

            }
        }
    }

    private fun validatePromoCode(txt: String): Boolean {
        if (txt.length < 4) {
            promoCodeTIL.showError("ইনভাইট কোডটি সর্বনিন্ম ৪ অক্ষরের হতে হবে")
            return false
        }
        promoCodeTIL.hideError()
        return true
    }
}
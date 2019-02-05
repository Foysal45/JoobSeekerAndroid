package com.bdjobs.app.InviteCode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.PaymentTypeInsertModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_invite_code_payment_methods.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentMethodFragment : Fragment() {
    private var bdjobsUserSession: BdjobsUserSession? = null
    private var inviteCodeCommunicator: InviteCodeCommunicator? = null
    private var paymentMethod = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_invite_code_payment_methods, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity!!)
        inviteCodeCommunicator = activity as InviteCodeCommunicator
        onClickListeners()
        accountNumberTIET.easyOnTextChangedListener { txt ->
            validateAccountNumber(txt)
        }
    }

    private fun validateAccountNumber(txt: CharSequence): Boolean {

        if (paymentMethod.equals("1", ignoreCase = true)) {

            if (txt.length == 11) {
                accountNumberTIL.hideError()
                return true

            } else if (txt.length < 11 || txt.length > 11) {
                accountNumberTIL.showError("বিকাশ একাউন্ট নম্বর ১১ সংখ্যা হতে হবে")
                requestFocus(accountNumberTIET)
                return false
            }

        } else if (paymentMethod.equals("2", ignoreCase = true)) {

            if (txt.length == 12) {
                accountNumberTIL.hideError()
                return true

            } else if (txt.length < 12) {
                accountNumberTIL.showError("রকেট একাউন্ট নম্বর ১২ সংখ্যা হতে হবে")
                requestFocus(accountNumberTIET)
                return false
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        paymentMethod = inviteCodeCommunicator?.getPaymentMethod()!!
        selectPaymentMethod(paymentMethod)

    }

    private fun onClickListeners() {

        backIconArrow.setOnClickListener { inviteCodeCommunicator?.backButtonClicked() }

        nextButton.setOnClickListener {
            if (paymentMethod.equalIgnoreCase("1") || paymentMethod.equalIgnoreCase("2")) {
                if (validateAccountNumber(accountNumberTIET.getString())) {
                    insertPaymentType(bdjobsUserSession?.userId, bdjobsUserSession?.decodId, inviteCodeCommunicator?.getInviteCodeUserType(), paymentMethod, accountNumberTIET.getString())
                }
            } else if (paymentMethod.equalIgnoreCase("3")) {
                insertPaymentType(bdjobsUserSession?.userId, bdjobsUserSession?.decodId, inviteCodeCommunicator?.getInviteCodeUserType(), paymentMethod, "")
            } else {
                toast("দয়াকরে যেকোনো একটি টাকা পাওয়ার মাধ্যম সিলেক্ট করুন")
            }


        }

        bkashBTN.setOnClickListener { selectPaymentMethod("1") }

        rocketBTN.setOnClickListener { selectPaymentMethod("2") }

        bdjobsBTN.setOnClickListener { selectPaymentMethod("3") }

    }

    private fun insertPaymentType(userId: String?, decodId: String?, inviteCodeUserType: String?, paymentMethod: String, accountNumber: String) {
        activity?.showProgressBar(loadingProgressBar)
        ApiServiceMyBdjobs.create().insertPaymentMethod(
                userID = userId,
                decodeID = decodId,
                userType = inviteCodeUserType,
                paymentType = paymentMethod,
                accountNo = accountNumber
        ).enqueue(
                object : Callback<PaymentTypeInsertModel> {
                    override fun onFailure(call: Call<PaymentTypeInsertModel>, t: Throwable) {
                        error("onFailure", t)
                        activity?.stopProgressBar(loadingProgressBar)
                        toast("আপনার টাকা পাওয়ার মাধ্যম সফলভাবে নিবন্ধন করা যায়নি। দয়াকরে আবার চেষ্টা করুন।")
                    }

                    override fun onResponse(call: Call<PaymentTypeInsertModel>, response: Response<PaymentTypeInsertModel>) {
                        activity?.stopProgressBar(loadingProgressBar)
                        try {
                            if (response.body()?.data?.get(0)?.inserted?.equalIgnoreCase("True")!!) {
                                toast("আপনার টাকা পাওয়ার মাধ্যম সফলভাবে নিবন্ধিত হয়েছে।")
                                inviteCodeCommunicator?.backButtonClicked()
                            } else {
                                toast("আপনার টাকা পাওয়ার মাধ্যম সফলভাবে নিবন্ধন করা যায়নি। দয়াকরে আবার চেষ্টা করুন।")
                            }
                        } catch (e: Exception) {
                            logException(e)
                            toast("আপনার টাকা পাওয়ার মাধ্যম সফলভাবে নিবন্ধন করা যায়নি। দয়াকরে আবার চেষ্টা করুন।")
                        }
                    }
                }
        )
    }

    private fun selectPaymentMethod(click: String) {
        paymentMethod = click

        when (click) {

            "1" -> {
                changeButtonBackground(bkashBTN, R.color.colorWhite, R.color.colorPrimary, R.color.colorWhite)
                changeButtonBackground(rocketBTN, R.color.colorPrimary, R.color.colorWhite, R.color.colorPrimary)
                changeButtonBackground(bdjobsBTN, R.color.colorPrimary, R.color.colorWhite, R.color.colorPrimary)
                accountNumberlayout.setVisibility(View.VISIBLE)
                if (inviteCodeCommunicator?.getAccountNumber()?.isNotEmpty()!!)
                    accountNumberTIET.setText(inviteCodeCommunicator?.getAccountNumber())
                validateAccountNumber(accountNumberTIET.getString())

            }

            "2" -> {
                changeButtonBackground(rocketBTN, R.color.colorWhite, R.color.colorPrimary, R.color.colorWhite)
                changeButtonBackground(bkashBTN, R.color.colorPrimary, R.color.colorWhite, R.color.colorPrimary)
                changeButtonBackground(bdjobsBTN, R.color.colorPrimary, R.color.colorWhite, R.color.colorPrimary)
                accountNumberlayout.setVisibility(View.VISIBLE)
                if (inviteCodeCommunicator?.getAccountNumber()?.isNotEmpty()!!)
                    accountNumberTIET.setText(inviteCodeCommunicator?.getAccountNumber())
                validateAccountNumber(accountNumberTIET.getString())

            }
            "3" -> {
                changeButtonBackground(bdjobsBTN, R.color.colorWhite, R.color.colorPrimary, R.color.colorWhite)
                changeButtonBackground(bkashBTN, R.color.colorPrimary, R.color.colorWhite, R.color.colorPrimary)
                changeButtonBackground(rocketBTN, R.color.colorPrimary, R.color.colorWhite, R.color.colorPrimary)
                accountNumberlayout.setVisibility(View.GONE)
            }
            else -> {
            }
        }


    }

    private fun changeButtonBackground(btn: MaterialButton, iconTint: Int, backGroundTint: Int, textColor: Int) {

        btn.iconTint = resources.getColorStateList(iconTint)
        btn.backgroundTintList = resources.getColorStateList(backGroundTint)
        btn.textColor = resources.getColor(textColor)

    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }


}
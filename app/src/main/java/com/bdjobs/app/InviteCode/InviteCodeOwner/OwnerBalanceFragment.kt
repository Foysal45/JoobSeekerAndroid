package com.bdjobs.app.InviteCode.InviteCodeOwner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.InviteCodeBalanceModel
import com.bdjobs.app.API.ModelClasses.InviteCodePaymentMethodModel
import com.bdjobs.app.InviteCode.InviteCodeCommunicator
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.toBanglaDigit
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.invite_code_owner_balance_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OwnerBalanceFragment : Fragment(), OnMapReadyCallback {
    private var bdjobsUserSession: BdjobsUserSession? = null
    private var inviteCodeCommunicator: InviteCodeCommunicator? = null
    var  paymentType =""
    var accountNumber=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.invite_code_owner_balance_fragment, container, false)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        val coordinates = LatLng(23.750839, 90.393307)
        googleMap.addMarker(MarkerOptions().position(coordinates).title("BDJOBS.COM LTD (DHAKA)")).showInfoWindow()
        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        bdjobsUserSession = BdjobsUserSession(activity!!)
        inviteCodeCommunicator = activity as InviteCodeCommunicator

        getBalanceInfo(bdjobsUserSession?.userId, bdjobsUserSession?.decodId, inviteCodeCommunicator?.getInviteCodepcOwnerID())
        getPaymentMethodType(bdjobsUserSession?.userId, bdjobsUserSession?.decodId, inviteCodeCommunicator?.getInviteCodeUserType())

        onClicks()
    }

    private fun onClicks() {
        moneyMethodChangeRL.setOnClickListener {
            inviteCodeCommunicator?.goToPaymentMethod(paymentMethod = paymentType,accountNumber = accountNumber)
        }
        moneyWithdrawRL.setOnClickListener {
            inviteCodeCommunicator?.goToPaymentMethod()
        }
    }

    private fun getPaymentMethodType(userId: String?, decodId: String?, inviteCodeUserType: String?) {


        ApiServiceMyBdjobs.create().getPaymentMethod(
                userID = userId,
                decodeID = decodId,
                userType = inviteCodeUserType
        ).enqueue(
                object : Callback<InviteCodePaymentMethodModel> {
                    override fun onFailure(call: Call<InviteCodePaymentMethodModel>, t: Throwable) {
                        error("onFailure", t)
                    }

                    override fun onResponse(call: Call<InviteCodePaymentMethodModel>, response: Response<InviteCodePaymentMethodModel>) {

                        try {
                            if (response.isSuccessful) {

                                val isExists = response.body()!!.data[0].isExist
                                 paymentType = response.body()!!.data[0].paymentType
                                 accountNumber = response.body()!!.data[0].accountNo
                                Log.d("paymentType", "paymentType = $paymentType")

                                Log.d("isExists", isExists)

                                if (isExists.equals("0", ignoreCase = true)) {
                                    moneyWithdrawRL.visibility = View.VISIBLE
                                    warningTV.visibility = View.VISIBLE
                                    moneyMethodChangeRL.visibility = View.GONE

                                } else if (isExists.equals("1", ignoreCase = true)) {
                                    moneyWithdrawRL.visibility = View.GONE
                                    warningTV.visibility = View.GONE
                                    moneyMethodChangeRL.visibility = View.VISIBLE
                                }


                                Log.d("paymentType", "paymentType = $paymentType")
                                if (paymentType != null && !paymentType.isEmpty()) {

                                    if (paymentType.equals("1", ignoreCase = true)) {

                                        paymentMediaTextView.text = "বিকাশ"
                                        accNumbertextView.text = accountNumber
                                        accountLayout.visibility = View.VISIBLE
                                        mapLayout.visibility = View.GONE

                                    } else if (paymentType.equals("2", ignoreCase = true)) {

                                        paymentMediaTextView.text = "রকেট"
                                        accNumbertextView.text = accountNumber
                                        mapLayout.visibility = View.GONE
                                        accountLayout.visibility = View.VISIBLE


                                    } else if (paymentType.equals("3", ignoreCase = true)) {
                                        accountLayout.visibility = View.GONE
                                        mapLayout.visibility = View.VISIBLE
                                    }
                                } else {

                                }
                            }
                        } catch (e: Exception) {

                            e.printStackTrace()
                        }

                    }

                }
        )

    }

    private fun getBalanceInfo(userId: String?, decodId: String?, inviteCodepcOwnerID: String?) {
        ApiServiceMyBdjobs.create().getOwnerBalance(
                userID = userId,
                decodeID = decodId,
                pcOwnerId = inviteCodepcOwnerID
        ).enqueue(
                object : Callback<InviteCodeBalanceModel> {
                    override fun onFailure(call: Call<InviteCodeBalanceModel>, t: Throwable) {
                        error("onFailure", t)
                    }

                    override fun onResponse(call: Call<InviteCodeBalanceModel>, response: Response<InviteCodeBalanceModel>) {
                        try {
                            subHeading1TV?.text = response.body()?.data?.get(0)?.totalEarned?.toBanglaDigit() + " /-"
                            subHeading2TV?.text = response.body()?.data?.get(0)?.totalWithDraw?.toBanglaDigit() + " /-"
                            subHeading3TV?.text = response.body()?.data?.get(0)?.totalPayable?.toBanglaDigit() + " /-"
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                }
        )

    }


}
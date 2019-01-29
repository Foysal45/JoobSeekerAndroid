package com.bdjobs.app.InviteCode.InviteCodeUser

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.InviteCodePaymentMethodModel
import com.bdjobs.app.API.ModelClasses.InviteCodeUserStatusModel
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.InviteCode.InviteCodeCommunicator
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.PhotoUploadActivity
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_invite_code_status.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InviteCodeStatusFragment : Fragment(), OnMapReadyCallback {
    private var bdjobsUserSession: BdjobsUserSession? = null

    private var inviteCodeCommunicator: InviteCodeCommunicator?=null
    private var dataStorage: DataStorage?=null

    var  paymentType =""
    var accountNumber=""



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_invite_code_status, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        onClicks()

        bdjobsUserSession = BdjobsUserSession(activity!!)
        inviteCodeCommunicator = activity as InviteCodeCommunicator
        dataStorage = DataStorage(activity!!)

        nameTV.text = bdjobsUserSession?.fullName
        categoryTV.text = dataStorage?.getCategoryBanglaNameByID(activity?.getBlueCollarUserId().toString())
        profileIMGV.loadCircularImageFromUrl(bdjobsUserSession?.userPicUrl)

        backIMGV.setOnClickListener {
            inviteCodeCommunicator?.backButtonClicked()
        }

        shimmer_view_container_JobList.show()
        shimmer_view_container_JobList.startShimmerAnimation()
        infoLL.hide()

        getUserStatus(
                userId = bdjobsUserSession?.userId!!,
                decodeId = bdjobsUserSession?.decodId!!,
                invitedUserId = bdjobsUserSession?.userId!!
        )

        getPaymentMethodType(userId = bdjobsUserSession?.userId,
                decodId = bdjobsUserSession?.decodId,
                inviteCodeUserType = inviteCodeCommunicator?.getInviteCodeUserType())
    }

    private fun onClicks() {
        changeRelativeLayout.setOnClickListener {
            inviteCodeCommunicator?.goToPaymentMethod(paymentType,accountNumber)
        }
        moneyWithdrawLayout.setOnClickListener {
            inviteCodeCommunicator?.goToPaymentMethod()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        val coordinates = LatLng(23.750839, 90.393307)
        googleMap.addMarker(MarkerOptions().position(coordinates).title("BDJOBS.COM LTD (DHAKA)")).showInfoWindow()
        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
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
                                    moneyWithdrawLayout.visibility = View.VISIBLE
                                    warningTV.visibility = View.VISIBLE
                                    changeRelativeLayout.visibility = View.GONE

                                } else if (isExists.equals("1", ignoreCase = true)) {
                                    moneyWithdrawLayout.visibility = View.GONE
                                    warningTV.visibility = View.GONE
                                    changeRelativeLayout.visibility = View.VISIBLE
                                }


                                shimmer_view_container_JobList.hide()
                                shimmer_view_container_JobList.stopShimmerAnimation()
                                infoLL.show()


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


    private fun setdata(personal: String, experience: String, education: String, photo: String, verified: String, paid: String) {

        if (personal.equals("true", ignoreCase = true) && experience.equals("true", ignoreCase = true) && education.equals("true", ignoreCase = true) && photo.equals("true", ignoreCase = true)) {
            accountInfoLL.visibility = View.GONE
            accountStatusLL.visibility = View.VISIBLE
            Log.d("StatusPPPP", "Paid: $paid, Verified: $verified")
            if (verified.equals("0", ignoreCase = true)) {
                cvInfoVerifyStopIMGV.setBackgroundResource(R.drawable.cv_check_pending)
            } else if (verified.equals("1", ignoreCase = true)) {
                cvInfoVerifyStopIMGV.setBackgroundResource(R.drawable.cv_check_ok)
            } else if (verified.equals("2", ignoreCase = true)) {
                cvInfoVerifyStopIMGV.setBackgroundResource(R.drawable.cv_check_not_ok)
            }

            if (paid.equals("True", ignoreCase = true)) {
                paymentInfoIMGV.setBackgroundResource(R.drawable.cv_check_ok)
            } else if (paid.equals("False", ignoreCase = true)) {
                paymentInfoIMGV.setBackgroundResource(R.drawable.cv_check_pending)
            }


        } else {
            accountInfoLL.visibility = View.VISIBLE
            accountStatusLL.visibility = View.GONE

            Log.d("CheckData", " personal $personal experinece $experience education $education photo $photo")

            if (personal.equals("True", ignoreCase = true)) {

                Log.d("CheckData", "12")
                personalInfoIMGV.setBackgroundResource(R.drawable.acount_right_icon)

            } else {

                Log.d("CheckData", "13")
                personalInfoIMGV.setBackgroundResource(R.drawable.resume_add_icon)

                personalInfoIMGV.setOnClickListener { setClickListener("personal") }

            }


            if (experience.equals("True", ignoreCase = true)) {

                Log.d("CheckData", "14")
                experienceInfoIMGV.setBackgroundResource(R.drawable.acount_right_icon)

            } else {

                Log.d("CheckData", "15")
                experienceInfoIMGV.setBackgroundResource(R.drawable.resume_add_icon)

                experienceInfoIMGV.setOnClickListener { setClickListener("experience") }
            }

            if (education.equals("True", ignoreCase = true)) {

                Log.d("CheckData", "1")

                educationalInfoIMGV.setBackgroundResource(R.drawable.acount_right_icon)

            } else {
                Log.d("CheckData", "2")
                educationalInfoIMGV.setBackgroundResource(R.drawable.resume_add_icon)

                educationalInfoIMGV.setOnClickListener { setClickListener("education") }
            }

            if (photo.equals("True", ignoreCase = true)) {
                Log.d("CheckData", "3")
                photoInfoIMGV.setBackgroundResource(R.drawable.acount_right_icon)

            } else {
                Log.d("CheckData", "4")
                photoInfoIMGV.setBackgroundResource(R.drawable.resume_add_icon)

                photoInfoIMGV.setOnClickListener { setClickListener("photo") }
            }

        }

    }

    private fun setClickListener(clickItem: String) {

        when (clickItem) {
            "personal" -> {
                activity?.startActivity<PersonalInfoActivity>("name" to "null", "personal_info_edit" to "addDirect")
            }
            "experience" -> {
                   activity?.startActivity<EmploymentHistoryActivity>("name" to "null", "emp_his_add" to "addDirect")
            }
            "education" -> {
                   activity?.startActivity<AcademicBaseActivity>("name" to "null", "education_info_edi" to "addDirect")
            }
            "photo" -> {
                   activity?.startActivity<PhotoUploadActivity>()
            }
        }
    }


    private fun getUserStatus(userId: String, decodeId: String, invitedUserId: String) {

        ApiServiceMyBdjobs.create().getInviteCodeUserStatus(
                userID = userId,
                decodeID = decodeId,
                invited_user_id = invitedUserId
        ).enqueue(
                object : Callback<InviteCodeUserStatusModel> {
                    override fun onFailure(call: Call<InviteCodeUserStatusModel>, t: Throwable) {
                        error("onFailure", t)
                    }

                    override fun onResponse(call: Call<InviteCodeUserStatusModel>, response: Response<InviteCodeUserStatusModel>) {

                        if (response.isSuccessful) {
                            setdata(personal = response.body()!!.data[0].personalInfo,
                                    experience = response.body()!!.data[0].skills,
                                    education = response.body()!!.data[0].educationInfo,
                                    photo = response.body()!!.data[0].photoInfo,
                                    verified = response.body()!!.data[0].verifiedStatus,
                                    paid = response.body()!!.data[0].paidStatus)
                        }
                    }
                }
        )


    }
}
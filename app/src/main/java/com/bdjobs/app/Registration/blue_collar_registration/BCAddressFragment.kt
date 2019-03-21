package com.bdjobs.app.Registration.blue_collar_registration

import android.app.AlertDialog
import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.External.LocationModel
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_adress.*


class BCAddressFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var dataStorage: DataStorage
    private lateinit var district: String
    private lateinit var thana: String
    private lateinit var postOffice: String
    private lateinit var address: String
    private lateinit var returnView: View
    var districtList: ArrayList<LocationModel>? = null
    var thanaList: ArrayList<LocationModel>? = null
    var postOfficeList: ArrayList<LocationModel>? = null
    var locationID = ""
    var thanaId = ""
    var postOfficeId = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        returnView = inflater.inflate(R.layout.fragment_bc_adress, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onClick()
        initialization()

    }


    private fun onClick() {

        bcAddressFAButton.setOnClickListener {

            checkValidity()

            thana = bcThanaTIET.getString()
            district = bcDistrictTIET.getString()
            /*   division = bcDivisionTIET.getString()*/
            address = bcVillageTIET.getString().trim()
            postOffice = bcPostOfficeTIET.getString()

            //fjgng

            /*if (postOffice.equals("অন্যান্য", ignoreCase = true) || TextUtils.isEmpty(postOffice)) {
                locationID = dataStorage.getBanglaLocationIDByName(thana)!!
            } else {
                locationID = dataStorage.getBanglaLocationIDByName(postOffice)!!
            }*/

            locationID = if (postOffice.equals("অন্যান্য", ignoreCase = true) || TextUtils.isEmpty(postOffice)) {
                thanaId
            } else {
                postOfficeId
            }



            if (validateCondition()) {
                registrationCommunicator.bcAddressSelected(district, thana, postOffice, address, locationID)
                registrationCommunicator.bcGoToStepExperience()
            }


        }




        supportTextView?.setOnClickListener {

            activity.callHelpLine()

        }

        bcHelpLineLayout?.setOnClickListener {

            activity.callHelpLine()
        }

    }

    private fun initialization() {

        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)
        addTextChangedListener()

    }

    private fun addTextChangedListener() {


        //kjhjh

        bcDistrictTIET?.easyOnTextChangedListener { charSequence ->

            addressValidation(charSequence.toString(), bcDistrictTIET, bcDistrictTIL, "")
            d("etTrInst : ->$charSequence|")

        }


        bcThanaTIET?.easyOnTextChangedListener { charSequence ->

            addressValidation(charSequence.toString(), bcThanaTIET, bcThanaTIL, "")

        }

        bcVillageTIET?.easyOnTextChangedListener { charSequence ->

            addressValidation(charSequence.toString(), bcVillageTIET, bcVillageTIL, "এলাকার ঠিকানা লিখুন")

        }


    }

    private fun validateCondition(): Boolean {
        return !TextUtils.isEmpty(bcVillageTIET?.text.toString()) and !TextUtils.isEmpty(bcDistrictTIET?.text.toString()) and !TextUtils.isEmpty(bcDistrictTIET?.text.toString()) and !TextUtils.isEmpty(bcThanaTIET?.text.toString())
    }


    private fun addressValidation(char: String, et: TextInputEditText?, til: TextInputLayout?, message: String): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til?.showError(message)
                if (et != null) {
                    try {
                        requestFocus(et)
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
                return false
            }
            else -> til?.hideError()
        }
        return true
    }


    private fun requestFocus(view: View?) {
        try {
            if (view != null) {

                try {
                    if (view.requestFocus()) {
                        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }
        } catch (e: Exception) {
            logException(e)

        }
    }

    private fun checkValidity() {

        if (TextUtils.isEmpty(bcDistrictTIET?.getString())) {

            bcDistrictTIL?.showError("জেলা নির্বাচন করুন")

        } else {

            bcDistrictTIL?.isErrorEnabled = false

        }

        if (TextUtils.isEmpty(bcThanaTIET?.getString())) {

            bcThanaTIL?.showError("থানা/উপজেলা নির্বাচন করুন")

        } else {

            bcThanaTIL?.isErrorEnabled = false


        }

        if (TextUtils.isEmpty(bcVillageTIET?.getString())) {

            bcVillageTIL?.showError("এলাকার ঠিকানা লিখুন ")
            bcVillageTIET?.requestFocus()
            bcVillageTIL?.requestFocus()

        } else {

            bcVillageTIL?.isErrorEnabled = false

        }


    }


    override fun onResume() {
        super.onResume()


        bcVillageTIL?.isErrorEnabled = false
        /*  bcVillageTIET.isFocusable = false*/


        /* setDialog("বিভাগ নির্বাচন করুন ", bcDivisionTIET, dataStorage.banglaAllDivision)*/

        districtList = dataStorage.getAllBngDistrictList()

        val districtNameList = arrayListOf<String>()

        districtList?.forEach { dt ->

            districtNameList.add(dt.locationName)

        }

        setDialog("জেলা নির্বাচন করুন", bcDistrictTIET, districtNameList.toTypedArray())

        /* if (!TextUtils.isEmpty(bcDivisionTIET.getString())) {
             var queryValue = bcDivisionTIET.getString()
             queryValue = queryValue.replace("'", "''")

         }*/
        if (!TextUtils.isEmpty(bcDistrictTIET?.getString())) {
            var queryValue = bcDistrictTIET.getString()
            queryValue = queryValue.replace("'", "''")
            setDialog("উপজেলা / থানা নির্বাচন করুন", bcThanaTIET, dataStorage.getDependentLocationByParentNameInBangla(queryValue))
        }
        if (!TextUtils.isEmpty(bcThanaTIET?.text.toString())) {
            var queryValue = bcThanaTIET?.text.toString()
            queryValue = queryValue.replace("'", "''")
            setDialog("পোষ্ট অফিস নির্বাচন করুন", bcPostOfficeTIET, dataStorage.getDependentPostOfficeByParentNameInBangla(queryValue))
        }


    }


    private fun setDialog(title: String, editText: TextInputEditText, data: Array<String>) {
        editText.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
                    .setItems(data
                    ) { dialog, which ->
                        editText.setText(data[which])



                        if (editText.id == R.id.bcDistrictTIET) {
                            bcThanaTIET?.clear()
                            bcPostOfficeTIET?.clear()
                            bcThanaTIET?.setOnClickListener(null)
                            bcPostOfficeTIET?.setOnClickListener(null)
                            var queryValue = editText.text.toString()
                            queryValue = queryValue.replace("'", "''")

                            thanaList = dataStorage.getDependentBanglaLocationByParentId(districtList?.get(which)?.locationId!!)
                            val thanaNameList = arrayListOf<String>()

                            thanaList?.forEach { dt ->

                                thanaNameList.add(dt.locationName)

                            }
                            setDialog("উপজেলা / থানা নির্বাচন করুন", bcThanaTIET, thanaNameList.toTypedArray())
                        }
                        if (editText.id == R.id.bcThanaTIET) {
                            bcPostOfficeTIET?.clear()
                            bcPostOfficeTIET?.setOnClickListener(null)
                            var queryValue = editText.text.toString()
                            queryValue = queryValue.replace("'", "''")

                            thanaId = thanaList?.get(which)?.locationId!!

                            postOfficeList = dataStorage.getDependentBanglaLocationByParentId(thanaList?.get(which)?.locationId!!)

                            val pstOfficeNameList = arrayListOf<String>()

                            if (pstOfficeNameList.isNullOrEmpty()) {

                                try {
                                    val otherLocation = LocationModel("অন্যান্য", "-2")
                                    postOfficeList?.add(otherLocation)
                                } catch (e: Exception) {
                                    logException(e)
                                }
                            }

                            postOfficeList?.forEach { dt ->

                                pstOfficeNameList.add(dt.locationName)

                            }

                            setDialog("পোষ্ট অফিস নির্বাচন করুন", bcPostOfficeTIET, pstOfficeNameList.toTypedArray())


                        }

                        if (editText.id == R.id.bcPostOfficeTIET) {

                            postOfficeId = postOfficeList?.get(which)?.locationId!!
                            postOffice = bcPostOfficeTIET.getString()


                        }



                    }
            val dialog = builder.create()
            dialog.show()
        }
    }

}

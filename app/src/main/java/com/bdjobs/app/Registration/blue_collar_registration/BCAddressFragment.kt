package com.bdjobs.app.Registration.blue_collar_registration

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.Databases.External.DataStorage

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import kotlinx.android.synthetic.main.fragment_bc_adress.*
import kotlinx.android.synthetic.main.fragment_bc_category.*
import kotlinx.android.synthetic.main.fragment_bc_mobile_number.*
import kotlinx.android.synthetic.main.fragment_bc_otp_code.*
import org.jetbrains.anko.selector


class BCAddressFragment : Fragment() {


    private lateinit var registrationCommunicator :RegistrationCommunicator
        private lateinit var dataStorage:DataStorage
    private lateinit var division :String
    private lateinit var district :String
    private lateinit var thana :String
    private lateinit var postOffice :String
    private lateinit var address :String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bc_adress, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onClick()
        initialization()

    }


    private fun onClick(){

        bcAddressFAButton.setOnClickListener {


            thana = bcThanaTIET.text.toString()
            district = bcDistrictTIET.text.toString()
            division = bcDivisionTIET.text.toString()
            address = bcVillageTIET.text.toString()
            postOffice = bcPostOfficeTIET.getText().toString()
            var locationID = ""

            if (postOffice.equals("অন্যান্য", ignoreCase = true) || TextUtils.isEmpty(postOffice)) {
                locationID = dataStorage.getBanglaLocationIDByName(thana)!!
            } else {
                locationID = dataStorage.getBanglaLocationIDByName(postOffice)!!
            }

           if (validateCondition()){

               registrationCommunicator.bcAddressSelected(division, district, thana, postOffice, address, locationID)
               registrationCommunicator.bcGoToStepExperience()
           }


        }

        bcDivisionTIET.setOnClickListener {

            val divisionList: Array<String> = dataStorage.banglaAllDivision

            selector("বিভাগ নির্বাচন করুন", divisionList.toList()) { dialogInterface, i ->

                bcDivisionTIET.setText(divisionList[i])
                bcDistrictTIL.requestFocus()


            }

        }

        bcDistrictTIET.setOnClickListener {

            var queryValue = bcDivisionTIET.text.toString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentNameInBangla(queryValue)

            selector("জেলা নির্বাচন করুন", districtList.toList()) { dialogInterface, i ->

                bcDistrictTIET.setText(districtList[i])

                bcDistrictTIL.requestFocus()


            }


        }

        bcThanaTIET.setOnClickListener {


            var queryValue = bcDistrictTIET.text.toString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentNameInBangla(queryValue)

            selector("উপজেলা / থানা নির্বাচন করুন", districtList.toList()) { dialogInterface, i ->

                bcThanaTIET.setText(districtList[i])
                bcDistrictTIL.requestFocus()



            }


        }

        bcPostOfficeTIET.setOnClickListener {

            var queryValue = bcThanaTIET.text.toString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentPostOfficeByParentNameInBangla(queryValue)

            selector("উপজেলা / থানা নির্বাচন করুন", districtList.toList()) { dialogInterface, i ->

                bcPostOfficeTIET.setText(districtList[i])
                bcPostOfficeTIL.requestFocus()


            }

        }

        bcVillageTIL.setOnClickListener {



        }

    }

    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)

    }


    private fun validateCondition(): Boolean {
        return !TextUtils.isEmpty(bcVillageTIET.getText().toString()) and !TextUtils.isEmpty(bcDistrictTIET.getText().toString()) and !TextUtils.isEmpty(bcDistrictTIET.getText().toString()) and !TextUtils.isEmpty(bcThanaTIET.getText().toString())
    }


}

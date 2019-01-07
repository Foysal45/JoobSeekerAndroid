package com.bdjobs.app.editResume.contactInfo.fragments.contactDetails

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_contact_edit.*
import org.jetbrains.anko.selector

class ContactEditFragment : Fragment() {

    private lateinit var contactInfo: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var dataStorage: DataStorage
    private var insideBD: String = ""
    private var outsideBD: String = ""
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataStorage = DataStorage(activity)
        session = BdjobsUserSession(activity)
        contactInfo = activity as PersonalInfo
    }

    override fun onResume() {
        super.onResume()
        contactInfo.setTitle(getString(R.string.title_contact))
        doWork()
    }

    private fun doWork() {
        fab_contact_update.setOnClickListener {
            updateData()
        }

        preloadedData()


        ////Present Address---------------Start


        contactDivTIET.setOnClickListener {


            val divisionList: Array<String> = dataStorage.allDivision
            selector("Select Your division", divisionList.toList()) { dialogInterface, i ->

                contactDivTIET.setText(divisionList[i])
                contactDivTIL.requestFocus()


            }


        }



        contactDistrictTIET1.setOnClickListener {


            var queryValue = contactDivTIET.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            selector("Please Select your district", districtList.toList()) { dialogInterface, i ->

                contactDistrictTIET1.setText(districtList[i])

                contactDistrictTIL1.requestFocus()


            }


        }



        contactThanaTIET.setOnClickListener {


            var queryValue = contactDistrictTIET1.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            selector("Please Select your police station", districtList.toList()) { dialogInterface, i ->

                contactThanaTIET.setText(districtList[i])
                contactThanaTIL1.requestFocus()


            }


        }






        contactPostOfficeTIET1.setOnClickListener {


            var queryValue = contactThanaTIET.text.toString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentPostOfficeByParentNameInBangla(queryValue)

            selector("Please Select your post office", districtList.toList()) { dialogInterface, i ->

                contactPostOfficeTIET1.setText(districtList[i])
                contactPostOfficeTIL1.requestFocus()


            }


        }





        presentContactCountryTIET.setOnClickListener {


            val countryList: Array<String> = dataStorage.allCountries

            selector("Please select your country ", countryList.toList()) { dialogInterface, i ->

                presentContactCountryTIET.setText(countryList[i])
                presentContactCountryTIL.requestFocus()


            }


        }


        ////Parmanent Address---------------Start


        contactDivTIET1.setOnClickListener {


            val divisionList: Array<String> = dataStorage.allDivision
            selector("Select Your division", divisionList.toList()) { dialogInterface, i ->

                contactDivTIET1.setText(divisionList[i])
                contactDivTIL1.requestFocus()


            }


        }



        contactDistrictTIET.setOnClickListener {


            var queryValue = contactDivTIET1.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            selector("Please Select your district", districtList.toList()) { dialogInterface, i ->

                contactDistrictTIET.setText(districtList[i])

                contactDistrictTIL.requestFocus()


            }


        }



        contactThanaTIETP.setOnClickListener {


            var queryValue = contactDistrictTIET.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            selector("Please Select your police station", districtList.toList()) { dialogInterface, i ->

                contactThanaTIETP.setText(districtList[i])
                contactThanaTIL1.requestFocus()


            }


        }






        contactPostOfficeTIET.setOnClickListener {


            var queryValue = contactThanaTIETP.text.toString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentPostOfficeByParentNameInBangla(queryValue)

            selector("Please Select your post office", districtList.toList()) { dialogInterface, i ->

                contactPostOfficeTIET.setText(districtList[i])
                contactPostOfficeTIL.requestFocus()


            }


        }


        presentContactCountryTIETP.setOnClickListener {


            val countryList: Array<String> = dataStorage.allCountries

            selector("Please select your country ", countryList.toList()) { _, i ->

                presentContactCountryTIETP.setText(countryList[i])
                presentContactCountryTILP.requestFocus()


            }


        }



    }

    private fun updateData() {
        getDataFromChipGroup(cgPermanent)
        getDataFromChipGroup(cgPresent)
    }

    private fun preloadedData() {
        val data = contactInfo.getContactData()

        if (data.presentInsideOutsideBD!! == "False") {
            selectChip(cgPresent, "False")
        } else {
            selectChip(cgPresent, "True")
        }
        if (data.permanentInsideOutsideBD!! == "False") {
            selectChip(cgPermanent, "False")
        } else {
            selectChip(cgPermanent, "True")
        }
    }


    private fun getDataFromChipGroup(cg: ChipGroup) {
        cg.setOnCheckedChangeListener { chipGroup, i ->
            if (i > 0) {
                val chip = chipGroup.findViewById(i) as Chip
                Log.d("chip_entry", "text: ${chip.text}")
                val data = chip.text.toString()
                when (chipGroup.id) {
                    R.id.cgPresent -> {
                        when (data) {
                            "Inside Bangladesh" -> {
                                insideBD = "True"
                                outsideBD = "False"

                                presentInsideBangladeshLayout1.show()
                                presentOutsideBangladeshLayout.hide()

                            }
                            "Outside Bangladesh" -> {
                                insideBD = "False"
                                outsideBD = "True"

                                presentInsideBangladeshLayout1.hide()
                                presentOutsideBangladeshLayout.show()

                            }
                            else -> insideBD
                        }
                        d("value : $insideBD and $outsideBD")

                    }
                    R.id.cgPermanent -> {
                        when (data) {
                            "Inside Bangladesh" -> {
                                insideBD = "True"
                                outsideBD = "False"

                                presentInsideBangladeshLayout.show()
                                presentOutsideBangladeshLayoutP.hide()

                            }
                            "Outside Bangladesh" -> {
                                insideBD = "False"
                                outsideBD = "True"

                                presentInsideBangladeshLayout.hide()
                                presentOutsideBangladeshLayoutP.show()

                            }
                            else -> insideBD
                        }

                        d("valuep : $insideBD and $outsideBD")
                    }
                }
            } else {
                when (chipGroup.id) {
                    R.id.cgPresent -> {

                    }
                    R.id.cgPermanent -> {

                    }
                }
            }
        }
    }

    private fun selectChip(chipGroup: ChipGroup, data: String) {
        val count = chipGroup.childCount
        for (i in 0 until count) {
            val chip = chipGroup.getChildAt(i) as Chip
            //val chipText = chip.text.toString()
            if (data.equalIgnoreCase("True")) {
                Log.d("chip_entry", "text:$i")
                chip.isChecked = true
                d("value t/f : ${chip.isChecked}")
            } else {
                chip.isChecked = false
                d("value t/f : ${chip.isChecked}")
            }
        }
    }

}

package com.bdjobs.app.editResume.personalInfo.fragments.preferredAreas


import android.app.Fragment
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.adapters.models.PreferredAreasData
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_preferred_areas_edit.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreferredAreasEditFragment : Fragment() {
    private lateinit var prefCallBack: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var ds: DataStorage
    private lateinit var data: PreferredAreasData
    //private var idArr: ArrayList<String> = ArrayList()
    private var idWCArr: ArrayList<String> = ArrayList()
    private var idBCArr: ArrayList<String> = ArrayList()
    private var idOrgArr: ArrayList<String> = ArrayList()
    private var idInBDArr: ArrayList<String> = ArrayList()
    private var idOutBDArr: ArrayList<String> = ArrayList()
    private var prefWcIds: String = ""
    private var prefBcIds: String = ""
    private var prefOrgIds: String = ""
    private var prefDistrictIds: String = ""
    private var prefCountryIds: String = ""
    private var anywhereinBD = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferred_areas_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ds = DataStorage(activity)
        session = BdjobsUserSession(activity)
        prefCallBack = activity as PersonalInfo
        prefCallBack.setTitle(getString(R.string.title_pref_areas))
        prefCallBack.setEditButton(false, "dd")
        doWork()
    }

    override fun onResume() {
        super.onResume()
        clearAllArr()
        d("onResume : $idWCArr// $idBCArr// $idOrgArr// $idInBDArr// $idOutBDArr//")
        try {
            data = prefCallBack.getPrefAreasData()
        } catch (e: Exception) {
            logException(e)
            d("++${e.message}")
        }
        preloadedData(data)
        d("onResume : $idWCArr// $idBCArr// $idOrgArr// $idInBDArr// $idOutBDArr//")
    }

    private fun doWork() {
        onClicks()
        acWCjobCat?.addTextChangedListener(TW.CrossIconBehaveACTV(acWCjobCat))
        acBCJobCat?.addTextChangedListener(TW.CrossIconBehaveACTV(acBCJobCat))
        acOutsideBD?.addTextChangedListener(TW.CrossIconBehaveACTV(acOutsideBD))
        acOrgType?.addTextChangedListener(TW.CrossIconBehaveACTV(acOrgType))
        acInsideBD?.addTextChangedListener(TW.CrossIconBehaveACTV(acInsideBD))
    }

    private fun preloadedData(data: PreferredAreasData) {
        val jobCats = data.preferredJobCategories
        val bcjobCats = data.preferredBlueCategories
        val orgTypes = data.preferredOrganizationType
        val inBD = data.inside
        val outBD = data.outside
        //for ((i, value) in areaOfexps?.withIndex()!!)
        jobCats?.forEach {

            //idWCArr.clear()
            addChip(ds.getCategoryNameByID(it?.id!!), "wc", acWCjobCat)
            acWCjobCat.isEnabled = idWCArr.size <= 3
            addAsString(it.id, idWCArr)
            //Log.d("prefs", "wc: $prefWcIds and $idWCArr")
            if (idWCArr.isNullOrEmpty()) tilWCjobCat.hideError() else tilWCjobCat.isErrorEnabled = true
        }
        bcjobCats?.forEach {
            //idBCArr.clear()
            addChip(ds.getCategoryBanglaNameByID(it?.id!!), "bc", acBCJobCat)
            acBCJobCat.isEnabled = idBCArr.size <= 3
            addAsString(it.id, idBCArr)
            //Log.d("prefs", "bc: $prefBcIds and $idBCArr")
        }
        orgTypes?.forEach {
            //idOrgArr.clear()
            addChip(ds.getOrgNameByID(it?.id!!), "orgs", acOrgType)
            acOrgType.isEnabled = idOrgArr.size <= 12
            addAsString(it.id, idOrgArr)
            //Log.d("prefs", "org: $prefOrgIds and $idOrgArr")
        }

        if (!inBD.isNullOrEmpty()) {
            inBD.forEach {
                //idInBDArr.clear()
                if (it?.id != "-1") {
                    addChip(ds.getLocationNameByID(it?.id!!).toString(), "in", acInsideBD)
                    acInsideBD.isEnabled = idInBDArr.size <= 15
                    addAsString(it.id, idInBDArr)
                } else {
                    anywhereinBD = true
                    changeBtnBackground(anywhereinBD)
                }
                //Log.d("prefs", "inBD: $prefDistrictIds and $idInBDArr")
            }
        }
        outBD?.forEach {
            //idOutBDArr.clear()
            addChip(ds.getLocationNameByID(it?.id!!).toString(), "out", acOutsideBD)
            acOutsideBD.isEnabled = idOutBDArr.size <= 10
            addAsString(it.id, idOutBDArr)

            //Log.d("prefs", "outBD: $prefCountryIds and $idOutBDArr")
        }
    }

    private fun addAsString(expID: String, idArr: ArrayList<String>) {
        if (!idArr.contains(expID)) {
            idArr.add(expID.trim())
            //Log.d("prefAreas", "test : $idArr")
        }
    }

    private fun clearAllArr() {
        idWCArr.clear()
        idBCArr.clear()
        idOrgArr.clear()
        idInBDArr.clear()
        idOutBDArr.clear()

        tilWCjobCat.hideError()
        tilBCJobCat.hideError()
        tilInsideBD.hideError()

        wc_entry_chip_group?.removeAllViews()
        bc_entry_chip_group?.removeAllViews()
        org_entry_chip_group?.removeAllViews()
        pref_locs_entry_chip_group?.removeAllViews()
        pref_countries_entry_chip_group?.removeAllViews()
        /*} catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }*/
    }

    private fun onClicks() {
        changeBtnBackground(anywhereinBD)
        fab_prefAreas_update.setOnClickListener {

            if (!acWCjobCat.text.toString().isNullOrEmpty()) {
                toast("Functional Job Category is not available")
            } else if (!acBCJobCat.text.toString().isNullOrEmpty()) {
                toast("Special Skilled Job Category is not available")
            } else if (!acOrgType.text.toString().isNullOrEmpty()) {
                toast("Organization Type is not available")
            } else if (!acInsideBD?.text.toString().isNullOrEmpty()) {
                toast("District is not available")
            } else if (!acOutsideBD.text.toString().isNullOrEmpty()) {
                toast("Country/Region is not available")
            } else {

                var valid = 0
                prefWcIds = TextUtils.join(",", idWCArr)
                prefBcIds = TextUtils.join(",", idBCArr)
                prefOrgIds = TextUtils.join(",", idOrgArr)
                prefDistrictIds = if (!anywhereinBD) TextUtils.join(",", idInBDArr) else "-1"
                prefCountryIds = TextUtils.join(",", idOutBDArr)

                if (idInBDArr.isEmpty() && idOutBDArr.isEmpty()) {
                    tilInsideBD.isErrorEnabled = true
                    tilInsideBD.error = "This field can not be empty"
                    //Log.d("valid1", "valid: $valid")
                    /*tilOutsideBD.isErrorEnabled = true
                        tilOutsideBD.error = "This field can not be empty"*/
                }
                if (idWCArr.isEmpty() && idBCArr.isEmpty()) {
                    //tilWCjobCat.hideError()
                    tilWCjobCat.isErrorEnabled = true
                    tilWCjobCat.error = "This field can not be empty"
                    //tilBCJobCat.hideError()
                    tilBCJobCat.isErrorEnabled = true
                    tilBCJobCat.error = "This field can not be empty"
                    //Log.d("valid2", "valid: $valid")
                }
                if (idBCArr.isNotEmpty() || idWCArr.isNotEmpty()) {
                    valid += 1
                    tilBCJobCat.hideError()
                    tilWCjobCat.hideError()
                    //Log.d("valid3", "valid: $valid")
                }
                if (idInBDArr.isNotEmpty()) {
                    valid += 1
                    tilInsideBD.hideError()
                    //Log.d("valid4", "valid: $valid")
                }
                if ((idWCArr.isNotEmpty() || idBCArr.isNotEmpty()) && idInBDArr.isNotEmpty()) {
                    valid += 1
                    tilWCjobCat.hideError()
                    tilBCJobCat.hideError()
                    tilInsideBD.hideError()
                    //Log.d("valid5", "valid: $valid")
                }

                if (idInBDArr.isEmpty() && !anywhereinBD) {
                    tilInsideBD.isErrorEnabled = true
                    tilInsideBD.error = "This field can not be empty"
                }

                if (valid >= 2) {
                    //Log.d("ppppppp", "$idBCArr, // $idInBDArr // $anywhereinBD // $valid  ")
                    updateData()
                } else if ((idWCArr.isNotEmpty() || idBCArr.isNotEmpty()) && anywhereinBD) {
                    updateData()
                    //Log.d("ppppppp2", "$idBCArr, // $idInBDArr // $anywhereinBD // $valid ")
                } else {
                    //Log.d("ppppppp3", "$idBCArr, // $idInBDArr // $anywhereinBD // $valid ")
                }

                //Log.d("acWCjobCat", "wc: $prefWcIds// $prefBcIds// $prefOrgIds// $prefDistrictIds and $prefCountryIds")
            }
        }

        btnAnywhereInBD.setOnClickListener {
            anywhereinBD = true
            idInBDArr.clear()
            pref_locs_entry_chip_group.removeAllViews()
            changeBtnBackground(anywhereinBD)
        }
        btnSelectDistrict.setOnClickListener {
            anywhereinBD = false
            changeBtnBackground(anywhereinBD)
        }
        acWCjobCat.onFocusChange { _, hasFocus ->
            if (hasFocus) {
                val acList: Array<String> = ds.allWhiteCollarCategories.toTypedArray()
                val expsAdapter = ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line, acList)
                acWCjobCat.setAdapter(expsAdapter)
                acWCjobCat.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
                acWCjobCat.showDropDown()
                acWCjobCat.setOnItemClickListener { _, _, position, id ->
                    //                    d("acWCjobCat : ${acList[position + 1]} and gotStr : ${acWCjobCat.text} and $idWCArr")
                    val inputId = ds.getCategoryIDByName(acWCjobCat.text.toString())!!

                    if (idWCArr.size in 0..2) {
                        acWCjobCat.isEnabled = true
                        acWCjobCat.requestFocus()
                        if (!idWCArr.contains(inputId)) {
                            addChip(ds.getCategoryNameByID(inputId), "wc", acWCjobCat)
                            addAsString(inputId, idWCArr)
                            //Log.d("acWCjobCat", "arr2: $idWCArr")
                        } else {
                            acWCjobCat.closeKeyboard(activity)
                            activity?.toast("Category already added")
                            acWCjobCat.setText("")
                            acWCjobCat.clearFocus()
                        }
                        tilWCjobCat.hideError()
                    } else {
                        activity?.toast("Maximum 3 items can be added.")
                        acWCjobCat.isEnabled = false
                        acWCjobCat.setText("")
                        acWCjobCat.clearFocus()
                    }
                    acWCjobCat.clearFocus()
                }
            }
        }
        acBCJobCat.onFocusChange { _, hasFocus ->
            if (hasFocus) {
                val acList: Array<String> = ds.allBlueCollarCategoriesInBangla.toTypedArray()
                val expsAdapter = ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line, acList)
                acBCJobCat.setAdapter(expsAdapter)
                acBCJobCat.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
                acBCJobCat.showDropDown()
                acBCJobCat.setOnItemClickListener { _, _, position, id ->
                    //d("Selected : ${acList[position + 1]} and gotStr : ${acBCJobCat.text}")
                    val inputId = ds.getCategoryIDByBanglaName(acBCJobCat.text.toString())!!
                    if (idBCArr.size in 0..2) {
                        acBCJobCat.isEnabled = true
                        acBCJobCat.requestFocus()
                        if (!idBCArr.contains(inputId)) {
                            addChip(ds.getCategoryBanglaNameByID(inputId), "bc", acBCJobCat)
                            addAsString(inputId, idBCArr)
                            //Log.d("acWCjobCat", "arr2: $idBCArr")
                        } else {
                            acBCJobCat.closeKeyboard(activity)
                            activity?.toast("??????????????????????????? ???????????????????????? ????????? ????????? ??????????????????")
                            acBCJobCat.setText("")
                            acBCJobCat.clearFocus()
                        }
                        tilBCJobCat.hideError()
                    } else {
                        activity?.toast("Maximum 3 items can be added.")
                        acBCJobCat.isEnabled = false
                        acBCJobCat.setText("")
                        acBCJobCat.clearFocus()
                    }
                    acBCJobCat.clearFocus()
                }
            }
        }
        acOrgType.onFocusChange { _, hasFocus ->
            if (hasFocus) {
                val acList: Array<String> = ds.allOrgTypes.toTypedArray()
                val expsAdapter = ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line, acList)
                acOrgType.setAdapter(expsAdapter)
                acOrgType.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
                acOrgType.showDropDown()
                acOrgType.setOnItemClickListener { _, _, position, id ->
                    //d("Selected : ${acList[position + 1]} and gotStr : ${acOrgType.text}")
                    val inputId = ds.getOrgIDByOrgName(acOrgType.text.toString())
                    if (idOrgArr.size in 0..11) {
                        acOrgType.isEnabled = true
                        acOrgType.requestFocus()
                        if (!idOrgArr.contains(inputId)) {
                            addChip(ds.getOrgNameByID(inputId), "orgs", acOrgType)
                            addAsString(inputId, idOrgArr)
                            //Log.d("acWCjobCat", "arr2: $idOrgArr")
                        } else {
                            acOrgType.closeKeyboard(activity)
                            activity?.toast("Organization type already added")
                            acOrgType.setText("")
                            acOrgType.clearFocus()
                        }
                        tilOrgType.hideError()
                    } else {
                        activity?.toast("Maximum 12 items can be added.")
                        acOrgType.isEnabled = false
                        acOrgType.setText("")
                        acOrgType.clearFocus()
                    }
                    acOrgType.clearFocus()
                }
            }
        }
        acInsideBD.onFocusChange { _, hasFocus ->
            if (hasFocus) {

                val districtList = ds.getAllEnglishDistrictList()
                d("districtList ${districtList?.size} list ${districtList.toString()}")
                val districtNameList = arrayListOf<String>()
                districtList?.forEach { dt ->
                    districtNameList.add(dt.locationName)
                }

                val acList: Array<String> = districtNameList.toTypedArray()
                val expsAdapter = ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line, acList)
                acInsideBD.setAdapter(expsAdapter)
                acInsideBD.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
                acInsideBD.showDropDown()
//                acInsideBD.clearFocus()

                acInsideBD.setOnItemClickListener { _, _, position, id ->
                    //d("Selected : ${acList[position + 1]} and gotStr : ${acInsideBD.text}")
                    val inputId = ds.getLocationIDByName(acInsideBD.text.toString())
                    if (idInBDArr.size in 0..14) {
                        if (!idInBDArr.contains(inputId)) {
                            acInsideBD.isEnabled = true
                            acInsideBD.requestFocus()
                            addChip(ds.getLocationNameByID(inputId).toString(), "in", acInsideBD)
                            addAsString(inputId.toString(), idInBDArr)
                            //Log.d("acWCjobCat", "arr2: $idInBDArr")
                        } else {
                            acInsideBD.closeKeyboard(activity)
                            activity?.toast("District already added")
                            acInsideBD.setText("")
                            acInsideBD.clearFocus()
                        }
                        tilInsideBD.hideError()
                    } else {
                        activity?.toast("Maximum 15 items can be added.")
                        acInsideBD.isEnabled = false
                        acInsideBD.setText("")
                        acInsideBD.clearFocus()
                    }
                    acInsideBD.clearFocus()
                }
            }
        }

        acOutsideBD.onFocusChange { _, hasFocus ->
            if (hasFocus) {
                val acList: Array<String> = ds.allCountriesWithOutBangladesh
                val expsAdapter = ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line, acList)
                acOutsideBD.setAdapter(expsAdapter)
                acOutsideBD.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
                acOutsideBD.showDropDown()
                acOutsideBD.setOnItemClickListener { _, _, position, id ->
                    //d("Selected : ${acList[position + 1]} and gotStr : ${acOutsideBD.text}")
                    val inputId = ds.getLocationIDByName(acOutsideBD.text.toString())
                    //idOutBDArr.add(inputId!!)
                    ////Log.d("acWCjobCat", "Countryarr2: idOutBDArr")
                    if (idOutBDArr.size in 0..9) {
                        acOutsideBD.isEnabled = true
                        acOutsideBD.requestFocus()
                        if (!idOutBDArr.contains(inputId)) {
                            addChip(ds.getLocationNameByID(inputId).toString(), "out", acOutsideBD)
                            addAsString(inputId.toString(), idOutBDArr)
                            //Log.d("acWCjobCat", "arr2: $idOutBDArr")
                        } else {
                            acOutsideBD.closeKeyboard(activity)
                            activity?.toast("Country/Region already added")
                            acOutsideBD.setText("")
                            acOutsideBD.clearFocus()
                        }
                        tilOutsideBD.hideError()
                    } else {
                        activity?.toast("Maximum 10 items can be added.")
                        acOutsideBD.isEnabled = false
                        acOutsideBD.setText("")
                        acOutsideBD.clearFocus()
                    }
                    acOutsideBD.clearFocus()
                }
            }
        }

    }

    private fun changeBtnBackground(b: Boolean) {
        if (b) {
            prefDistrictIds = "-1"
            llAddDistricts.hide()
            btnAnywhereInBD.textColor = Color.parseColor("#FFFFFF")
            btnAnywhereInBD.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2F64A3"))
            btnSelectDistrict.textColor = Color.parseColor("#2F64A3")
            btnSelectDistrict.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
        } else {
            llAddDistricts.show()
            btnAnywhereInBD.textColor = Color.parseColor("#2F64A3")
            btnAnywhereInBD.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
            btnSelectDistrict.textColor = Color.parseColor("#FFFFFF")
            btnSelectDistrict.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2F64A3"))
        }
    }

    private fun addChip(input: String, tag: String, acTV: AutoCompleteTextView) {
        var maxItems = 0
        //var maxArr = ArrayList<String>()
        val cg: ChipGroup = when (tag) {
            "wc" -> {
                maxItems = 3
                wc_entry_chip_group
            }
            "bc" -> {
                maxItems = 3
                bc_entry_chip_group
            }
            "orgs" -> {
                maxItems = 12
                org_entry_chip_group
            }
            "in" -> {
                maxItems = 15
                //Log.d("insideBD", "$idInBDArr")
                pref_locs_entry_chip_group
            }
            "out" -> {
                maxItems = 10
                pref_countries_entry_chip_group
            }
            else -> org_entry_chip_group
        }

        when {
            cg.childCount < maxItems -> {
                acTV.isEnabled = true
                val c1 = getChip(cg, input, R.xml.chip_entry, tag)
                //maxItemsArr.add(ds.getCategoryIDByName(input).toString())
                if (cg.childCount == maxItems)
                    acTV.isEnabled = false
                cg.addView(c1)
            }
        }
        acTV.clearText()
        clPrefAreasEdit?.closeKeyboard(activity)
    }


    private fun getChip(entryChipGroup: ChipGroup, text: String, item: Int, tag: String): Chip {
        val chip = Chip(activity)
        chip.setChipDrawable(ChipDrawable.createFromResource(activity, item))
        val paddingDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        chip.setOnCloseIconClickListener {
            entryChipGroup.removeView(chip)
            val check = chip.text.toString()/*.replace("'", "''")*/
            removeItem(check, tag)
            //Log.d("coxx22", "value: $check")
        }
        return chip
    }

    private fun removeItem(s: String, tag: String) {
        var id: String? = ""
        var idArr: ArrayList<String> = ArrayList()
        when (tag) {
            "wc" -> {
                //idArr = idWCArr
                id = ds.getCategoryIDByName(s)
                removeId(id, idWCArr, 3, acWCjobCat)
            }
            "bc" -> {
                //idArr = idBCArr
                id = ds.getCategoryIDByBanglaName(s)
                removeId(id, idBCArr, 3, acBCJobCat)
            }
            "orgs" -> {
                //idArr = idOrgArr
                id = ds.getOrgIDByOrgName(s)
                removeId(id, idOrgArr, 12, acOrgType)
            }
            "in" -> {
                //idArr = idInBDArr
                id = ds.getLocationIDByName(s)
                removeId(id, idInBDArr, 15, acInsideBD)
            }
            "out" -> {
                //idArr = idOutBDArr
                id = ds.getLocationIDByName(s)
                removeId(id, idOutBDArr, 10, acOutsideBD)
            }
        }
    }

    private fun updateData() {

//         else {
        if (anywhereinBD) prefDistrictIds = "-1" else {
            prefDistrictIds = ""
            if (idInBDArr.contains("-1"))
                idInBDArr.remove("-1")
            prefDistrictIds = TextUtils.join(",", idInBDArr)
        }
        activity?.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updatePrefAreasData(session.userId, session.decodId, session.IsResumeUpdate,
                prefWcIds, prefBcIds, prefDistrictIds, prefCountryIds, prefOrgIds)
        //Log.d("PrefAreas", "${TextUtils.join(",", idWCArr)} // [${TextUtils.join(",", idBCArr)}] ${TextUtils.join(",", idInBDArr)} // ${TextUtils.join(",", idOutBDArr)} // and check: // ${TextUtils.join(",", idOrgArr)}")
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                try {
                    activity?.stopProgressBar(loadingProgressBar)
                    activity?.toast("Can not connect to the server! Try again")
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(loadingProgressBar)
                        response.body()?.message?.let { activity.toast(it) }
                        if (response.body()?.statuscode == "4") {
                            prefCallBack.setBackFrom(Constants.prefAreasUpdate)
                            prefCallBack.goBack()
                            //onDestroy()
                        }
                    } else {
                        activity?.stopProgressBar(loadingProgressBar)
                        response.body()?.message?.let { activity.toast(it) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
//        }
    }

    private fun removeId(id: String?, idArr: ArrayList<String>, range: Int, acTv: AutoCompleteTextView) {
        if (idArr.contains(id)) {
            idArr.remove(id!!)
        }
        if (idArr.size < range)
            acTv.isEnabled = true

        //prefWcIds = TextUtils.join(",", idWCArr)
        d("selected rmv: $idArr")
    }


}

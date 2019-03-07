package com.bdjobs.app.editResume.personalInfo.fragments.preferredAreas


import android.app.Fragment
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferred_areas_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ds = DataStorage(activity)
        session = BdjobsUserSession(activity)
        prefCallBack = activity as PersonalInfo
        d("onActivityCreated")
        prefCallBack.setTitle(getString(R.string.title_pref_areas))
        prefCallBack.setEditButton(false, "dd")
        doWork()
    }

    override fun onResume() {
        super.onResume()
        data = prefCallBack.getPrefAreasData()
        preloadedData(data)
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
        clearAllArr()
        val jobCats = data.preferredJobCategories
        val bcjobCats = data.preferredBlueCategories
        val orgTypes = data.preferredOrganizationType
        val inBD = data.inside
        val outBD = data.outside
        //val
        //for ((i, value) in areaOfexps?.withIndex()!!)
        jobCats?.forEach {

            //idWCArr.clear()
            addChip(ds.getCategoryNameByID(it?.id!!), "wc", acWCjobCat)
            //addAsString(it.id, idWCArr)
            Log.d("prefs", "wc: $prefWcIds and $idWCArr")
            if (idWCArr.isNullOrEmpty()) tilWCjobCat.hideError() else tilWCjobCat.isErrorEnabled = true
        }
        bcjobCats?.forEach {
            //idBCArr.clear()
            addChip(ds.getCategoryBanglaNameByID(it?.id!!), "bc", acBCJobCat)
            //addAsString(it.id, idBCArr)
            Log.d("prefs", "bc: $prefBcIds and $idBCArr")
        }
        orgTypes?.forEach {
            //idOrgArr.clear()
            addChip(ds.getOrgNameByID(it?.id!!), "orgs", acOrgType)
            //addAsString(it.id, idOrgArr)
            Log.d("prefs", "org: $prefOrgIds and $idOrgArr")
        }

        if (!inBD.isNullOrEmpty()) {
            inBD.forEach {
                //idInBDArr.clear()
                if (it?.id != "-1")
                    addChip(ds.getLocationNameByID(it?.id!!).toString(), "in", acInsideBD)
                else {
                    anywhereinBD = true
                    changeBtnBackground(anywhereinBD)
                }
                //addAsString(it.id, idInBDArr)
                Log.d("prefs", "inBD: $prefDistrictIds and $idInBDArr")
            }
        }
        outBD?.forEach {
            //idOutBDArr.clear()
            addChip(ds.getLocationNameByID(it?.id!!).toString(), "out", acOutsideBD)
            //addAsString(it.id, idOutBDArr)
            Log.d("prefs", "outBD: $prefCountryIds and $idOutBDArr")
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
        //if (idWCArr.isNullOrEmpty()) {
        /*acWCjobCat.easyOnTextChangedListener { charSequence ->
            activity?.ACTVValidation(charSequence.toString(), acWCjobCat, tilWCjobCat)
        }*/
        //}
        changeBtnBackground(anywhereinBD)
        fab_prefAreas_update.setOnClickListener {
            var valid = 0
            prefWcIds = TextUtils.join(",", idWCArr)
            prefBcIds = TextUtils.join(",", idBCArr)
            prefOrgIds = TextUtils.join(",", idOrgArr)
            prefDistrictIds = TextUtils.join(",", idInBDArr)
            prefCountryIds = TextUtils.join(",", idOutBDArr)

            //valid = isValidateAutoCompleteTV(acInsideBD, tilInsideBD, null, true, valid)
            //valid = isValidateAutoCompleteTV(acOutsideBD, tilOutsideBD, null, true, valid)

            /*if (idWCArr.isEmpty()) {
                valid = isValidateAutoCompleteTV(acWCjobCat, tilWCjobCat, null, true, valid)
            }
*/
            //valid = isValidateAutoCompleteTV(acWCjobCat, tilWCjobCat, null, true, valid)
            //valid = isValidateAutoCompleteTV(acInsideBD, tilInsideBD, null, true, valid)
            when {
                idInBDArr.isEmpty() && idOutBDArr.isEmpty() -> {
                    tilInsideBD.isErrorEnabled = true
                    tilInsideBD.error = "This field can not be empty"
                    /*tilOutsideBD.isErrorEnabled = true
                    tilOutsideBD.error = "This field can not be empty"*/
                }
                idWCArr.isEmpty() && idBCArr.isEmpty() -> {
                    //tilWCjobCat.hideError()
                    tilWCjobCat.isErrorEnabled = true
                    tilWCjobCat.error = "This field can not be empty"
                    //tilBCJobCat.hideError()
                    tilBCJobCat.isErrorEnabled = true
                    tilBCJobCat.error = "This field can not be empty"
                }
                idWCArr.isEmpty() || idBCArr.isNotEmpty() -> {
                    valid += 1
                    tilWCjobCat.hideError()
                    tilBCJobCat.hideError()
                }
                idWCArr.isNotEmpty() || idBCArr.isEmpty() -> {
                    valid += 1
                    tilWCjobCat.hideError()
                    tilBCJobCat.hideError()
                }
                idBCArr.isNotEmpty() && idWCArr.isNotEmpty() -> {
                    valid += 1
                    tilBCJobCat.hideError()
                    tilWCjobCat.hideError()
                }
                anywhereinBD || idInBDArr.isNotEmpty() -> {
                    /*tilInsideBD.isErrorEnabled = true
                    tilInsideBD.error = "This field can not be empty"*/
                    valid += 1
                    tilInsideBD.hideError()
                }
                else -> {
                    tilWCjobCat.hideError()
                    tilBCJobCat.hideError()
                    tilInsideBD.hideError()
                }
            }

            /*if (idInBDArr.isNullOrEmpty()) {
                toast("Getting inBd null")
            }*/

            if (!idInBDArr.isNullOrEmpty() || !idOutBDArr.isNullOrEmpty() || anywhereinBD) valid += 1
            //if (idWCArr.isNullOrEmpty() && (idInBDArr.isNullOrEmpty() || idOutBDArr.isNullOrEmpty()))
            if (valid >= 2) {
                Log.d("ppppppp","$idWCArr, // $idInBDArr // ")
                updateData()
            } else {
                Log.d("ppppppp2","$idWCArr, // $idInBDArr // ")
            }

            Log.d("acWCjobCat", "wc: $prefWcIds// $prefBcIds// $prefOrgIds// $prefDistrictIds and $prefCountryIds")
        }

        btnAnywhereInBD.setOnClickListener {
            anywhereinBD = true
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
                acWCjobCat.setOnItemClickListener { _, _, position, id ->
                    d("acWCjobCat : ${acList[position + 1]} and gotStr : ${acWCjobCat.text} and $idWCArr")
                    val inputId = ds.getCategoryIDByName(acWCjobCat.text.toString())!!

                    if (idWCArr.size != 0) {
                        if (!idWCArr.contains(inputId)) {
                            addChip(ds.getCategoryNameByID(inputId), "wc", acWCjobCat)
                            Log.d("acWCjobCat", "arr2: $idWCArr")
                        }
                        else {
                            acWCjobCat.closeKeyboard(activity)
                            activity.toast("Category already added")
                        }
                        tilWCjobCat.hideError()
                    } else {
                        addChip(ds.getCategoryNameByID(inputId), "wc", acWCjobCat)
                        d("Array size : ${idWCArr.size} and $prefWcIds and id : $id")
                        /*isEmpty = true
                        experiencesTIL.isErrorEnabled = true*/
                        tilWCjobCat.hideError()
                    }
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
                acBCJobCat.setOnItemClickListener { _, _, position, id ->
                    d("Selected : ${acList[position + 1]} and gotStr : ${acBCJobCat.text}")
                    val inputId = ds.getCategoryIDByBanglaName(acBCJobCat.text.toString())!!
                    if (idBCArr.size != 0) {
                        if (!idBCArr.contains(inputId)) {
                            addChip(ds.getCategoryBanglaNameByID(inputId), "bc", acBCJobCat)
                            Log.d("acWCjobCat", "arr2: $idBCArr")
                        }
                        else {
                            acBCJobCat.closeKeyboard(activity)
                            activity.toast("ক্যাটাগরি ইতিমধ্যে যোগ করা হয়েছে")
                        }
                        tilBCJobCat.hideError()
                    } else {
                        addChip(ds.getCategoryBanglaNameByID(inputId), "bc", acBCJobCat)
                        d("Array size : ${idBCArr.size} and $prefBcIds and id : $id")
                        tilBCJobCat.hideError()
                    }
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
                acOrgType.setOnItemClickListener { _, _, position, id ->
                    d("Selected : ${acList[position + 1]} and gotStr : ${acOrgType.text}")
                    val inputId = ds.getOrgIDByOrgName(acOrgType.text.toString())
                    if (idOrgArr.size != 0) {
                        if (!idOrgArr.contains(inputId)) {
                            addChip(ds.getOrgNameByID(inputId), "orgs", acOrgType)
                            Log.d("acWCjobCat", "arr2: $idOrgArr")
                        }
                        else {
                            acOrgType.closeKeyboard(activity)
                            activity.toast("Organization type already added")
                        }
                        tilOrgType.hideError()
                    } else {
                        addChip(ds.getOrgNameByID(inputId), "orgs", acOrgType)
                        d("Array size : ${idOrgArr.size} and $prefOrgIds and id : $id")
                        tilOrgType.hideError()
                    }
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
                acInsideBD.setOnItemClickListener { _, _, position, id ->
                    d("Selected : ${acList[position + 1]} and gotStr : ${acInsideBD.text}")
                    val inputId = ds.getLocationIDByName(acInsideBD.text.toString())
                    if (idInBDArr.size != 0) {
                        if (!idInBDArr.contains(inputId)) {
                            addChip(ds.getLocationNameByID(inputId).toString(), "in", acInsideBD)
                            Log.d("acWCjobCat", "arr2: $idInBDArr")
                        }
                        else {
                            acInsideBD.closeKeyboard(activity)
                            activity.toast("District already added")
                        }
                        tilInsideBD.hideError()
                    } else {
                        addChip(ds.getLocationNameByID(inputId).toString(), "in", acInsideBD)
                        d("Array size : ${idInBDArr.size} and $prefDistrictIds and id : $id")
                        tilInsideBD.hideError()
                    }
                }
            }
        }
        acOutsideBD.onFocusChange { _, hasFocus ->
            if (hasFocus) {
                val acList: Array<String> = ds.allCountries
                val expsAdapter = ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line, acList)
                acOutsideBD.setAdapter(expsAdapter)
                acOutsideBD.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
                acOutsideBD.setOnItemClickListener { _, _, position, id ->
                    d("Selected : ${acList[position + 1]} and gotStr : ${acOutsideBD.text}")
                    val inputId = ds.getLocationIDByName(acOutsideBD.text.toString())
                    //idOutBDArr.add(inputId!!)
                    //Log.d("acWCjobCat", "Countryarr2: $idInBDArr")
                    if (idOutBDArr.size != 0) {
                        if (!idOutBDArr.contains(inputId)) {
                            addChip(ds.getLocationNameByID(inputId).toString(), "out", acOutsideBD)
                            Log.d("acWCjobCat", "arr2: $idOutBDArr")
                        }
                        else {
                            acOutsideBD.closeKeyboard(activity)
                            activity.toast("Country already added")
                        }
                        tilOutsideBD.hideError()
                    } else {
                        addChip(ds.getLocationNameByID(inputId).toString(), "out", acOutsideBD)
                        d("Array size : ${idOutBDArr.size} and $prefCountryIds and id : $id")
                        tilOutsideBD.hideError()
                    }
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
        var item = ""
        var maxItemsArr = ArrayList<String>()
        val cg: ChipGroup = when (tag) {
            "wc" -> {
                maxItems = 3
                maxItemsArr = idWCArr
                idWCArr.add(ds.getCategoryIDByName(input).toString())
                wc_entry_chip_group
            }
            "bc" -> {
                maxItems = 3
                maxItemsArr = idBCArr
                idBCArr.add(ds.getCategoryIDByName(input).toString())
                bc_entry_chip_group
            }
            "orgs" -> {
                maxItems = 12
                maxItemsArr = idOrgArr
                idOrgArr.add(ds.getCategoryIDByName(input).toString())
                org_entry_chip_group
            }
            "in" -> {
                maxItems = 15
                maxItemsArr = idInBDArr
                idInBDArr.add(ds.getCategoryIDByName(input).toString())
                pref_locs_entry_chip_group
            }
            "out" -> {
                maxItems = 10
                maxItemsArr = idOutBDArr
                idOutBDArr.add(ds.getCategoryIDByName(input).toString())
                pref_countries_entry_chip_group
            } else -> org_entry_chip_group
        }

        when {
            cg.childCount < maxItems -> {
                val c1 = getChip(cg, input, R.xml.chip_entry, tag)
                //maxItemsArr.add(ds.getCategoryIDByName(input).toString())
                cg.addView(c1)
            }
            cg.childCount == maxItems -> activity.toast("Maximum $maxItems items can be added.")
            else -> Log.d("chip_child","count: ${cg.childCount}")
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
            Log.d("coxx22", "value: $check")
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
                removeId(id, idWCArr)
            }
            "bc" -> {
                //idArr = idBCArr
                id = ds.getCategoryIDByBanglaName(s)
                removeId(id, idBCArr)
            }
            "orgs" -> {
                //idArr = idOrgArr
                id = ds.getOrgIDByOrgName(s)
                removeId(id, idOrgArr)
            }
            "in" -> {
                //idArr = idInBDArr
                id = ds.getLocationIDByName(s)
                removeId(id, idInBDArr)
            }
            "out" -> {
                //idArr = idOutBDArr
                id = ds.getLocationIDByName(s)
                removeId(id, idOutBDArr)
            }
        }
    }

    private fun updateData() {
        if (anywhereinBD) prefDistrictIds = "-1"
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updatePrefAreasData(session.userId, session.decodId, session.IsResumeUpdate,
                TextUtils.join(",", idWCArr), TextUtils.join(",", idBCArr), TextUtils.join(",", idInBDArr), TextUtils.join(",", idOutBDArr), TextUtils.join(",", idOrgArr))
        Log.d("PrefAreas", "${TextUtils.join(",", idWCArr)} // [${TextUtils.join(",", idBCArr)}] ${TextUtils.join(",", idInBDArr)} // ${TextUtils.join(",", idOutBDArr)} // and check: // ${TextUtils.join(",", idOrgArr)}")
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBar)
                activity.toast("Can not connect to the server! Try again")
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBar)
                        response.body()?.message?.let { activity.toast(it) }
                        if (response.body()?.statuscode == "4") {
                            prefCallBack.goBack()
                            onDestroy()
                        }
                    } else {
                        activity.stopProgressBar(loadingProgressBar)
                        response.body()?.message?.let { activity.toast(it) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }

    private fun withComma(ids: String): String? {
        return ",$ids,"
    }


    private fun removeId(id: String?, idArr: ArrayList<String>) {
        if (idArr.contains(id))
            idArr.remove(id!!)
        //prefWcIds = TextUtils.join(",", idWCArr)
        d("selected rmv: $idArr")
    }
}

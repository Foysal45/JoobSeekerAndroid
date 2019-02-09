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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.PreferredAreasData
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_preferred_areas_edit.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

class PreferredAreasEditFragment : Fragment() {
    private lateinit var prefCallBack: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var ds: DataStorage
    private lateinit var data: PreferredAreasData
    private var idArr: ArrayList<String> = ArrayList()
    private var idWCArr: ArrayList<String> = ArrayList()
    private var idBCArr: ArrayList<String> = ArrayList()
    private var idOrgArr: ArrayList<String> = ArrayList()
    private var idInBDArr: ArrayList<String> = ArrayList()
    private var idOutBDArr: ArrayList<String> = ArrayList()
    private var exps: String = ""
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
        doWork()
        prefCallBack.setTitle(getString(R.string.title_pref_areas))
        prefCallBack.setEditButton(false, "dd")
    }

    private fun doWork() {
        data = prefCallBack.getPrefAreasData()
        onClicks()
        preloadedData(data)
    }

    private fun preloadedData(data: PreferredAreasData) {
        val jobCats = data.preferredJobCategories
        val bcjobCats = data.preferredBlueCategories
        val orgTypes = data.preferredOrganizationType
        val inBD = data.inside
        val outBD = data.outside
        //val
        //for ((i, value) in areaOfexps?.withIndex()!!)
        jobCats?.forEach {
            addChip(ds.getCategoryNameByID(it?.id!!), "wc", acWCjobCat)
            addAsString(it.id)
        }
        bcjobCats?.forEach {
            addChip(ds.getCategoryBanglaNameByID(it?.id!!), "bc", acBCJobCat)
            addAsString(it.id)
        }
        orgTypes?.forEach {
            addChip(ds.getOrgNameByID(it?.id!!), "orgs", acOrgType)
            addAsString(it.id)
        }

        if (!inBD.isNullOrEmpty()) {
            inBD.forEach {
                addChip(ds.getLocationNameByID(it?.id!!).toString(), "in", acInsideBD)
                addAsString(it.id)
            }
        } else {
            anywhereinBD = true
            changeBtnBackground(anywhereinBD)
        }
        outBD?.forEach {
            addChip(ds.getLocationNameByID(it?.id!!).toString(), "out", acOutsideBD)
            addAsString(it.id)
        }

    }

    private fun onClicks() {
        changeBtnBackground(anywhereinBD)
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
                    d("Selected : ${acList[position + 1]} and gotStr : ${acWCjobCat.text}")
                    val inputId = ds.getCategoryIDByName(acWCjobCat.text.toString())!!
                    if (idArr.size != 0) {
                        if (!idArr.contains(inputId))
                            addChip(ds.getCategoryNameByID(inputId), "wc", acWCjobCat)
                        else {
                            acWCjobCat.closeKeyboard(activity)
                            activity.toast("Experience already added")
                        }
                        tilWCjobCat.hideError()
                    } else {
                        addChip(ds.getCategoryNameByID(inputId), "wc", acWCjobCat)
                        d("Array size : ${idArr.size} and $exps and id : $id")
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
                    if (idArr.size != 0) {
                        if (!idArr.contains(inputId))
                            addChip(ds.getCategoryBanglaNameByID(inputId), "bc", acBCJobCat)
                        else {
                            acBCJobCat.closeKeyboard(activity)
                            activity.toast("অভিজ্ঞতা ইতিমধ্যে যোগ করা হয়েছে")
                        }
                        tilBCJobCat.hideError()
                    } else {
                        addChip(ds.getCategoryBanglaNameByID(inputId), "bc", acBCJobCat)
                        d("Array size : ${idArr.size} and $exps and id : $id")
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
                    if (idArr.size != 0) {
                        if (!idArr.contains(inputId))
                            addChip(ds.getOrgNameByID(inputId), "orgs", acOrgType)
                        else {
                            acOrgType.closeKeyboard(activity)
                            activity.toast("Organization type already added")
                        }
                        tilOrgType.hideError()
                    } else {
                        addChip(ds.getOrgNameByID(inputId), "orgs", acOrgType)
                        d("Array size : ${idArr.size} and $exps and id : $id")
                        tilOrgType.hideError()
                    }
                }
            }
        }

    }

    private fun changeBtnBackground(b: Boolean) {
        if (b) {
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

    private fun addAsString(expID: String) {
        if (!idArr.contains(expID)) {
            idArr.add(expID.trim())
            exps = TextUtils.join(",", idArr)
        }

        d("selected exps:$exps and ids $idArr")
    }

    private fun addChip(input: String, tag: String, acTV: AutoCompleteTextView) {
        val maxItems: Int
        //val idsss
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
                maxItems = 10
                org_entry_chip_group
            }
            "in" -> {
                maxItems = 15
                pref_locs_entry_chip_group
            }
            "out" -> {
                maxItems = 10
                pref_countries_entry_chip_group
            }
            else -> {
                maxItems = 12
                wc_entry_chip_group
            }
        }

        if (cg.childCount <= maxItems - 1) {
            //addAsString(idsss)
            val c1 = getChip(cg, input, R.xml.chip_entry)
            cg.addView(c1)
        } else {
            activity.toast("Maximum $maxItems items can be added.")
        }
        acTV.clearText()
        clPrefAreasEdit?.closeKeyboard(activity)
    }

    private fun getChip(entryChipGroup: ChipGroup, text: String, item: Int): Chip {
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
            //removeItem(chip.text.toString())
        }
        return chip
    }

    private fun removeItem(s: String) {
        val id = ds.workDisciplineIDByWorkDiscipline(s)
        if (idArr.contains(id))
            idArr.remove("$id")
        exps = TextUtils.join(",", idArr)
        d("selected rmv: $exps and $idArr")
    }
}

package com.bdjobs.app.FavouriteSearch

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.SaveUpdateFavFilterModel
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.SuggestiveSearch.SuggestiveSearchActivity
import com.bdjobs.app.Utilities.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_favourite_search_filter_edit.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class FavouriteSearchFilterEditFragment : Fragment() {

    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var bdjobsDB: BdjobsDB
    private lateinit var favCommunicator: FavCommunicator
    private lateinit var dataStorage: DataStorage
    private var filterID = ""
    private var organization = ""
    private var experience = ""
    private var jobType = ""
    private var jobLevel = ""
    private var jobNature = ""
    private var postedWithin = ""
    private var deadline = ""
    private var age = ""
    private var army = ""
    private var location = ""
    private var keyword = ""
    private var newspaper = ""
    private var category = ""
    private var industry = ""
    private var filterName = ""
    private var createdOn: Date? = null
    private var gender = ""
    val genderList:MutableList<String> =ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_favourite_search_filter_edit, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        dataStorage = DataStorage(activity)
        favCommunicator = activity as FavCommunicator
        filterID = favCommunicator.getFilterID()
        onClicks()
        setData()

    }


    private fun goToSuggestiveSearch(from: String, typedData: String) {
        val intent = Intent(activity, SuggestiveSearchActivity::class.java)
        intent.putExtra(Constants.key_from, from)
        intent.putExtra(Constants.key_typedData, typedData)
        activity.window.exitTransition = null
        startActivityForResult(intent, Constants.BdjobsUserRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.BdjobsUserRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val typedData = data?.getStringExtra(Constants.key_typedData)
                val from = data?.getStringExtra(Constants.key_from)

                Log.d("typedData", "typedData : $typedData")

                when (from) {
                    Constants.key_jobtitleET -> {
                        keyword = typedData!!
                        keywordET?.setText(keyword)
                    }
                    Constants.key_loacationET -> {
                        location = dataStorage.getLocationIDByName(typedData!!)!!
                        loacationET?.setText(dataStorage.getLocationNameByID(location))
                    }
                    Constants.key_categoryET -> {
                        category = dataStorage.getCategoryIDByName(typedData!!)!!
                        generalCatET?.setText(dataStorage.getCategoryNameByID(category))
                    }
                    Constants.key_special_categoryET -> {
                        category = dataStorage.getCategoryIDByBanglaName(typedData!!)!!
                        specialCatET?.setText(dataStorage.getCategoryBanglaNameByID(category))
                    }
                    Constants.key_newspaperET -> {
                        newspaper = dataStorage.getNewspaperIDByName(typedData!!)!!
                        newsPaperET?.setText(dataStorage.getNewspaperNameById(newspaper))
                    }
                    Constants.key_industryET -> {
                        industry = dataStorage.getJobSearcIndustryIDbyName(typedData!!)!!
                        industryET?.setText(dataStorage.getJobSearcIndustryNameByID(industry))
                    }
                }
            }
        }
    }

    private fun onClicks() {
        backIV.setOnClickListener {
            favCommunicator?.backButtonPressed()
        }

        updateBTN.setOnClickListener {
            if(validateFilterName(filterNameET.getString(),filterNameTIL)) {
                updateFavSearch()
            }
        }
        filterNameET.easyOnTextChangedListener {
            validateFilterName(filterNameET.getString(),filterNameTIL)
        }

        keywordET?.easyOnTextChangedListener { text ->
            showHideCrossButton(keywordET)
            if (text?.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                keyword = ""
            }
        }
        generalCatET?.easyOnTextChangedListener { text ->
            showHideCrossButton(generalCatET)
            if (text?.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                category = ""
            }

        }

        loacationET?.easyOnTextChangedListener { text ->
            showHideCrossButton(loacationET)
            if (text?.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                location = ""
            }
        }
        specialCatET?.easyOnTextChangedListener { text ->
            showHideCrossButton(specialCatET)
            if (text?.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                category = ""
            }

        }
        newsPaperET?.easyOnTextChangedListener { text ->
            showHideCrossButton(newsPaperET)
            if (text?.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                newspaper = ""
            }
        }
        industryET?.easyOnTextChangedListener { text ->
            showHideCrossButton(industryET)
            if (text?.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                industry = ""
            }
        }

        keywordET?.setOnClickListener {
            goToSuggestiveSearch(Constants.key_jobtitleET, keywordET.text.toString())
        }
        generalCatET?.setOnClickListener {
            goToSuggestiveSearch(Constants.key_categoryET, generalCatET.text.toString())
        }
        specialCatET?.setOnClickListener {
            goToSuggestiveSearch(Constants.key_special_categoryET, specialCatET.text.toString())
        }
        loacationET?.setOnClickListener {
            goToSuggestiveSearch(Constants.key_loacationET, loacationET.text.toString())
        }

        newsPaperET?.setOnClickListener {
            goToSuggestiveSearch(Constants.key_newspaperET, newsPaperET.text.toString())
        }

        industryET?.setOnClickListener {
            goToSuggestiveSearch(Constants.key_industryET, industryET.text.toString())
        }

        getDataFromChipGroup(orgCG)
        getDataFromChipGroup(experienceCG)
        getDataFromChipGroup(jobTypeCG)
        getDataFromChipGroup(jobLevelCG)
        getDataFromChipGroup(jobNatureCG)
        getDataFromChipGroup(postedWithinCG)
        getDataFromChipGroup(deadlineCG)
        getDataFromChipGroup(ageRangeCG)
        getDataFromChipGroup(armyCG)


        maleChip?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if("M" !in genderList){
                    genderList.add("M")
                }
            } else {
                if("M" in genderList) {
                    genderList.remove("M")
                }
            }
            gender = genderList.joinToString(transform = { it })
        }

        femaleChip?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if("F" !in genderList){
                    genderList.add("F")
                }
            } else {
                if("F" in genderList) {
                    genderList.remove("F")
                }
            }
            gender = genderList.joinToString(transform = { it })

        }

        otherChip?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if("O" !in genderList){
                    genderList.add("O")
                }
            } else {

                if("O" in genderList) {
                    genderList.remove("O")
                }
            }
            gender = genderList.joinToString(transform = { it })

        }
    }

    private fun updateFavSearch() {

        filterName = filterNameET.getString()


        val loadingDialog = indeterminateProgressDialog("Saving")
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
        ApiServiceJobs.create().saveOrUpdateFilter(
                icat = industry,
                fcat = category,
                location = location,
                qOT = organization,
                qJobNature = jobNature,
                qJobLevel = jobLevel,
                qPosted = postedWithin,
                qDeadline = deadline,
                txtsearch = keyword,
                qExp = experience,
                qGender = gender,
                qGenderB = "",
                qJobSpecialSkill = jobType,
                qRetiredArmy = army,
                savefilterid = filterID,
                userId = bdjobsUserSession.userId,
                filterName = filterNameET.getString().trim(),
                qAge = age,
                newspaper = newspaper,
                encoded = Constants.ENCODED_JOBS

        ).enqueue(object : Callback<SaveUpdateFavFilterModel> {
            override fun onFailure(call: Call<SaveUpdateFavFilterModel>, t: Throwable) {
                loadingDialog.dismiss()
                error("onFailure", t)
                toast("${t.message}")
            }

            override fun onResponse(call: Call<SaveUpdateFavFilterModel>, response: Response<SaveUpdateFavFilterModel>) {

                try {
                    Log.d("resposet", response.body().toString())

                    if (response?.body()?.data?.get(0)?.status?.equalIgnoreCase("0")!!) {
                        doAsync {
                            val favouriteSearch = FavouriteSearch(
                                    filterid = filterID,
                                    filtername = filterName.trim(),
                                    industrialCat = industry,
                                    functionalCat = category,
                                    location = location,
                                    organization = organization,
                                    jobnature = jobNature,
                                    joblevel = jobLevel,
                                    postedon = postedWithin,
                                    deadline = deadline,
                                    keyword = keyword,
                                    newspaper = newspaper,
                                    gender = gender,
                                    experience = experience,
                                    age = age,
                                    jobtype = jobType,
                                    retiredarmy = army,
                                    updatedon = Date(),
                                    totaljobs = "",
                                    createdon = createdOn,
                                    genderb = ""

                            )
                            bdjobsDB.favouriteSearchFilterDao().updateFavouriteSearchFilter(favouriteSearch)

                            uiThread {
                                loadingDialog.dismiss()
                                toast("${response?.body()?.data?.get(0)?.message}")
                                favCommunicator.backButtonPressed()
                            }
                        }

                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }

    private fun getDataFromChipGroup(chipGroup: ChipGroup) {
        chipGroup.setOnCheckedChangeListener { chipGroup, i ->
            if (i > 0) {
                val chip = chipGroup?.findViewById(i) as Chip
                Log.d("chip", "text: ${chip.text}")
                val data = chip?.text.toString()
                when (chipGroup.id) {
                    R.id.orgCG -> {
                        organization = dataStorage.getJobSearcOrgTypeIDByName(data)!!
                    }
                    R.id.experienceCG -> {
                        experience = dataStorage.getJobExperineceIDByName(data)!!
                    }
                    R.id.jobTypeCG -> {
                        jobType = dataStorage.getJobTypeIDByName(data)!!
                    }
                    R.id.jobLevelCG -> {
                        jobLevel = dataStorage.getJobLevelIDByName(data.toLowerCase())!!
                    }
                    R.id.jobNatureCG -> {
                        jobNature = dataStorage.getJobNatureIDByName(data.toLowerCase())!!
                    }
                    R.id.postedWithinCG -> {
                        postedWithin = dataStorage.getPostedWithinIDByName(data)!!
                    }
                    R.id.deadlineCG -> {
                        deadline = dataStorage.getDeadlineIDByNAme(data)!!
                    }
                    R.id.ageRangeCG -> {
                        age = dataStorage.getAgeRangeIDByName(data)!!
                    }
                    R.id.armyCG -> {
                        army = "1"
                    }
                }
            } else {
                when (chipGroup.id) {
                    R.id.orgCG -> {
                        organization = ""
                    }
                    R.id.experienceCG -> {
                        experience = ""
                    }
                    R.id.jobTypeCG -> {
                        jobType = ""
                    }
                    R.id.jobLevelCG -> {
                        jobLevel = ""
                    }
                    R.id.jobNatureCG -> {
                        jobNature = ""
                    }
                    R.id.postedWithinCG -> {
                        postedWithin = ""
                    }
                    R.id.deadlineCG -> {
                        deadline = ""
                    }
                    R.id.ageRangeCG -> {
                        age = ""
                    }
                    R.id.armyCG -> {
                        army = "1"
                    }
                }
            }
        }
    }

    private fun setGenderData(gndr: String?) {
        val tempGender = gndr
        Log.d("GenderCheck", "tempGender: $tempGender")
        maleChip?.isChecked = false
        femaleChip?.isChecked = false
        otherChip?.isChecked = false
        val genderList = tempGender?.split(",")
        genderList?.forEach { it ->
            Log.d("GenderCheck", "genderGet: $it")
            Log.d("GenderCheck", " dataStorage genderGet: ${dataStorage.getGenderByID(it.trim())}")
            selectChip(genderCG, dataStorage.getGenderByID(it.trim()))
        }
    }

    private fun selectChip(chipGroup: ChipGroup, data: String) {
        val count = chipGroup.childCount
        for (i in 0 until count) {
            val chip = chipGroup.getChildAt(i) as Chip
            val chipText = chip.text.toString()
            if (data.equalIgnoreCase(chipText)) {
                Log.d("chip", "text:$i")
                chip?.isChecked = true
            }
        }
    }

    private fun setData() {
        Log.d("filterID", "filterID= $filterID")
        doAsync {
            val filterData = bdjobsDB.favouriteSearchFilterDao().getFavouriteSearchByID(filterid = filterID)

            uiThread {
                setGenderData(filterData.gender)
                filterName = filterData?.filtername!!
                createdOn = filterData?.createdon
                filterNameET?.setText(filterName)
                keywordET?.setText(filterData.keyword)
                Log.d("catTest", "category : ${filterData.keyword}")

                if (filterData?.functionalCat?.isNotBlank()!!) {
                    if (filterData?.functionalCat.toInt() < 30) {
                        generalCatET?.setText(dataStorage.getCategoryNameByID(filterData?.functionalCat))
                        specialCatET?.text?.clear()
                    } else {
                        generalCatET?.text?.clear()
                    }

                    if (filterData?.functionalCat.toInt() > 60) {
                        specialCatET?.setText(dataStorage.getCategoryBanglaNameByID(filterData?.functionalCat))
                        generalCatET?.text?.clear()
                    } else {
                        specialCatET?.text?.clear()
                    }
                }


                keyword = filterData?.keyword!!
                category = filterData?.functionalCat!!
                location = filterData?.location!!
                industry = filterData?.industrialCat!!
                newspaper = filterData?.newspaper!!



                loacationET?.setText(dataStorage.getLocationNameByID(filterData.location))
                newsPaperET?.setText(dataStorage.getNewspaperNameById(filterData.newspaper))
                industryET?.setText(dataStorage.getJobSearcIndustryNameByID(filterData.industrialCat))
                selectChip(orgCG, dataStorage.getJobSearcOrgTypeByID(filterData.organization)!!)
                selectChip(experienceCG, dataStorage.getJobExperineceByID(filterData.experience)!!)
                selectChip(jobTypeCG, dataStorage.getJobTypeByID(filterData.jobtype)!!)
                selectChip(jobLevelCG, dataStorage.getJobLevelByID(filterData.joblevel)!!)
                selectChip(jobNatureCG, dataStorage.getJobNatureByID(filterData.jobnature)!!)
                selectChip(postedWithinCG, dataStorage.getPostedWithinNameByID(filterData.postedon)!!)
                selectChip(deadlineCG, dataStorage.getDedlineNameByID(filterData.deadline)!!)
                selectChip(ageRangeCG, dataStorage.getAgeRangeNameByID(filterData.age)!!)
                if (filterData.retiredarmy == "1") {
                    selectChip(armyCG, "Yes")
                }
            }
        }
    }

    private fun validateFilterName(typedData: String, textInputLayout: TextInputLayout): Boolean {

        if (typedData.trim().isNullOrBlank()) {
            textInputLayout.showError(getString(R.string.field_empty_error_message_common))
            return false
        }
        textInputLayout.hideError()
        return true
    }

    private fun showHideCrossButton(editText: EditText) {
        if (editText?.text.isBlank()) {
            editText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            editText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_ash, 0)
            editText?.clearTextOnDrawableRightClick()
        }
    }
}
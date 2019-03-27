package com.bdjobs.app.Jobs

import android.app.Dialog
import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.JobListModel
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.API.ModelClasses.SaveUpdateFavFilterModel
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.Databases.Internal.LastSearch
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.ENCODED_JOBS
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_joblist_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class JoblistFragment : Fragment() {

    private lateinit var session: BdjobsUserSession
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var joblistAdapter: JoblistAdapter? = null
    private var jobListGet: MutableList<JobListModelData>? = null
    private var currentPage = 1
    private var TOTAL_PAGES: Int? = null
    private var isLoadings = false
    private var isLastPages = false
    private lateinit var communicator: JobCommunicator

    private var keyword:String? = ""
    private var location:String? = ""
    private var category:String? = ""
    private var newsPaper:String? = ""
    private var industry:String? = ""
    private var organization:String? = ""
    private var gender:String? = ""
    private var experience:String? = ""
    private var jobType:String? = ""
    private var jobLevel:String? = ""
    private var jobNature:String? = ""
    private var postedWithin:String? = ""
    private var deadline:String? = ""
    private var age:String? = ""
    private var army:String? = ""
    private var filterName:String? = ""
    private var filterID:String? = ""
    lateinit var bdjobsDB: BdjobsDB

    var totalRecordsFound = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_joblist_layout, container, false)!!

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsDB = BdjobsDB.getInstance(activity)
    }

    private fun getData() {
        keyword = communicator?.getKeyword()
        location = communicator?.getLocation()
        category = communicator?.getCategory()
        newsPaper = communicator?.getNewsPaper()
        industry = communicator?.getIndustry()
        organization = communicator?.getOrganization()
        gender = communicator?.getGender()
        experience = communicator?.getExperience()
        jobType = communicator?.getJobType()
        jobLevel = communicator?.getJobLevel()
        jobNature = communicator?.getJobNature()
        postedWithin = communicator?.getPostedWithin()
        deadline = communicator?.getDeadline()
        age = communicator?.getAge()
        army = communicator?.getArmy()

        Log.d("wtji","joblist=>\nkeyword: $keyword \nlocation: $location\n category:$category")

        saveSearchDicission()

        suggestiveSearchET?.text = keyword
        suggestiveSearchET?.setOnClickListener { et ->
            communicator.goToSuggestiveSearch(Constants.key_jobtitleET, suggestiveSearchET.text.toString())
        }

        joblistAdapter!!.clear()

        if (session.isLoggedIn!!) {
            val lastSearch = LastSearch(searchTime = Date(),
                    jobLevel = jobLevel,
                    newsPaper = newsPaper,
                    armyp = army,
                    blueColur = "",
                    category = category,
                    deadline = deadline,
                    encoded = ENCODED_JOBS,
                    experince = experience,
                    gender = gender,
                    genderB = "",
                    industry = industry,
                    isFirstRequest = "",
                    jobnature = jobNature,
                    jobType = jobType,
                    keyword = keyword,
                    lastJPD = "",
                    location = location,
                    organization = organization,
                    pageId = "",
                    pageNumber = 1,
                    postedWithIn = postedWithin,
                    age = age,
                    rpp = "",
                    slno = "",
                    version = "")

            doAsync {
                bdjobsDB.lastSearchDao().updateLastSearch(lastSearch = lastSearch)
            }
        }

        loadFisrtPageTest(
                jobLevel = jobLevel,
                newsPaper = newsPaper,
                armyp = army,
                blueColur = "",
                category = category,
                deadline = deadline,
                encoded = ENCODED_JOBS,
                experince = experience,
                gender = gender,
                genderB = "",
                industry = industry,
                isFirstRequest = "",
                jobnature = jobNature,
                jobType = jobType,
                keyword = keyword,
                lastJPD = "",
                location = location,
                organization = organization,
                pageId = "",
                pageNumber = 1,
                postedWithIn = postedWithin,
                age = age,
                rpp = "",
                slno = "",
                version = ""
        )
    }

    private fun saveSearchDicission() {
        try {
            filterName = communicator.getFilterName()
            filterID = communicator.getFilterID()

            if (filterName?.isNotBlank()!!) {

                doAsync {
                    val favsearch = bdjobsDB.favouriteSearchFilterDao().getFavFilterByFilters(
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
                            qAge = age,
                            newspaper = newsPaper
                    )

                    uiThread {
                        if (!favsearch.isNullOrEmpty()) {
                            saveSearchBtn.text = filterName
                            saveSearchBtn.setIconTintResource(R.color.fav_search_save)
                        }
                        saveSearchBtn.setOnClickListener {
                            if (!favsearch.isNullOrEmpty()) {
                                Snackbar.make(parentCL, "Search is already saved", Snackbar.LENGTH_LONG).show()
                            } else {
                                saveSearch()
                            }
                        }
                    }
                }

            } else {
                saveSearchBtn.setOnClickListener {
                    saveSearch()
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        session = BdjobsUserSession(activity)
        currentPage = 1
        jobListRecyclerView?.setHasFixedSize(true)
        communicator = activity as JobCommunicator
        layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        jobListRecyclerView?.layoutManager = layoutManager
        joblistAdapter = JoblistAdapter(activity)
        jobListRecyclerView?.adapter = joblistAdapter

        onClick()
        if (communicator.getBackFrom().equalIgnoreCase("")) {
            getData()
        } else {
            getDataNew()
        }

        jobListRecyclerView?.addOnScrollListener(object : PaginationScrollListener(layoutManager!! as LinearLayoutManager) {

            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages
            override var isLoading: Boolean = false
                get() = isLoadings

            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                communicator.setpageNumber(currentPage)

                loadNextPage(
                        jobLevel = jobLevel,
                        newsPaper = newsPaper,
                        armyp = army,
                        blueColur = "",
                        category = category,
                        deadline = deadline,
                        encoded = ENCODED_JOBS,
                        experince = experience,
                        gender = gender,
                        genderB = "",
                        industry = industry,
                        isFirstRequest = "",
                        jobnature = jobNature,
                        jobType = jobType,
                        keyword = keyword,
                        lastJPD = "",
                        location = location,
                        organization = organization,
                        pageId = "",
                        pageNumber = currentPage,
                        postedWithIn = postedWithin,
                        age = age,
                        rpp = "",
                        slno = "",
                        version = ""
                )
            }
        })

    }

    private fun getDataNew() {


        try {
            jobListGet = communicator.getJobList()!!
        } catch (e: Exception) {
            logException(e)
        }


        try {
            Log.d("djggsgdjdg", "jobListGet ${jobListGet!!.size}")

            Log.d("djggsgdjdg", "clickedPosition ${communicator.getItemClickPosition()}")

            Log.d("djggsgdjdg", "clickedPosition data ${jobListGet!!.get(communicator.getItemClickPosition()).jobTitle}}")

            Log.d("djggsgdjdg", "clickedPosition data ${jobListGet!!.get(communicator.getItemClickPosition()).jobTitle}}")
        } catch (e: Exception) {
            logException(e)
        }




        try {
            if (totalRecordsFound.toInt() > 1) {
                val styledText = "<b><font color='#13A10E'>$totalRecordsFound</font></b> Jobs"
                jobCounterTV?.text = Html.fromHtml(styledText)
            } else {
                val styledText = "<b><font color='#13A10E'>$totalRecordsFound</font></b> Job"
                jobCounterTV?.text = Html.fromHtml(styledText)
            }

            if(totalRecordsFound>0){
                noDataLL?.hide()
            }else{
                noDataLL?.show()
            }
        } catch (e: Exception) {
            logException(e)
        }


        currentPage = communicator?.getCurrentPageNumber()
        TOTAL_PAGES = communicator?.getTotalPage()
        isLastPages = communicator?.getLastPasge()

        keyword = communicator?.getKeyword()
        location = communicator?.getLocation()
        category = communicator?.getCategory()
        newsPaper = communicator?.getNewsPaper()
        industry = communicator?.getIndustry()
        organization = communicator?.getIndustry()
        gender = communicator?.getGender()
        experience = communicator?.getExperience()
        jobType = communicator?.getJobType()
        jobLevel = communicator?.getJobLevel()
        jobNature = communicator?.getJobNature()
        postedWithin = communicator?.getPostedWithin()
        deadline = communicator?.getDeadline()
        age = communicator?.getAge()
        army = communicator?.getArmy()

        saveSearchDicission()

        suggestiveSearchET?.text = keyword

        suggestiveSearchET?.setOnClickListener { et ->
            communicator?.setBackFrom("")
            communicator?.goToSuggestiveSearch(Constants.key_jobtitleET, suggestiveSearchET.text.toString())
        }

        loadFirstPageNew()
        Handler().postDelayed({ jobListRecyclerView?.scrollToPosition(communicator.getCurrentJobPosition()) }, 200)
        Log.d("jobListRecyclerView", "getCurrentJobPosition = ${communicator.getCurrentJobPosition()}")


    }


    private fun loadFirstPageNew() {
        try {
            joblistAdapter?.clear()
            joblistAdapter?.addAllTest(jobListGet as List<JobListModelData>)
            if (currentPage == TOTAL_PAGES!!) {
                isLastPages = true
            }

        } catch (e: Exception) {
            logException(e)
        }
    }


    private fun loadFisrtPageTest(jobLevel: String?, newsPaper: String?, armyp: String?, blueColur: String?, category: String?, deadline: String?, encoded: String?, experince: String?, gender: String?, genderB: String?, industry: String?, isFirstRequest: String?, jobnature: String?, jobType: String?, keyword: String?, lastJPD: String?, location: String?, organization: String?, pageId: String?, pageNumber: Int, postedWithIn: String?, age: String?, rpp: String?, slno: String?, version: String?) {

        Log.d("Paramtest", "jobLevel: $jobLevel")

        jobListRecyclerView.hide()
        filterLayout.hide()
        shimmer_view_container_JobList.show()
        shimmer_view_container_JobList.startShimmerAnimation()

        val call = ApiServiceJobs.create().getJobList(jobLevel = jobLevel,
                Newspaper = newsPaper,
                armyp = armyp,
                bc = blueColur,
                category = category,
                deadline = deadline,
                encoded = encoded,
                experience = experince,
                gender = gender,
                genderB = genderB,
                industry = industry,
                isFirstRequest = isFirstRequest,
                jobNature = jobnature,
                jobType = jobType,
                keyword = keyword,
                lastJPD = lastJPD,
                location = location,
                org = organization,
                pageid = pageId,
                pg = pageNumber,
                postedWithin = postedWithIn,
                qAge = age,
                rpp = rpp,
                slno = slno,
                version = version)
        call.enqueue(object : Callback<JobListModel> {

            override fun onResponse(call: Call<JobListModel>?, response: Response<JobListModel>) {

                try {
                    if (response.isSuccessful) {
                        jobListRecyclerView?.show()
                        filterLayout.show()
                        shimmer_view_container_JobList.hide()
                        shimmer_view_container_JobList.stopShimmerAnimation()

                        val jobResponse = response.body()

                        TOTAL_PAGES = jobResponse?.common?.totalpages


                        Log.d("dkgjn", " Total page " + jobResponse?.common?.totalpages)
                        Log.d("dkgjn", " totalRecordsFound " + jobResponse?.common?.totalRecordsFound)

                        communicator.totalJobCount(jobResponse?.common?.totalRecordsFound)
                        val results = response.body()?.data

                        if (!results.isNullOrEmpty()) {
                            joblistAdapter?.addAllTest(results)
                        }

                        if (currentPage == TOTAL_PAGES!!) {
                            isLastPages = true
                        } else {
                            joblistAdapter?.addLoadingFooter()
                        }

                        val totalJobs = jobResponse!!.common!!.totalRecordsFound
                        if (totalJobs?.toInt()!! > 1) {
                            val styledText = "<b><font color='#13A10E'>$totalJobs</font></b> Jobs"
                            jobCounterTV?.text = Html.fromHtml(styledText)
                        } else {
                            val styledText = "<b><font color='#13A10E'>$totalJobs</font></b> Job"
                            jobCounterTV?.text = Html.fromHtml(styledText)

                        }

                        if(totalJobs>0){
                            jobListRecyclerView?.show()
                            noDataLL?.hide()
                        }else{
                            jobListRecyclerView?.hide()
                            noDataLL?.show()
                        }
                        communicator.setIsLoading(isLoadings)
                        communicator.setLastPasge(isLastPages)
                        communicator.setTotalJob(jobResponse.common!!.totalRecordsFound!!.toInt())
                        totalRecordsFound = jobResponse.common.totalRecordsFound!!.toInt()

                    } else {
                        /*Log.d("TAG", "not successful: $TAG")*/
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }

            override fun onFailure(call: Call<JobListModel>?, t: Throwable) {
                Log.d("TAG", "not successful!! onFail")
                error("onFailure", t)
            }
        })
    }

    private fun loadNextPage(jobLevel: String?, newsPaper: String?, armyp: String?, blueColur: String?, category: String?, deadline: String?, encoded: String?, experince: String?, gender: String?, genderB: String?, industry: String?, isFirstRequest: String?, jobnature: String?, jobType: String?, keyword: String?, lastJPD: String?, location: String?, organization: String?, pageId: String?, pageNumber: Int, postedWithIn: String?, age: String?, rpp: String?, slno: String?, version: String?) {
        Log.d("ArrayTest", " loadNextPage called")

        Log.d("Paramtest", "jobLevel: $jobLevel")

        val call = ApiServiceJobs.create().getJobList(jobLevel = jobLevel,
                Newspaper = newsPaper,
                armyp = armyp,
                bc = blueColur,
                category = category,
                deadline = deadline,
                encoded = encoded,
                experience = experince,
                gender = gender,
                genderB = genderB,
                industry = industry,
                isFirstRequest = isFirstRequest,
                jobNature = jobnature,
                jobType = jobType,
                keyword = keyword,
                lastJPD = lastJPD,
                location = location,
                org = organization,
                pageid = pageId,
                pg = pageNumber,
                postedWithin = postedWithIn,
                qAge = age,
                rpp = rpp,
                slno = slno,
                version = version)
        call.enqueue(object : Callback<JobListModel> {

            override fun onResponse(call: Call<JobListModel>?, response: Response<JobListModel>) {

                try {
                    Log.d("Paramtest", "response :   ${response.body().toString()}")
                    if (response.isSuccessful) {

                        try {
                            val resp_jobs = response.body()
                            TOTAL_PAGES = resp_jobs?.common?.totalpages
                            joblistAdapter?.removeLoadingFooter()
                            isLoadings = false

                            val results = response.body()?.data
                            joblistAdapter?.addAllTest(results as List<JobListModelData>)

                            if (currentPage == TOTAL_PAGES) {
                                isLastPages = true
                            } else {
                                joblistAdapter?.addLoadingFooter()
                            }

                            communicator.setIsLoading(isLoadings)
                            communicator.setLastPasge(isLastPages)
                            communicator.setTotalJob(resp_jobs?.common!!.totalRecordsFound!!.toInt())



                            totalRecordsFound = resp_jobs.common.totalRecordsFound!!


                            if (totalRecordsFound.toInt() > 1) {
                                val styledText = "<b><font color='#13A10E'>$totalRecordsFound</font></b> Jobs"
                                jobCounterTV?.text = Html.fromHtml(styledText)
                            } else {
                                val styledText = "<b><font color='#13A10E'>$totalRecordsFound</font></b> Job"
                                jobCounterTV?.text = Html.fromHtml(styledText)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("TAG", "not successful: ")
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }

            override fun onFailure(call: Call<JobListModel>?, t: Throwable?) {
                Log.d("TAG", "not successful!! onFail")
            }
        })
    }


    private fun onClick() {
        backIV?.setOnClickListener {
            communicator?.backButtonPressesd()
        }
        filterIMGV?.setOnClickListener {
            communicator?.goToAdvanceSearch()
        }

        advnSearchFloatBtn?.setOnClickListener {
            communicator?.goToAdvanceSearch()
        }

    }

    private fun saveSearch() {
        if (industry.isNullOrBlank() &&
                category.isNullOrBlank() &&
                location.isNullOrBlank() &&
                organization.isNullOrBlank() &&
                jobNature.isNullOrBlank() &&
                jobLevel.isNullOrBlank() &&
                postedWithin.isNullOrBlank() &&
                deadline.isNullOrBlank() &&
                keyword.isNullOrBlank() &&
                experience.isNullOrBlank() &&
                gender.isNullOrBlank() &&
                jobType.isNullOrBlank() &&
                army.isNullOrBlank() &&
                age.isNullOrBlank() &&
                newsPaper.isNullOrBlank()
        ) {
            Snackbar.make(parentCL, "Please apply at least one filter to save the search", Snackbar.LENGTH_LONG).show()
        } else {
            if (!session.isLoggedIn!!) {
                communicator.goToLoginPage()
            } else {
                var tempFilterID:String? = ""
                val saveSearchDialog = Dialog(activity)
                saveSearchDialog.setContentView(R.layout.save_search_dialog_layout)
                saveSearchDialog.setCancelable(true)
                saveSearchDialog.show()
                val saveBTN = saveSearchDialog.findViewById(R.id.saveBTN) as Button
                val cancelBTN = saveSearchDialog.findViewById(R.id.cancelBTN) as Button
                val filterNameET = saveSearchDialog.findViewById(R.id.filterNameET) as EditText
                val textInputLayout = saveSearchDialog.findViewById(R.id.textInputLayout) as TextInputLayout
                val updateCG = saveSearchDialog.findViewById(R.id.updateCG) as ChipGroup

                Log.d("FavParams", " icat = $industry, fcat = $category, location = $location, qOT = $organization, qJobNature = $jobNature, qJobLevel = $jobLevel, qPosted= $postedWithin, qDeadline= $deadline, txtsearch = $keyword, qExp = $experience, qGender = $gender, qGenderB= ,qJobSpecialSkill = $jobType, qRetiredArmy= $army,userId= ${session.userId},filterName = ${filterNameET.getString()},qAge = $age,newspaper = $newsPaper,encoded = ${Constants.ENCODED_JOBS}")


                filterNameET?.easyOnTextChangedListener { text ->
                    validateFilterName(text.toString(), textInputLayout)
                }

                cancelBTN?.setOnClickListener {
                    saveSearchDialog.dismiss()
                }

                if (filterID?.isNotBlank()!!) {
                    updateCG.show()
                } else {
                    updateCG.hide()
                }

                if (filterName?.isNotBlank()!!) {
                    filterNameET.setText(filterName)
                }


                if (updateCG.isVisible) {
                    saveBTN.isEnabled = false
                    updateCG.setOnCheckedChangeListener { chipGroup, id ->
                        if (id > 0) {
                            saveBTN.isEnabled = true
                            when (id) {
                                R.id.updateChip -> tempFilterID = filterID
                                R.id.saveChip -> tempFilterID = ""
                            }
                        } else {
                            saveBTN.isEnabled = false
                        }
                    }
                }

                saveBTN?.setOnClickListener {

                    if (validateFilterName(filterNameET.getString(), textInputLayout)) {
                        if (!session.isLoggedIn!!) {
                            communicator.goToLoginPage()
                        } else {
                            if (filterID == "") {
                                doAsync {
                                    val favsearchList = bdjobsDB.favouriteSearchFilterDao().getAllFavouriteSearchFilter()
                                    val favSearch = bdjobsDB.favouriteSearchFilterDao().getFavouriteSearchByName(filterNameET.getString().trim())

                                    var fid = ""
                                    try {
                                        fid = favSearch.filterid!!
                                    } catch (e: java.lang.Exception) {
                                        logException(e)
                                    }

                                    uiThread {
                                        Log.d("favsearchList", "size: ${favsearchList.size} fid: $fid")
                                        if (favsearchList.size > 9) {
                                            toast("You cannot add more than 10 Favourite Search")
                                        } else if (fid != "") {
                                            toast("This filter name is already exists.")
                                        } else {
                                            saveSearchIntoAPIAndDB(tempFilterID = tempFilterID, filterName = filterNameET.getString(), saveSearchDialog = saveSearchDialog)
                                        }
                                    }
                                }

                            } else {
                                saveSearchIntoAPIAndDB(tempFilterID = tempFilterID, filterName = filterNameET.getString(), saveSearchDialog = saveSearchDialog)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveSearchIntoAPIAndDB(tempFilterID: String?, filterName: String?, saveSearchDialog: Dialog) {
        val loadingDialog = indeterminateProgressDialog("Saving")
        loadingDialog.setCancelable(false)
        loadingDialog.show()

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
                savefilterid = tempFilterID,
                userId = session.userId,
                filterName = filterName?.trim(),
                qAge = age,
                newspaper = newsPaper,
                encoded = Constants.ENCODED_JOBS

        ).enqueue(object : Callback<SaveUpdateFavFilterModel> {
            override fun onFailure(call: Call<SaveUpdateFavFilterModel>, t: Throwable) {
                loadingDialog.dismiss()
                error("onFailure", t)
            }

            override fun onResponse(call: Call<SaveUpdateFavFilterModel>, response: Response<SaveUpdateFavFilterModel>) {

                try {
                    if (response.body()?.data?.get(0)?.status?.equalIgnoreCase("0")!!) {
                        doAsync {

                            try {
                                val favouriteSearch = FavouriteSearch(
                                        filterid = response.body()?.data?.get(0)?.sfilterid,
                                        filtername = filterName?.trim(),
                                        industrialCat = industry,
                                        functionalCat = category,
                                        location = location,
                                        organization = organization,
                                        jobnature = jobNature,
                                        joblevel = jobLevel,
                                        postedon = postedWithin,
                                        deadline = deadline,
                                        keyword = keyword,
                                        newspaper = newsPaper,
                                        gender = gender,
                                        experience = experience,
                                        age = age,
                                        jobtype = jobType,
                                        retiredarmy = army,
                                        createdon = Date(),
                                        updatedon = null,
                                        totaljobs = "",
                                        genderb = ""
                                )

                                bdjobsDB.favouriteSearchFilterDao().updateFavouriteSearchFilter(favouriteSearch)

                                communicator.setFilterID(response.body()?.data?.get(0)?.sfilterid!!)
                                communicator.setFilterName(filterName)
                                uiThread {
                                    loadingDialog.dismiss()
                                    toast("${response.body()?.data?.get(0)?.message}")
                                    saveSearchDialog.dismiss()
                                    saveSearchDicission()
                                }
                            } catch (e: Exception) {
                                logException(e)
                            }
                        }

                    } else {
                        loadingDialog?.dismiss()
                        saveSearchDialog?.dismiss()
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }


    private fun validateFilterName(typedData: String, textInputLayout: TextInputLayout?): Boolean {
        if (typedData.trim().isNullOrBlank()) {
            textInputLayout?.showError(getString(R.string.field_empty_error_message_common))
            return false
        }
        textInputLayout?.hideError()
        return true
    }


}
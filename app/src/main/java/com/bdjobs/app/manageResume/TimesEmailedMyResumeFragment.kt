package com.bdjobs.app.manageResume


//import com.google.android.gms.ads.AdRequest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.TimesEmailed
import com.bdjobs.app.API.ModelClasses.TimesEmailedData
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.*
import kotlinx.android.synthetic.main.fragment_times_emailed_my_resume.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class TimesEmailedMyResumeFragment : Fragment() {

    private val PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false
    private var layoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null
    private var bdjobsUserSession: BdjobsUserSession? = null
    private lateinit var isActivityDate: String

    private var selectedResumeType = "1"

    private lateinit var manageCommunicator: ManageResumeCommunicator
    private lateinit var timesEmailedMyResumeAdapter: TimesEmailedMyResumeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_times_emailed_my_resume, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        manageCommunicator = requireActivity() as ManageResumeCommunicator
        bdjobsUserSession = BdjobsUserSession(requireContext())

        backIMV.setOnClickListener {
            manageCommunicator.backButtonPressed()
        }


        filter_btn.setOnClickListener {
            manageCommunicator.gotoTimesResumeFilterFrag()
        }

        newEmaiResume.setOnClickListener {
            manageCommunicator.gotoEmailResumeFragment()
        }
//        val adRequest = AdRequest.Builder().build()
//        adView?.loadAd(adRequest)
        Ads.loadAdaptiveBanner(requireContext(), adView)


        //Log.d("isActivityDate", "vava = ${Constants.timesEmailedResumeLast}")

        if (!Constants.timesEmailedResumeLast) {
            allTV.performClick()
            isActivityDate = "0"
            allOnClickAction(isActivityDate)
            initPagination()
        } else if (Constants.timesEmailedResumeLast) {
            matchedTV.performClick()
            isActivityDate = "1"
            lastOnClickAction(isActivityDate)
            initPagination()
        }


    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        textView10.text = "You didn't email any Resume yet."

        if (manageCommunicator.getBackFrom() == "filter") {
            manageCommunicator.setBackFrom("")
            cross_iv?.show()
            filter_btn?.hide()
        }

        cross_iv?.setOnClickListener {
            cross_iv?.hide()
            filter_btn?.show()
        }

        matchedTV.setOnClickListener {
            if (!Constants.timesEmailedResumeLast) {
                isActivityDate = "1"
                lastOnClickAction(isActivityDate)
                Constants.timesEmailedResumeLast = true
                matchedTV?.isEnabled = false
                allTV?.isEnabled = true
            }
            initPagination()
        }

        allTV.setOnClickListener {
            if (Constants.timesEmailedResumeLast) {
                isActivityDate = "0"
                allOnClickAction(isActivityDate)
                Constants.timesEmailedResumeLast = false
                matchedTV?.isEnabled = true
                allTV?.isEnabled = false
            }
            initPagination()
        }

        loadSpinner()

        filter_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedType = parent?.getItemAtPosition(position).toString()
                Timber.d("Selected Resume Type: $selectedType")

                when (selectedType) {
                    "All" -> selectedResumeType = "1"
                    "Bdjobs Resume" -> selectedResumeType = "2"
                    "Personalized Resume" -> selectedResumeType = "3"
                }

                initPagination()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

    }

    private fun loadSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinnerFilterItemEmailResume,
            android.R.layout.simple_spinner_dropdown_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            filter_spinner.adapter = it
        }
    }

    private fun lastOnClickAction(isActivityDate: String) {
        // lastSelected()
        lastSelected()
        loadFirstPage(isActivityDate)
        Constants.timesEmailedResumeLast = true
    }

    private fun allOnClickAction(isActivityDate: String) {
        // allSelected()
        allSelected()
        loadFirstPage(isActivityDate)
        Constants.timesEmailedResumeLast = false

    }

    private fun lastSelected() {
        matchedTV.setTextColor(Color.parseColor("#FFFFFF"))
        matchedTV.setBackgroundResource(R.drawable.success_active_bg)
        allTV.setTextColor(Color.parseColor("#424242"))
        allTV.setBackgroundResource(R.drawable.pending_inactive_bg)
//        matchedTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//        allTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//        state = 0
//        verifyStatus = "1"

    }

    private fun allSelected() {
        allTV.setTextColor(Color.parseColor("#FFFFFF"))
        allTV.setBackgroundResource(R.drawable.pending_active_bg)
        matchedTV.setTextColor(Color.parseColor("#424242"))
        matchedTV.setBackgroundResource(R.drawable.success_inactive_bg)
//        matchedTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//        allTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//        state = 1
//        verifyStatus = "0"
    }

    private fun initPagination() {
        //Log.d("timesemailedresumepgNo", "vava = ${pgNo}")
        timesEmailedMyResumeAdapter = TimesEmailedMyResumeAdapter(requireContext())
        emailedResumeRV!!.setHasFixedSize(true)
        emailedResumeRV!!.adapter = timesEmailedMyResumeAdapter
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        emailedResumeRV!!.layoutManager = layoutManager
        //Log.d("initPag", "called = ${TOTAL_PAGES}")
        emailedResumeRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        emailedResumeRV?.addOnScrollListener(object : PaginationScrollListener(layoutManager!!) {
            override val isLoading: Boolean
                get() = isLoadings
            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages

            override fun loadMoreItems() {
                isLoadings = true
                pgNo++
                loadNextPage(isActivityDate)
            }
        })
        loadFirstPage(isActivityDate)

    }

    private fun loadFirstPage(isActivityDate: String) {
        /*pgNo = 1
        TOTAL_PAGES = 0
        isLastPages = false*/

        emailedResumeRV?.hide()
        shimmer_view_container_emailedResumeList?.show()
        shimmer_view_container_emailedResumeList?.startShimmer()
        numberTV.text = "0"
        ApiServiceMyBdjobs.create().emailedMyResume(
            userID = bdjobsUserSession?.userId,
            decodeID = bdjobsUserSession?.decodId,
            pageNumber = pgNo.toString(),
            itemsPerPage = "20",
            isActivityDate = isActivityDate,
            resumeType = selectedResumeType
        ).enqueue(object : Callback<TimesEmailed> {
            override fun onFailure(call: Call<TimesEmailed>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<TimesEmailed>, response: Response<TimesEmailed>) {

                try {
                    TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                    var totalEmailRecords = response.body()?.common?.totalNumberOfEmail


                    if (totalEmailRecords?.isEmpty()!!) {
                        totalEmailRecords = "0"

                        /*  val styledText = " Time Emailed Resume "
                          titleTV?.text = styledText*/

                    }
                    if (!response.body()?.data.isNullOrEmpty()) {
                        //  emailedResumeRV!!.visibility = View.VISIBLE
                        timesEmailedMyResumeAdapter.removeAll()
                        timesEmailedMyResumeAdapter.addAll((response.body()?.data as List<TimesEmailedData>?)!!)
                        numberTV.text = totalEmailRecords

                        /* if (pgNo <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                             //Log.d("loadif", "$TOTAL_PAGES and $pgNo ")
                             timesEmailedMyResumeAdapter?.addLoadingFooter()
                         } else {
                             //Log.d("loadelse", "$TOTAL_PAGES and $pgNo ")
                             isLastPages = true
                         }*/
                        if (pgNo == TOTAL_PAGES!!) {
                            isLastPages = true
                        } else {
                            timesEmailedMyResumeAdapter.addLoadingFooter()
                        }
                    }



                    if (totalEmailRecords.toInt() > 1) {
                        val styledText = " times Emailed Resume"
                        titleTV?.text = styledText
                    } else {
                        val styledText = " time Emailed Resume "
                        titleTV?.text = styledText
                    }

                    //bLL?.visibility = View.VISIBLE

                } catch (e: Exception) {
                    logException(e)
                }

                if (response.body()?.data.isNullOrEmpty()) {
                    try {
                        if (isAdded) {
                            timesEmailedNoDataLL?.show()
                            titleTV.hide()
                            numberTV.hide()
                            emailedResumeRV?.hide()
                            shimmer_view_container_emailedResumeList?.hide()
                            shimmer_view_container_emailedResumeList?.stopShimmer()
                        }

                    } catch (e: Exception) {
                    }
                    //Log.d("totalJobs", "zero")
                } else {
                    try {
                        if (isAdded) {
                            timesEmailedNoDataLL?.hide()
                            titleTV.show()
                            numberTV.show()
                            emailedResumeRV?.show()
                            shimmer_view_container_emailedResumeList?.hide()
                            shimmer_view_container_emailedResumeList?.stopShimmer()
                        }
                    } catch (e: Exception) {
                    }
                }
            }

        })
    }

    private fun loadNextPage(isActivityDate: String) {
        ApiServiceMyBdjobs.create().emailedMyResume(
            userID = bdjobsUserSession?.userId,
            decodeID = bdjobsUserSession?.decodId,
            pageNumber = pgNo.toString(),
            itemsPerPage = "20",
            isActivityDate = isActivityDate,
            resumeType = selectedResumeType,
        ).enqueue(object : Callback<TimesEmailed> {
            override fun onFailure(call: Call<TimesEmailed>, t: Throwable) {
                error("onFailure", t)
                activity?.toast("${t.message}")
            }

            override fun onResponse(call: Call<TimesEmailed>, response: Response<TimesEmailed>) {

                try {

                    TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                    timesEmailedMyResumeAdapter.removeLoadingFooter()
                    isLoadings = false

                    timesEmailedMyResumeAdapter.addAll((response.body()?.data as List<TimesEmailedData>?)!!)
                    if (pgNo < TOTAL_PAGES!!)
                        timesEmailedMyResumeAdapter.addLoadingFooter()
                    else {
                        isLastPages = true
                    }

                } catch (e: Exception) {
                    logException(e)
                }
            }

        })

    }


}

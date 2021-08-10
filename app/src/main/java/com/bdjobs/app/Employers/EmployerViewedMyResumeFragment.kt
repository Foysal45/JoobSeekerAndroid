package com.bdjobs.app.Employers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.Fragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.DataEmpV
import com.bdjobs.app.API.ModelClasses.EmpViewedResumeModel
import com.bdjobs.app.API.ModelClasses.EmpVwdResume
import com.bdjobs.app.API.ModelClasses.EmpVwdResumeData
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
//import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_employer_viewed_my_resume.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class EmployerViewedMyResumeFragment : Fragment() {

    private var PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false
    private lateinit var employerViewedMyResumeAdapter: EmployerViewedMyResumeAdapter
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var employerCommunicator: EmployersCommunicator
    private lateinit var isActivityDate: String

    private lateinit var companyNameET: TextInputEditText

    private var selectedType = ""

    private var fromText = ""
    private var toText = ""

    private val calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_viewed_my_resume, container, false)
        //Log.d("called", "onCreateView")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        employerCommunicator = activity as EmployersCommunicator
        isActivityDate = employerCommunicator.getTime()
        if (isActivityDate == "")
            isActivityDate = "1"
        //Log.d("test", "test" + isActivityDate)
        //Log.d("called", "onActivityCreated")

        backIMV?.setOnClickListener {
            employerCommunicator.backButtonPressed()
        }


    }

    override fun onResume() {
        super.onResume()
        initializeViews()

        favCountTV.setOnClickListener {
            showFilterDialog()
        }

//        val adRequest = AdRequest.Builder().build()
//        adView?.loadAd(adRequest)
//        Ads.loadAdaptiveBanner(activity.applicationContext,adView)

        //Log.d("called", "onResume")

    }

    @SuppressLint("SetTextI18n")
    private fun showFilterDialog() {
        val dialog = Dialog(activity).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(R.layout.dialog_filter_employer_view)
        }

        val resumeTypeET = dialog.findViewById<TextInputEditText>(R.id.et_resume_type)
        companyNameET = dialog.findViewById<TextInputEditText>(R.id.et_company_name)
        val resumeTypeTIL = dialog.findViewById<TextInputLayout>(R.id.til_resume_type)
        val dropdownCard = dialog.findViewById<MaterialCardView>(R.id.dropdown_filter_options)
        val bdJobsResumeTV = dialog.findViewById<MaterialTextView>(R.id.tv_bdjobs_resume_filter)
        val personalizeResumeTV = dialog.findViewById<MaterialTextView>(R.id.tv_per_resume_filter)
        val videoResumeTV = dialog.findViewById<MaterialTextView>(R.id.tv_video_resume_filter)
        val allResumeTV = dialog.findViewById<MaterialTextView>(R.id.tv_all_resume_filter)
        val fromTIL = dialog.findViewById<TextInputLayout>(R.id.til_from)
        val fromET = dialog.findViewById<TextInputEditText>(R.id.et_from_filter)
        val toET = dialog.findViewById<TextInputEditText>(R.id.et_to_filter)
        val toTIL = dialog.findViewById<TextInputLayout>(R.id.til_to)

        val applyBtn = dialog.findViewById<MaterialButton>(R.id.btn_applied)
        val cancelBtn = dialog.findViewById<MaterialButton>(R.id.btn_cancel)

        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        selectedType = "1"
        fromText = ""
        toText = ""

//        fromET.setText(sdf.format(calendar.time))
//        toET.setText(sdf.format(calendar.time))

        bdJobsResumeTV.setOnClickListener {
            selectedType = "1"
            resumeTypeET.setText("Bdjobs Resume")
            dropdownCard.hide()
        }

        personalizeResumeTV.setOnClickListener {
            selectedType = "2"
            resumeTypeET.setText("Personalized Resume")
            dropdownCard.hide()
        }

        videoResumeTV.setOnClickListener {
            selectedType = "3"
            resumeTypeET.setText("Video Resume")
            dropdownCard.hide()
        }

        allResumeTV.setOnClickListener {
            selectedType = ""
            resumeTypeET.setText("All")
            dropdownCard.hide()
        }

        resumeTypeET.setOnClickListener { dropdownCard.visibility = View.VISIBLE }
        cancelBtn.setOnClickListener { dialog.dismiss() }

        fromET.setOnClickListener {
            showDatePicker(fromET,"from")
        }

        toET.setOnClickListener {
            showDatePicker(toET,"to")
        }

        applyBtn.setOnClickListener {
            initializeViews()
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePicker(fromET: TextInputEditText,type:String) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(activity, { _, y, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            val dd = if (dayOfMonth<10) "0$dayOfMonth" else "$dayOfMonth"
            val MM = if (monthOfYear+1 <10) "0${monthOfYear + 1}" else "${monthOfYear+1}"
            fromET.setText("$dd/$MM/$y")

            if (type=="from") {
                val myFormat = "MM/dd/yyyy"
                val inputFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val inputSdf = SimpleDateFormat(inputFormat, Locale.US)

                Timber.d("FromText: ${fromET.text.toString()}")

                val inputDate = inputSdf.parse(fromET.text.toString())

                fromText = sdf.format(inputDate!!)
            } else {
                val myFormat = "MM/dd/yyyy"
                val inputFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val inputSdf = SimpleDateFormat(inputFormat, Locale.US)

                val inputDate = inputSdf.parse(fromET.text.toString())

                toText = sdf.format(inputDate!!)
            }

        }, year, month, day)

        dpd.datePicker.maxDate = calendar.timeInMillis
        dpd.show()
    }

    private fun initializeViews() {
        bdjobsUserSession = BdjobsUserSession(activity)
        employerViewedMyResumeAdapter = EmployerViewedMyResumeAdapter(activity)
        viewedMyResumeRV!!.adapter = employerViewedMyResumeAdapter
        viewedMyResumeRV!!.setHasFixedSize(true)
        viewedMyResumeRV?.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        //Log.d("initPag", "called")
        viewedMyResumeRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        viewedMyResumeRV?.addOnScrollListener(object :
            PaginationScrollListener((viewedMyResumeRV.layoutManager as LinearLayoutManager?)!!) {
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

    private fun loadFirstPage(activityDate: String) {
        PAGE_START = 1
        TOTAL_PAGES = null
        pgNo = PAGE_START
        isLastPages = false
        isLoadings = false

        try {
            viewedMyResumeRV?.hide()
            favCountTV?.hide()
            shimmer_view_container_employerViewedMyList?.show()
            shimmer_view_container_employerViewedMyList?.startShimmer()


//            ApiServiceMyBdjobs.create().getEmpVwdMyResume(
//                userId = bdjobsUserSession.userId,
//                decodeId = bdjobsUserSession.decodId,
//                pageNumber = pgNo.toString(),
//                itemsPerPage = "10",
//                isActivityDate = activityDate,
//                AppsDate = "1"
//
//                /*    userId = "241028",
//                    decodeId = "T8B8Rx",
//                    pageNumber = "1",
//                    itemsPerPage = "10",
//                    isActivityDate = "1",
//                    AppsDate = ""*/
//
//            )
               ApiServiceMyBdjobs.create().getEmployerViewedResume(
                   userId = bdjobsUserSession.userId,
                   decodeId = bdjobsUserSession.decodId,
                   pageNumber = pgNo.toString(),
                   itemsPerPage = "30",
                   fromDate = fromText,
                   toDate = toText,
                   txtStatus = selectedType,
                   compName = companyNameET.text?.toString(),
               ) .enqueue(object : Callback<EmpViewedResumeModel> {
                override fun onFailure(call: Call<EmpViewedResumeModel>, t: Throwable) {
                    try {
                        t.printStackTrace()
                        activity?.toast("${t.message}")
                        Timber.e("Exception: ${t.localizedMessage}")
                        shimmer_view_container_employerViewedMyList?.hide()
                        shimmer_view_container_employerViewedMyList?.stopShimmer()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
//                userId=241028&decodeId=T8B8Rx&pageNumber=1&itemsPerPage=10&isActivityDate=1&AppsDate=1&appId=1
//                userId=241028&decodeId=T8B8Rx&pageNumber=1&itemsPerPage=10&isActivityDate=&AppsDate=1&appId=1

                override fun onResponse(
                    call: Call<EmpViewedResumeModel>,
                    response: Response<EmpViewedResumeModel>
                ) {
                    shimmer_view_container_employerViewedMyList?.hide()
                    shimmer_view_container_employerViewedMyList?.stopShimmer()


                    try {

                        TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                        val totalRecords = response.body()?.common?.totalNumberOfItems
                        if (!response?.body()?.data.isNullOrEmpty()) {
                            resumeViewNoDataLL?.hide()
                            viewedMyResumeRV?.show()
                            ////Log.d("oooooo", "url: ${response?.body()?.data} and ")

                            val value = response.body()?.data
                            employerViewedMyResumeAdapter?.removeAll()
                            employerViewedMyResumeAdapter?.addAll(response?.body()?.data as List<DataEmpV>)

                            if (pgNo <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                                //Log.d("loadif", "$TOTAL_PAGES and $pgNo ")
                                employerViewedMyResumeAdapter?.addLoadingFooter()
                            } else {
                                //Log.d("loadelse", "$TOTAL_PAGES and $pgNo ")
                                isLastPages = true
                            }

                            //Log.d("totalJobs", "totalRecords $totalRecords")


                            if (totalRecords?.toInt()!! > 1) {
                                val styledText =
                                    "<b><font color='#13A10E'>$totalRecords</font></b> Employers viewed my Resume"
                                favCountTV?.text = Html.fromHtml(styledText)
                            } else if (totalRecords?.toInt()!! <= 1 || (totalRecords.toInt()!! == null)) {
                                val styledText =
                                    "<b><font color='#13A10E'>$totalRecords</font></b> Employer viewed my Resume"
                                favCountTV?.text = Html.fromHtml(styledText)
                            }


                        } else {


                            resumeViewNoDataLL?.show()
                            viewedMyResumeRV?.hide()
                            //Log.d("totalJobs", "zero")


                        }

                        /*   viewedMyResumeRV?.show()*/
                        favCountTV?.show()
                        shimmer_view_container_employerViewedMyList?.hide()
                        shimmer_view_container_employerViewedMyList?.stopShimmer()

                    } catch (exception: Exception) {
                        //Log.d("issue", exception.toString())

                    }
                }

            })
        } catch (e: Exception) {
            logException(e)
        }


    }

    private fun loadNextPage(activityDate: String) {
        try {
//            ApiServiceMyBdjobs.create().getEmpVwdMyResume(
//                userId = bdjobsUserSession.userId,
//                decodeId = bdjobsUserSession.decodId,
//                pageNumber = pgNo.toString(),
//                itemsPerPage = "10",
//                isActivityDate = activityDate,
//                AppsDate = ""
//
//
//            )
            ApiServiceMyBdjobs.create().getEmployerViewedResume(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId,
                pageNumber = pgNo.toString(),
                itemsPerPage = "30",
                fromDate = fromText,
                toDate = toText,
                txtStatus = selectedType
            ).enqueue(object : Callback<EmpViewedResumeModel> {
                override fun onFailure(call: Call<EmpViewedResumeModel>, t: Throwable) {
                    try {
                        activity?.toast("${t.message}")
                        error("onFailure", t)
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                override fun onResponse(
                    call: Call<EmpViewedResumeModel>,
                    response: Response<EmpViewedResumeModel>
                ) {

                    try {
                        TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                        employerViewedMyResumeAdapter?.removeLoadingFooter()
                        isLoadings = false

                        employerViewedMyResumeAdapter?.addAll(response?.body()?.data as List<DataEmpV>)


                        if (pgNo != TOTAL_PAGES)
                            employerViewedMyResumeAdapter?.addLoadingFooter()
                        else {
                            isLastPages = true
                        }

                    } catch (e: Exception) {
                        logException(e)
                    }
                }


            })
        } catch (e: Exception) {
            logException(e)
        }


    }

}

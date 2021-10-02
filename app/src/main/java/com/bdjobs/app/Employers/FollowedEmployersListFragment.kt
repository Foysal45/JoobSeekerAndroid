package com.bdjobs.app.Employers

//import com.google.android.gms.ads.AdRequest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.FollowEmployerListData
import com.bdjobs.app.API.ModelClasses.FollowEmployerListModelClass
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.sms.BaseActivity
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_followed_employers.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class FollowedEmployersListFragment : Fragment(),FollowedEmployersAdapter.OnUpdateCounter {
    private lateinit var bdJobsDB: BdjobsDB
    private var followedEmployersAdapter: FollowedEmployersAdapter? = null
    lateinit var employersCommunicator: EmployersCommunicator
    private lateinit var isActivityDate: String
    var followedListSize = 0
    private var followedEmployerList: List<FollowEmployerListData>? = null
    private var currentPage = 1
    private var totalPages: Int? = 1
    private var isLoadings = false
    private var isLastPages = false
    private lateinit var bdJobsUserSession: BdjobsUserSession


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_followed_employers, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        employersCommunicator = activity as EmployersCommunicator
        isActivityDate = employersCommunicator.getTime()
        bdJobsDB = BdjobsDB.getInstance(requireContext())
        bdJobsUserSession = BdjobsUserSession(requireContext())

//        val adRequest = AdRequest.Builder().build()
//        adView?.loadAd(adRequest)
//        Ads.loadAdaptiveBanner(activity.applicationContext,adView)


        backIMV?.setOnClickListener {
            employersCommunicator.backButtonPressed()
        }



        btn_sms_settings?.setOnClickListener {
            requireContext().startActivity<BaseActivity>("from" to "employer")
        }

        btn_job_list.setOnClickListener {
            requireContext().startActivity(
                Intent(
                    requireContext(),
                    EmployersBaseActivity::class.java
                ).putExtra("from", "employer")
            )
        }

        btn_sms_alert_fab.setOnClickListener {
            openSmsAlertDialog()
        }

    }

    override fun onResume() {
        super.onResume()

        try {
            followedEmployersAdapter = FollowedEmployersAdapter(requireContext(),this)
            followedRV?.adapter = followedEmployersAdapter
            followedRV?.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            followedRV?.layoutManager = layoutManager
            followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

            if (employersCommunicator.getFollowedEmployerList().isNullOrEmpty()) {
                shimmer_view_container_JobList?.show()
                shimmer_view_container_JobList?.startShimmer()
                loadData(1)
            } else {
                try {
                    followedEmployersAdapter?.removeAll()
                    followedEmployersAdapter?.addAll(employersCommunicator.getFollowedEmployerList()!!)
                    currentPage = employersCommunicator.getCurrentPage()!!
                    totalPages = employersCommunicator.getTotalPage()!!
                    isLoadings = employersCommunicator.getIsloading()!!
                    isLastPages = employersCommunicator.getIsLastPage()!!
                    followedListSize = employersCommunicator.getFollowedListSize()!!
                } catch (e: Exception) {
                    logException(e)
                }
                try {
                    if (followedListSize > 1) {
                        val styledText = "<b><font color='#13A10E'>${followedListSize}</font></b> Followed Employers"
                        favCountTV?.text = Html.fromHtml(styledText)
                    } else {
                        val styledText = "<b><font color='#13A10E'>${followedListSize}</font></b> Followed Employer"
                        favCountTV?.text = Html.fromHtml(styledText)
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }


            followedRV?.addOnScrollListener(object : PaginationScrollListener((followedRV.layoutManager as LinearLayoutManager?)!!) {

                override val totalPageCount: Int
                    get() = totalPages!!
                override val isLastPage: Boolean
                    get() = isLastPages
                override val isLoading: Boolean
                    get() = isLoadings


                override fun loadMoreItems() {
                    isLoadings = true
                    currentPage += 1
                    //loadData(currentPage);

                    loadNextPage()
                }
            })


        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun loadNextPage() {

        if (currentPage <= totalPages?:0){
            try {
                ApiServiceJobs.create().getFollowEmployerListLazy(
                        pg = currentPage.toString(),
                        isActivityDate = isActivityDate,
                        userID = bdJobsUserSession.userId,
                        decodeId = bdJobsUserSession.decodId,
                        encoded = Constants.ENCODED_JOBS


                ).enqueue(object : Callback<FollowEmployerListModelClass> {
                    override fun onFailure(call: Call<FollowEmployerListModelClass>, t: Throwable) {
                        //Log.d("getFEmployerListLazy", t.message)
                    }

                    override fun onResponse(call: Call<FollowEmployerListModelClass>, response: Response<FollowEmployerListModelClass>) {

                        try {
                            totalPages = response.body()?.common?.totalpages?.toInt()
                            followedEmployersAdapter?.removeLoadingFooter()
                            isLoadings = false
                            followedEmployersAdapter?.addAll(response.body()?.data as List<FollowEmployerListData>)
                            if (currentPage != totalPages)
                                followedEmployersAdapter?.addLoadingFooter()
                            else
                                isLastPages = true
                        } catch (e: java.lang.Exception) {
                            logException(e)
                        }

                    }

                })

            } catch (e : java.lang.Exception){
                logException(e)
            }
        }

    }

    private fun loadData(currentPage: Int) {

        shimmer_view_container_JobList.show()
        shimmer_view_container_JobList.startShimmer()
        cl_total_count.hide()
        followedRV?.hide()
        followEmployerNoDataLL?.hide()

        ApiServiceJobs.create().getFollowEmployerListLazy(
                pg = currentPage.toString(),
                isActivityDate = isActivityDate,
                userID = bdJobsUserSession.userId,
                decodeId = bdJobsUserSession.decodId,
                encoded = Constants.ENCODED_JOBS


        ).enqueue(object : Callback<FollowEmployerListModelClass> {
            override fun onFailure(call: Call<FollowEmployerListModelClass>, t: Throwable) {
                shimmer_view_container_JobList?.hide()
                shimmer_view_container_JobList?.stopShimmer()
                Toast.makeText(
                    requireContext(),
                    "Something went wrong! Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<FollowEmployerListModelClass>, response: Response<FollowEmployerListModelClass>) {

                try {

                    shimmer_view_container_JobList?.hide()
                    shimmer_view_container_JobList?.stopShimmer()

                    cl_total_count.show()
                    followedEmployerList = response.body()?.data

                    if (followedEmployerList != null && followedEmployerList!!.isNotEmpty()) {
                        followEmployerNoDataLL?.hide()
                        followedRV?.show()

                        followedListSize = response.body()?.common?.totalRecordsFound?.toInt()!!
                        followedEmployersAdapter?.addAll(followedEmployerList!!)

                        employersCommunicator.setTotalFollowedEmployersCount(response.body()?.common?.totalRecordsFound?.toInt()!!)


                        totalPages = response.body()?.common?.totalpages?.toInt()
                        if (currentPage <= totalPages!! && totalPages!! > 1) {
                            followedEmployersAdapter?.addLoadingFooter()
                        } else {
                            isLastPages = true
                        }
                    } else {
                        followedRV?.hide()
                        followEmployerNoDataLL?.show()
                    }

                } catch (e: Exception) {
                    shimmer_view_container_JobList?.hide()
                    shimmer_view_container_JobList?.stopShimmer()
                    followedRV?.hide()
                    followEmployerNoDataLL?.hide()
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong! Please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                    logException(e)
                }

                /*try {
                    if (followedEmployerList?.size!! > 0) {
                        followEmployerNoDataLL?.hide()
                        followedRV?.show()
                        //Log.d("totalJobs", "data ase")
                    } else {
                        followEmployerNoDataLL?.show()
                        followedRV?.hide()
                        //Log.d("totalJobs", "zero")
                    }
                } catch (e: Exception) {
                    logException(e)

                }*/

                try {
                    if (followedListSize > 1) {
                        val styledText = "<b><font color='#13A10E'>${followedListSize}</font></b> Followed Employers"
                        favCountTV?.text = Html.fromHtml(styledText)
                    } else {
                        val styledText = "<b><font color='#13A10E'>${followedListSize}</font></b> Followed Employer"
                        favCountTV?.text = Html.fromHtml(styledText)
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }

        })

    }

    fun scrollToUndoPosition(position: Int) {
        followedRV?.scrollToPosition(position)
        followedListSize++
        if (followedListSize > 1) {
            val styledText = "<b><font color='#13A10E'>$followedListSize</font></b> Followed Employers"
            favCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$followedListSize</font></b> Followed Employer"
            favCountTV?.text = Html.fromHtml(styledText)
        }

    }

    fun decrementCounter(position: Int) {
        followedListSize--
        run {
            followedRV?.scrollToPosition(position)
        }
        if (followedListSize > 1) {
            val styledText = "<b><font color='#13A10E'>$followedListSize</font></b> Followed Employers"
            favCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$followedListSize</font></b> Followed Employer"
            favCountTV?.text = Html.fromHtml(styledText)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openSmsAlertDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater = requireContext().layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_sms_alert, null))
        builder.create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            findViewById<ImageView>(R.id.img_close).setOnClickListener {
                this.cancel()
            }
            findViewById<MaterialButton>(R.id.btn_purchase).setOnClickListener {
                requireContext().startActivity(Intent(requireContext(), BaseActivity::class.java))
                this.cancel()
            }
            findViewById<MaterialButton>(R.id.btn_sms_settings).setOnClickListener {
                requireContext().startActivity<BaseActivity>("from" to "employer")
                this.cancel()
            }
            findViewById<TextView>(R.id.tv_body).text =
                "Buy SMS to get job alert from subscribed Followed Employers"
        }
    }


    override fun onPause() {
        super.onPause()
        try {
            employersCommunicator.setFollowedListSize(followedListSize)
            employersCommunicator.setCurrentPage(currentPage)
            employersCommunicator.setTotalPage(totalPages)
            employersCommunicator.setIsloading(isLoadings)
            employersCommunicator.setIsLastPage(isLastPages)
        } catch (e: Exception) {
            logException(e)
        }

    }

    override fun update(count: Int) {
        Timber.d("Followed emp count: $count")
        employersCommunicator.setTotalFollowedEmployersCount(count)
        if (count > 0) {
            val styledText = "<b><font color='#13A10E'>$count</font></b> Followed Employers"
            favCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$count</font></b> Followed Employer"
            favCountTV?.text = Html.fromHtml(styledText)
            followedRV?.hide()
            followEmployerNoDataLL?.show()
        }
    }


}

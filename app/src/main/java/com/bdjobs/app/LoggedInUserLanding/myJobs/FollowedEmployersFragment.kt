package com.bdjobs.app.LoggedInUserLanding.myJobs

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
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.Employers.FollowedEmployersAdapter
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.LoggedInUserLanding.HomeCommunicator
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.sms.SmsBaseActivity
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_followed_employers.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class FollowedEmployersFragment : Fragment(), FollowedEmployersAdapter.OnUpdateCounter {

    private lateinit var bdJobsDB: BdjobsDB
    private var followedEmployersAdapter: FollowedEmployersAdapter? = null
    private lateinit var isActivityDate: String
    var followedListSize = 0
    private var followedEmployerList: List<FollowEmployerListData>? = null
    private var currentPage = 1
    private var totalPage: Int? = 1
    private var isLoadings = false
    private var isLastPages = false
    private lateinit var bdJobsUserSession: BdjobsUserSession
    lateinit var homeCommunicator: HomeCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_followed_employers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdJobsDB = BdjobsDB.getInstance(requireContext())
        bdJobsUserSession = BdjobsUserSession(requireContext())
        homeCommunicator = requireActivity() as HomeCommunicator
        isActivityDate = homeCommunicator.getTime()

        /*try {

            toolbar2.visibility = View.GONE


            followedRV?.addOnScrollListener(object : PaginationScrollListener((followedRV.layoutManager as LinearLayoutManager?)!!) {

                override val totalPageCount: Int get() = totalPage!!
                override val isLastPage: Boolean get() = isLastPages
                override val isLoading: Boolean get() = isLoadings
                override fun loadMoreItems() {
                    isLoadings = true
                    currentPage += 1

                    loadNextPage()
                }
            })


        } catch (e: Exception) {
            logException(e)
            Timber.e("Exception : ${e.localizedMessage}")
        }*/

        toolbar2.visibility = View.GONE

        btn_sms_settings?.setOnClickListener {
            startActivity<SmsBaseActivity>("from" to "employer")
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
            followedEmployersAdapter = FollowedEmployersAdapter(requireContext(), this)
            followedRV?.adapter = followedEmployersAdapter
            followedRV?.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            followedRV?.layoutManager = layoutManager
            followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

            followedRV?.addOnScrollListener(object : PaginationScrollListener((followedRV.layoutManager as LinearLayoutManager?)!!) {

                override val totalPageCount: Int get() = totalPage!!
                override val isLastPage: Boolean get() = isLastPages
                override val isLoading: Boolean get() = isLoadings
                override fun loadMoreItems() {
                    isLoadings = true
                    currentPage += 1

                    loadNextPage()
                }
            })


            loadData(1)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Something went wrong! Please try again later",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadNextPage() {
        if (currentPage <= totalPage ?: 0) {
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

                    override fun onResponse(
                        call: Call<FollowEmployerListModelClass>,
                        response: Response<FollowEmployerListModelClass>
                    ) {

                        try {
                            totalPage = response.body()?.common?.totalpages?.toInt()
                            followedEmployersAdapter?.removeLoadingFooter()
                            isLoadings = false
                            followedEmployersAdapter?.addAll(response.body()?.data as List<FollowEmployerListData>)
                            if (currentPage != totalPage)
                                followedEmployersAdapter?.addLoadingFooter()
                            else
                                isLastPages = true
                        } catch (e: java.lang.Exception) {
                            logException(e)
                        }

                    }

                })

            } catch (e: java.lang.Exception) {
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
                Timber.e("onFailure: ${t.localizedMessage}")
                //Log.d("getFEmployerListLazy", t.message)
                if (isAdded) {
                    shimmer_view_container_JobList?.hide()
                    shimmer_view_container_JobList?.stopShimmer()
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong! Please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onResponse(
                call: Call<FollowEmployerListModelClass>,
                response: Response<FollowEmployerListModelClass>
            ) {

                try {

                    shimmer_view_container_JobList?.hide()
                    shimmer_view_container_JobList?.stopShimmer()

                    cl_total_count.show()

                    followedEmployerList = response.body()?.data

                    if (followedEmployerList != null && followedEmployerList!!.isNotEmpty()) {


                        followEmployerNoDataLL?.hide()
                        followedRV?.show()
                        btn_sms_alert_fab?.show()

                        followedListSize = response.body()?.common?.totalRecordsFound?.toInt()!!
                        followedEmployersAdapter?.addAll(followedEmployerList!!)

                        homeCommunicator.setTotalFollowedEmployersCount(response.body()?.common?.totalRecordsFound?.toInt()!!)

                        totalPage = response.body()?.common?.totalpages?.toInt()
                        if (currentPage <= totalPage!! && totalPage!! > 1) {
                            followedEmployersAdapter?.addLoadingFooter()
                        } else {
                            isLastPages = true
                        }
                    } else {
                        // no followed employers
                        followedRV?.hide()
                        cl_total_count.hide()
                        followEmployerNoDataLL?.show()
                        btn_sms_alert_fab?.hide()
                    }


                } catch (e: Exception) {
                    if (isAdded) {
                        Timber.e("Exception: ${e.localizedMessage}")
                        shimmer_view_container_JobList?.hide()
                        shimmer_view_container_JobList?.stopShimmer()
                        followedRV?.hide()
                        cl_total_count.hide()
                        followEmployerNoDataLL?.hide()

                        Toast.makeText(
                            requireContext(),
                            "Something went wrong! Please try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    logException(e)
                }

                try {
                    if (followedListSize > 1) {
                        val styledText =
                            "<b><font color='#13A10E'>${followedListSize}</font></b> Followed Employers"
                        favCountTV?.text = Html.fromHtml(styledText)
                    } else {
                        val styledText =
                            "<b><font color='#13A10E'>${followedListSize}</font></b> Followed Employer"
                        favCountTV?.text = Html.fromHtml(styledText)
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }

        })

    }

    override fun update(count: Int) {
        Timber.d("Followed emp count: $count")
        homeCommunicator.setTotalFollowedEmployersCount(count)
        if (count > 0) {
            val styledText = "<b><font color='#13A10E'>$count</font></b> Followed Employers"
            favCountTV?.text = Html.fromHtml(styledText)
            btn_sms_alert_fab?.show()
            cl_total_count.show()
        } else {
            val styledText = "<b><font color='#13A10E'>$count</font></b> Followed Employer"
            favCountTV?.text = Html.fromHtml(styledText)
            followedRV?.hide()
            followEmployerNoDataLL?.show()
            cl_total_count.hide()
            btn_sms_alert_fab?.hide()
        }
    }

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
                requireContext().startActivity(
                    Intent(
                        requireContext(),
                        SmsBaseActivity::class.java
                    )
                )
                this.cancel()
            }
            findViewById<MaterialButton>(R.id.btn_sms_settings).setOnClickListener {
                requireContext().startActivity<SmsBaseActivity>("from" to "employer")
                this.cancel()
            }
            findViewById<TextView>(R.id.tv_body).text =
                "Buy SMS to get job alert from subscribed Followed Employers"
        }
    }


}
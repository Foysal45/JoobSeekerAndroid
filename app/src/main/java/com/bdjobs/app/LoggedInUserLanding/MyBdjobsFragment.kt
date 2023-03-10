package com.bdjobs.app.LoggedInUserLanding

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MybdjobsData
import com.bdjobs.app.API.ModelClasses.StatsModelClassData
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.ajkerDeal.ui.home.page_home.HomeNewFragment
import com.bdjobs.app.utilities.*
import com.bdjobs.app.databases.internal.BdjobsDB
import kotlinx.android.synthetic.main.fragment_mybdjobs_layout.*
import org.jetbrains.anko.doAsync
import timber.log.Timber

class MyBdjobsFragment : Fragment() {
    private lateinit var bdjobsUserSession: BdjobsUserSession

    private lateinit var bdjobsDB: BdjobsDB
    private var myBdJobsAdapter: MyBdJobsAdapter? = null
    private var bdjobsList: ArrayList<MybdjobsData> = ArrayList()
    private lateinit var communicator: HomeCommunicator
    private var lastMonthStatsData: List<StatsModelClassData?>? = null
    private var allStatsData: List<StatsModelClassData?>? = null
    val background_resources = intArrayOf(
        R.drawable.online_application,
        R.drawable.times_emailed,
        R.drawable.viewed_resume,
        R.drawable.employer_followed,
        R.drawable.interview_invitation,
        R.drawable.message_employers,
        R.drawable.video_interview,
        R.drawable.live_interview
    )
    val icon_resources = intArrayOf(
        R.drawable.ic_applied_jobs_new,
        R.drawable.ic_times_emailed_my_resume,
        R.drawable.ic_view_resum,
        R.drawable.ic_followed_emp_new,
        R.drawable.ic_general_interview_new,
        R.drawable.ic_emp_message_new,
        R.drawable.ic_video_camera_gray,
        R.drawable.ic_live_interview_new
    )
    private lateinit var session: BdjobsUserSession
    private fun populateDataModel() {
        try {
            myBdJobsAdapter?.removeAll()
            bdjobsList.clear()
            myBdJobsAdapter?.notifyDataSetChanged()
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_jobs_applied_lastmonth!!,
                    Constants.session_key_mybdjobscount_jobs_applied,
                    background_resources[0],
                    icon_resources[0]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_times_emailed_resume_lastmonth!!,
                    Constants.session_key_mybdjobscount_times_emailed_resume,
                    background_resources[1],
                    icon_resources[1]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_employers_viwed_resume_lastmonth!!,
                    Constants.session_key_mybdjobscount_employers_viwed_resume,
                    background_resources[2],
                    icon_resources[2]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_employers_followed_lastmonth!!,
                    Constants.session_key_mybdjobscount_employers_followed,
                    background_resources[3],
                    icon_resources[3]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_interview_invitation_lastmonth!!,
                    Constants.session_key_mybdjobscount_interview_invitation,
                    background_resources[4],
                    icon_resources[4]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_message_by_employers_lastmonth!!,
                    Constants.session_key_mybdjobscount_message_by_employers,
                    background_resources[5],
                    icon_resources[5]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_video_invitation_lastmonth!!,
                    Constants.session_key_mybdjobscount_video_invitation,
                    background_resources[6],
                    icon_resources[6]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_live_invitation_lastmonth!!,
                    Constants.session_key_mybdjobscount_live_invitation,
                    background_resources[7],
                    icon_resources[7]
                )
            )

            myBdJobsAdapter?.addAll(bdjobsList)
        } catch (e: Exception) {
        }
    }

    private fun populateDataModelALL() {
        try {
            myBdJobsAdapter?.removeAll()
            bdjobsList.clear()
            myBdJobsAdapter?.notifyDataSetChanged()
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_jobs_applied_alltime!!,
                    Constants.session_key_mybdjobscount_jobs_applied,
                    background_resources[0],
                    icon_resources[0]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_times_emailed_resume_alltime!!,
                    Constants.session_key_mybdjobscount_times_emailed_resume,
                    background_resources[1],
                    icon_resources[1]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_employers_viwed_resume_alltime!!,
                    Constants.session_key_mybdjobscount_employers_viwed_resume,
                    background_resources[2],
                    icon_resources[2]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_employers_followed_alltime!!,
                    Constants.session_key_mybdjobscount_employers_followed,
                    background_resources[3],
                    icon_resources[3]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_interview_invitation_alltime!!,
                    Constants.session_key_mybdjobscount_interview_invitation,
                    background_resources[4],
                    icon_resources[4]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_message_by_employers_alltime!!,
                    Constants.session_key_mybdjobscount_message_by_employers,
                    background_resources[5],
                    icon_resources[5]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_video_invitation_alltime!!,
                    Constants.session_key_mybdjobscount_video_invitation,
                    background_resources[6],
                    icon_resources[6]
                )
            )
            bdjobsList.add(
                MybdjobsData(
                    session.mybdjobscount_live_invitation_alltime!!,
                    Constants.session_key_mybdjobscount_live_invitation,
                    background_resources[7],
                    icon_resources[7]
                )
            )

            myBdJobsAdapter?.addAll(bdjobsList)
        } catch (e: Exception) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mybdjobs_layout, container, false)!!

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator = requireActivity() as HomeCommunicator
        bdjobsUserSession = BdjobsUserSession(requireContext())
        bdjobsDB = BdjobsDB.getInstance(requireContext())

        try {
            if (bdjobsUserSession.adTypeMyBdJobs == "2") {
                try {
                    navHostFragmentADMB.visibility = View.VISIBLE
                    transaction(HomeNewFragment(),R.id.navHostFragmentADMB,false)
                } catch (e: Exception) {
                    navHostFragmentADMB.visibility = View.GONE
                }
            } else {
                navHostFragmentADMB.visibility = View.GONE
            }
        } catch (e: Exception) {
        }

    }

    override fun onResume() {
        super.onResume()
        session = BdjobsUserSession(requireContext())
        initializeViews()
        onClick()

        showNotificationCount()
        showMessageCount()
    }

    private fun showNotificationCount() {
        try {
            bdjobsUserSession = BdjobsUserSession(requireContext())
            if (bdjobsUserSession.notificationCount!! <= 0) {
                notificationCountTV?.hide()
            } else {
                notificationCountTV?.show()
                if (bdjobsUserSession.notificationCount!! > 99) {
                    notificationCountTV?.text = "99+"

                } else {
                    notificationCountTV?.text = "${bdjobsUserSession.notificationCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    fun updateMessageView(count: Int?) {
        //Log.d("rakib", "in home fragment $count")
        if (count!! > 0) {
            messageCountTV?.show()
            if (count <= 99)
                messageCountTV?.text = "$count"
            else
                messageCountTV?.text = "99+"
        } else {
            messageCountTV?.hide()
        }
    }

    private fun showMessageCount() {
        try {

            doAsync {
                bdjobsUserSession = BdjobsUserSession(requireContext())
                val count = bdjobsDB.notificationDao().getMessageCount()
                Timber.d("Messages count: $count")
                bdjobsUserSession.updateMessageCount(count)
            }

            if (bdjobsUserSession.messageCount!! <= 0) {
                messageCountTV?.hide()
            } else {
                messageCountTV?.show()
                if (bdjobsUserSession.messageCount!! > 99) {
                    messageCountTV?.text = "99+"

                } else {
                    messageCountTV?.text = "${bdjobsUserSession.messageCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }


    private fun initializeViews() {
        myBdJobsAdapter = MyBdJobsAdapter(requireContext())
        myBdjobsgridView_RV?.adapter = myBdJobsAdapter
        myBdjobsgridView_RV?.setHasFixedSize(true)
        myBdjobsgridView_RV?.isNestedScrollingEnabled = false
        myBdjobsgridView_RV?.layoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        myBdjobsgridView_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        populateDataModel()
    }

    private fun onClick() {
        searchIMGV.setOnClickListener {
            communicator.gotoJobSearch()
        }

        profilePicIMGV.setOnClickListener {
            communicator.gotoEditresume()
        }

        notificationIMGV?.setOnClickListener {
            communicator.goToNotifications()
        }

        messageIMGV?.setOnClickListener {
            communicator.goToMessages()
        }

        profilePicIMGV?.loadCircularImageFromUrl(BdjobsUserSession(requireContext()).userPicUrl?.trim())

        lastmonth_MBTN?.setOnClickListener {
            getStatsData(1.toString())
            populateDataModel()
            communicator.setTime("1")
            Constants.myBdjobsStatsLastMonth = true
            Constants.timesEmailedResumeLast = true
            lastmonth_MBTN?.setBackgroundResource(R.drawable.left_rounded_background_black)
            all_MBTN?.setBackgroundResource(R.drawable.right_rounded_background)
            all_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
            lastmonth_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
            //  populateDataLastMonthStats()
            lastmonth_MBTN?.isEnabled = false
            all_MBTN?.isEnabled = true

        }
        all_MBTN?.setOnClickListener {
            getStatsData(0.toString())
            populateDataModelALL()
            communicator.setTime("0")
            Constants.myBdjobsStatsLastMonth = false
            Constants.timesEmailedResumeLast = false
            lastmonth_MBTN?.setBackgroundResource(R.drawable.left_rounded_background)
            all_MBTN?.setBackgroundResource(R.drawable.right_rounded_background_black)
            lastmonth_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
            all_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
            //  populateDataAllMonthStats()
            all_MBTN?.isEnabled = false
            lastmonth_MBTN?.isEnabled = true
        }

        nextButtonFAB?.setOnClickListener {
            communicator.goToResumeManager()
        }

        //Log.d("sagor", "sagor= " + Constants.myBdjobsStatsLastMonth)
        if (Constants.myBdjobsStatsLastMonth) {
            lastmonth_MBTN?.performClick()
        } else if (!Constants.myBdjobsStatsLastMonth) {
            all_MBTN?.performClick()
        }
    }

    private fun populateDataLastMonthStats() {
        try {
            for ((index, value) in lastMonthStatsData!!.withIndex()) {
                if (index < (lastMonthStatsData?.size!! - 1)) {
                    bdjobsList.add(
                        MybdjobsData(
                            value?.count!!,
                            value.title!!,
                            background_resources[index],
                            icon_resources[index]
                        )
                    )
                    //Log.d("vvuu", "${bdjobsList?.get(index)?.itemID} = ${value?.count!!}")
                }
            }
            myBdJobsAdapter?.addAll(bdjobsList)
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun populateDataAllMonthStats() {
        try {
            for ((index, value) in allStatsData!!.withIndex()) {
                if (index < (allStatsData?.size!! - 1)) {
                    bdjobsList.add(
                        MybdjobsData(
                            value?.count!!,
                            value.title!!,
                            background_resources[index],
                            icon_resources[index]
                        )
                    )
                }
            }
            myBdJobsAdapter?.addAll(bdjobsList)
        } catch (e: Exception) {
            logException(e)
        }
    }


    private fun getStatsData(activityDate: String) {
        //requireContext().showProgressBar(mybdjobsLoadingProgressBar)

        /*  mybdjobsLoadingProgressBar?.show()

          ApiServiceMyBdjobs.create().mybdjobStats(
                  userId = session.userId,
                  decodeId = session.decodId,
                  isActivityDate = activityDate,
                  trainingId = session.trainingId,
                  isResumeUpdate = session.IsResumeUpdate

          ).enqueue(object : Callback<StatsModelClass> {
              override fun onFailure(call: Call<StatsModelClass>, t: Throwable) {
                  try {
                      error("onFailure", t)
                      //requireContext()?.stopProgressBar(mybdjobsLoadingProgressBar)
                      mybdjobsLoadingProgressBar?.hide()
                      requireContext()?.toast(R.string.message_common_error)
                  } catch (e: Exception) {
                      logException(e)
                  }
              }

              override fun onResponse(call: Call<StatsModelClass>, response: Response<StatsModelClass>) {
                  // requireContext()?.stopProgressBar(mybdjobsLoadingProgressBar)
                  mybdjobsLoadingProgressBar?.hide()
                  try {
                      if (activityDate == "0") {
                          allStatsData = response.body()?.data
                          //populateDataAllMonthStats()
                          if (bdjobsList.isNullOrEmpty()) {
                              populateDataAllMonthStats()
                          } else {
                              mybdjobsAdapter?.removeAll()
                              bdjobsList.clear()
                              populateDataAllMonthStats()
                          }
                      } else if (activityDate == "1") {
                          lastMonthStatsData = response.body()?.data
                          if (bdjobsList.isNullOrEmpty()) {
                              populateDataLastMonthStats()
                          } else {
                              mybdjobsAdapter?.removeAll()
                              bdjobsList.clear()
                              populateDataLastMonthStats()
                          }
                      }

                      // //Log.d("respp", " === $allStatsData \n $lastMonthStatsData")
                  } catch (e: Exception) {
                      logException(e)
                  }
              }

          })*/
    }

    fun updateNotificationView(count: Int?) {
        //Log.d("rakib", "in mybdjobs fragment $count")
        if (count!! > 0) {
            notificationCountTV?.show()
            if (count <= 99)
                notificationCountTV?.text = "$count"
            else
                notificationCountTV?.text = "99+"
        } else {
            notificationCountTV?.hide()
        }

//        notificationCountTV?.show()
//        bdjobsUserSession = BdjobsUserSession(requireContext())
//        if (bdjobsUserSession.notificationCount!! > 99){
//            notificationCountTV?.text = "99+"
//
//        } else{
//            notificationCountTV?.text = "${bdjobsUserSession.notificationCount!!}"
//
//        }
    }

    private fun transaction(
        fragment: Fragment,
        holderID: Int,
        addToBackStack: Boolean
    ) {
        try {
            val fragmentManager = childFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            if (addToBackStack) {
                fragmentTransaction.apply {
                    replace(holderID, fragment)
                    addToBackStack(fragment::class.java.name)
                }
            } else {
                fragmentTransaction.apply {
                    replace(holderID, fragment)
                }
            }
            fragmentTransaction.commit()
        } catch (e: Exception) {
            logException(e)
        }
    }
}
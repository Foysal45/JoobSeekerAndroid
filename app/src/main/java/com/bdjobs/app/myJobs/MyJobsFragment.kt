package com.bdjobs.app.myJobs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bdjobs.app.LoggedInUserLanding.HomeCommunicator
import com.bdjobs.app.LoggedInUserLanding.ShortListedJobFragment
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.utilities.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_my_jobs.*
import kotlinx.android.synthetic.main.fragment_my_jobs.messageCountTV
import kotlinx.android.synthetic.main.fragment_my_jobs.messageIMGV
import kotlinx.android.synthetic.main.fragment_my_jobs.notificationCountTV
import kotlinx.android.synthetic.main.fragment_my_jobs.notificationIMGV
import kotlinx.android.synthetic.main.fragment_my_jobs.profilePicIMGV
import kotlinx.android.synthetic.main.fragment_my_jobs.searchIMGV
import org.jetbrains.anko.doAsync
import timber.log.Timber

@Suppress("SameParameterValue")
class MyJobsFragment : Fragment() {


    lateinit var bdJobsDB: BdjobsDB
    lateinit var bdJobsUserSession: BdjobsUserSession
    lateinit var homeCommunicator: HomeCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_jobs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Timber.d("From : ${arguments?.getString("from")}")

        when (arguments?.getString("from")) {
            "favSearch" -> {
                try {
                    transaction(
                        FavouriteSearchList(),
                        R.id.fragment_container,
                        false
                    )
                    tabs.getTabAt(2)?.select()
                } catch (e: Exception) {
                }
            }
            "follow" -> {
                try {
                    transaction(
                        FollowedEmployersFragment(),
                        R.id.fragment_container,
                        false
                    )
                    tabs.getTabAt(1)?.select()
                } catch (e: Exception) {
                }
            }
            else -> {
                transaction(
                    ShortListedJobFragment(),
                    R.id.fragment_container,
                    false
                )
            }
        }

        bdJobsDB = BdjobsDB.getInstance(requireContext())
        bdJobsUserSession = BdjobsUserSession(requireContext())
        homeCommunicator = requireActivity() as HomeCommunicator
        profilePicIMGV?.loadCircularImageFromUrl(bdJobsUserSession.userPicUrl)
        searchIMGV?.setOnClickListener {
            homeCommunicator.gotoJobSearch()
        }
        profilePicIMGV?.setOnClickListener {
            homeCommunicator.gotoEditresume()
        }
        notificationIMGV?.setOnClickListener {
            homeCommunicator.goToNotifications()
        }

        messageIMGV?.setOnClickListener {
            homeCommunicator.goToMessages()
        }
        
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tabs.selectedTabPosition) {
                    0 -> transaction(
                        ShortListedJobFragment(),
                        R.id.fragment_container,
                        false
                    )
                    1 -> transaction(
                        FollowedEmployersFragment(),
                        R.id.fragment_container,
                        false
                    )
                    2 -> transaction(
                        FavouriteSearchList(),
                        R.id.fragment_container,
                        false
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    override fun onResume() {
        super.onResume()

        showNotificationCount()
        showMessageCount()
    }

    override fun onDetach() {

        Timber.d("Detached!")
        arguments?.putString("from", "short")

        super.onDetach()
    }

    @SuppressLint("SetTextI18n")
    fun updateMessageView(count: Int?) {
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

    @SuppressLint("SetTextI18n")
    private fun showMessageCount() {
        try {

            doAsync {
                bdJobsUserSession = BdjobsUserSession(requireContext())
                val count = bdJobsDB.notificationDao().getMessageCount()
                Timber.d("Messages count: $count")
                bdJobsUserSession.updateMessageCount(count)
            }

            if (bdJobsUserSession.messageCount!! <= 0) {
                messageCountTV?.hide()
            } else {
                messageCountTV?.show()
                if (bdJobsUserSession.messageCount!! > 99) {
                    messageCountTV?.text = "99+"

                } else {
                    messageCountTV?.text = "${bdJobsUserSession.messageCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showNotificationCount() {
        try {
            bdJobsUserSession = BdjobsUserSession(requireContext())
            if (bdJobsUserSession.notificationCount!! <= 0) {
                notificationCountTV?.hide()
            } else {
                notificationCountTV?.show()
                if (bdJobsUserSession.notificationCount!! > 99) {
                    notificationCountTV?.text = "99+"

                } else {
                    notificationCountTV?.text = "${bdJobsUserSession.notificationCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateNotificationView(count: Int?) {
        //Log.d("rakib", "in shorlist fragment $count")
        if (count!! > 0) {
            notificationCountTV?.show()
            if (count <= 99)
                notificationCountTV?.text = "$count"
            else
                notificationCountTV?.text = "99+"
        } else {
            notificationCountTV?.hide()
        }
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
package com.bdjobs.app.Notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.google.android.gms.ads.AdRequest
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_notification_base.*
import kotlinx.android.synthetic.main.activity_notification_base.backIV

class NotificationBaseActivity : AppCompatActivity(), NotificationCommunicatior {
    override fun goToNotificationListFragment() {
        transitFragment(notificationListFragment, R.id.notificationFragmentHolder)
    }

    override fun goToMessageListFragment() {
        transitFragment(messageListFragment, R.id.notificationFragmentHolder)
    }

    val notificationListFragment = NotificationListFragment()
    val messageListFragment = MessageListFragment()

    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_base)
        setupClickListeners()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tabs.selectedTabPosition == 0) {

                    goToNotificationListFragment()
                } else {

                    goToMessageListFragment()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


        try {
            adView_notifications?.hide()
            val deviceInfo = getDeviceInformation()
            val screenSize = deviceInfo[Constants.KEY_SCREEN_SIZE]

            screenSize?.let{it->
                if(it.toFloat()>5.0){
                    val adRequest = AdRequest.Builder().build()
                    adView_notifications?.loadAd(adRequest)
                    adView_notifications?.show()
                }
            }
        } catch (e: Exception) {
            logException(e)
        }

        goToNotificationListFragment()

    }

    private fun setupClickListeners() {
        backIV?.setOnClickListener {
            onBackPressed()
        }
    }


}

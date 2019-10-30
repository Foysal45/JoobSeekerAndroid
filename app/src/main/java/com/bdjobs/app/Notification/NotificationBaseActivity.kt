package com.bdjobs.app.Notification

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.BroadCastReceivers.BackgroundJobBroadcastReceiver
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.google.android.gms.ads.AdRequest
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_notification_base.*
import kotlinx.android.synthetic.main.activity_notification_base.backIV
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class NotificationBaseActivity : AppCompatActivity(), NotificationCommunicatior, BackgroundJobBroadcastReceiver.NotificationUpdateListener {

    override fun positionClickedMessage(item: Int) {
        positionClickedMessage = item
    }

    override fun getPositionClickedMessage(): Int {
        return positionClickedMessage
    }

    override fun positionClicked(item: Int) {
        positionClicked = item
    }

    override fun getPositionClicked(): Int {
        return positionClicked
    }

    override fun onUpdateNotification() {
        Log.d("rakib", "came in noti list")
        doAsync {
            val item = bdjobsDB.notificationDao().getSingleItem()

            uiThread {
                if (item.type == "pm"){
                    messageListFragment.updateView(item)
                } else{
                    notificationListFragment.updateView(item)
                }
            }
        }
    }

    private val intentFilter = IntentFilter(Constants.BROADCAST_DATABASE_UPDATE_JOB)
    private lateinit var backgroundJobBroadcastReceiver: BackgroundJobBroadcastReceiver

    override fun goToNotificationListFragment() {
        transitFragment(notificationListFragment, R.id.notificationFragmentHolder)
    }

    override fun goToMessageListFragment() {
        transitFragment(messageListFragment, R.id.notificationFragmentHolder)
    }

    val notificationListFragment = NotificationListFragment()
    val messageListFragment = MessageListFragment()
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    private var positionClicked: Int = 0
    private var positionClickedMessage : Int = 0
    var from = ""

    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_base)

        backgroundJobBroadcastReceiver = BackgroundJobBroadcastReceiver()
        bdjobsDB = BdjobsDB.getInstance(this@NotificationBaseActivity)
        bdjobsUserSession = BdjobsUserSession(this@NotificationBaseActivity)

        setupClickListeners()

        try {
            from = intent.getStringExtra("from")
        } catch (e: Exception) {
            logException(e)
        }



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

        when{
            from?.equals("notification")->{
                try {
                    val tab = tabs.getTabAt(1)
                    tab!!.select()
                    goToMessageListFragment()
                } catch (e: Exception) {
                }
            }
            else->{
                goToNotificationListFragment()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(backgroundJobBroadcastReceiver, intentFilter)
        BackgroundJobBroadcastReceiver.notificationUpdateListener = this
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(backgroundJobBroadcastReceiver)
    }

    private fun setupClickListeners() {
        backIV?.setOnClickListener {
            onBackPressed()
        }
    }

}

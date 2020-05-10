package com.bdjobs.app.Notification

import android.annotation.TargetApi
import android.content.Intent
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
import java.util.Arrays.asList
import io.fabric.sdk.android.services.settings.IconRequest.build
import android.content.Intent.ACTION_VIEW
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.Databases.Internal.Notification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class NotificationBaseActivity : FragmentActivity(), NotificationCommunicatior, BackgroundJobBroadcastReceiver.NotificationUpdateListener {


    private var seen = false

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
        //Log.d("rakib", "came in noti list")
        doAsync {
            val item : Notification? = bdjobsDB.notificationDao().getSingleItem()

            uiThread {
                item?.let {
                    if (item.type == "pm"){
                        messageListFragment.updateView(item)
                    } else{
                        notificationListFragment.updateView(item)
                    }
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
    var id = ""
    var nId = ""

    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_base)

//        if (Build.VERSION.SDK_INT >= 25) {
//            createShorcut()
//        }

        backgroundJobBroadcastReceiver = BackgroundJobBroadcastReceiver()
        bdjobsDB = BdjobsDB.getInstance(this@NotificationBaseActivity)
        bdjobsUserSession = BdjobsUserSession(this@NotificationBaseActivity)

        setupClickListeners()

        try {
            from = intent.getStringExtra("from")
            id = intent.getStringExtra("id")
            nId = intent.getStringExtra("nid")

        } catch (e: Exception) {
            logException(e)
        }

        try {
            seen = intent.getBooleanExtra("seen",false)
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
            Ads.loadAdaptiveBanner(this@NotificationBaseActivity,adView_notifications)
        } catch (e: Exception) {
            logException(e)
        }

        when{
            from?.equals("notification")->{
                logDataForAnalytics(Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE, applicationContext, id,nId)
                try {
                    ApiServiceJobs.create().sendDataForAnalytics(
                            userID = bdjobsUserSession.userId, decodeID = bdjobsUserSession.decodId, uniqueID =  nId, notificationType = Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE, encode = Constants.ENCODED_JOBS, sentTo = "Android"
                    ).enqueue(
                            object : Callback<String> {
                                override fun onFailure(call: Call<String>, t: Throwable) {
                                }
                                override fun onResponse(call: Call<String>, response: Response<String>) {
                                    try {
                                        if (response.isSuccessful) {
                                        }
                                    } catch (e: Exception) {
                                        logException(e)
                                    }
                                }
                            }
                    )
                } catch (e: Exception) {
                }
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

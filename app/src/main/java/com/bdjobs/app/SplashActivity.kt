package com.bdjobs.app

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.view.Window
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.work.*
//import com.bdjobs.app.BackgroundJob.DatabaseUpdateJob
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.GuestUserLanding.GuestUserJobSearchActivity
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.SessionManger.DeviceProtectedSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.name_sharedPref
import com.bdjobs.app.Workmanager.DatabaseUpdateWorker
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.fondesa.kpermissions.request.PermissionRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.squareup.picasso.Picasso
import com.squareup.picasso.PicassoTools
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.no_internet.*
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.security.MessageDigest


class SplashActivity : FragmentActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    lateinit var pref: SharedPreferences
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private lateinit var dataStorage: DataStorage
    private lateinit var internalDb: BdjobsDB
    private lateinit var mPublisherInterstitialAd: PublisherInterstitialAd
    private val APP_UPDATE_REQUEST_CODE = 156
    private var dialog: Dialog? = null
    private var firstDialog: Dialog? = null
    lateinit var request: PermissionRequest
    private var connectionStatus = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        bdjobsUserSession = BdjobsUserSession(applicationContext)
        internalDb = BdjobsDB.getInstance(this@SplashActivity)
        //generateKeyHash()
        getFCMtoken()
        subscribeToFCMTopic("Global")
//        unsubscribeFromFCMTopic("test_rakib")
//        MobileAds.initialize(this@SplashActivity, Constants.ADMOB_APP_ID)
        MobileAds.initialize(this)
        /* mPublisherInterstitialAd = PublisherInterstitialAd(this)
         mPublisherInterstitialAd.adUnitId = "/6499/example/interstitial"
         mPublisherInterstitialAd.loadAd(PublisherAdRequest.Builder().build())*/
        PicassoTools().clearCache(Picasso.get())

//        remoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setUserId(bdjobsUserSession.userId)

//        val configSettings = FirebaseRemoteConfigSettings.Builder()
//                .build()
//        remoteConfig.setConfigSettingsAsync(configSettings)
//        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        setRemoteConfigValues()

        val deviceProtectedSession = DeviceProtectedSession(this)
        deviceProtectedSession.isLoggedIn = bdjobsUserSession.isLoggedIn

        if (!bdjobsUserSession.isFirstInstall!!) {
            if (this.isFirstInstall() || this.isInstallFromUpdate()) {
                Timber.d("Splash: FirstInstall")
                bdjobsUserSession.isFirstInstall = true
                bdjobsUserSession.firstInstallAt = currentDate
            }
        }

    }

    private fun setRemoteConfigValues() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 360
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate().addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                val updated = task.result
                Timber.d("Config params updated: $updated")
                Timber.d("Config value for MyBdJobs Ad: ${remoteConfig["AD_IN_MYBDJOBS"].asString()}")
                Timber.d("Config value for JobList Ad: ${remoteConfig["AD_IN_JOBLIST"].asString()}")
                Timber.d("Config value for Landing Ad: ${remoteConfig["AD_IN_MAIN_LANDING"].asString()}")
                bdjobsUserSession.adTypeMyBdJobs = remoteConfig["AD_IN_MYBDJOBS"].asString()
                bdjobsUserSession.adTypeJobList = remoteConfig["AD_IN_JOBLIST"].asString()
                bdjobsUserSession.adTypeLanding = remoteConfig["AD_IN_MAIN_LANDING"].asString()
                Timber.d("Session value for MyBdJobs Ad: ${bdjobsUserSession.adTypeMyBdJobs}")
                Timber.d("Session value for Job List Ad: ${bdjobsUserSession.adTypeJobList}")
                Timber.d("Session value for Landing Ad: ${bdjobsUserSession.adTypeLanding}")
            } else {
                Timber.e("Remote config task failed")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pref = getSharedPreferences(name_sharedPref, Context.MODE_PRIVATE)
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun showExplanationFirstTimePopup(isConnected: Boolean) {
        Timber.d("First")
        firstDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        firstDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        firstDialog?.setCancelable(false)
        firstDialog?.setContentView(R.layout.layout_explanation_first_time_pop_up)
        firstDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val helpBtn = firstDialog?.findViewById<Button>(R.id.btn_help)
        val agreedBtn = firstDialog?.findViewById<Button>(R.id.btn_next)

        helpBtn?.setOnClickListener {
            openUrlInBrowser("https://www.bdjobs.com/tos.asp")
        }

        agreedBtn?.setOnClickListener {
            Timber.d("Agreed button clicked")

            permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE).build().send { result ->
                when {
                    result.allGranted() -> {
                        Timber.d("All granted!!")
                        // Notified when the permissions are accepted.
                        dataStorage = DataStorage(this@SplashActivity) // don't delete this line. It is used to copy db
                        firstDialog?.dismiss()
                        //Log.d("rakib", "on accepted")
                        doWork(connectionStatus)
                    }
                    result.allDenied() || result.anyDenied() -> {
                        Timber.d("Denied!!")
                    }

                    result.allPermanentlyDenied() || result.anyPermanentlyDenied() -> {

                        showPermanentlyDeniedPopup(firstDialog as Dialog)
                    }
                }
            }


        }
        firstDialog?.show()
    }

    private fun takeDecisions(isConnected: Boolean) {

        //Log.d("rakib", "take decisions called")

        if (isConnected) {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                showExplanationFirstTimePopup(isConnected)
            } else {
                //Log.d("rakib", "below else")
                doWork(isConnected)
            }
        } else {
            doWork(isConnected)
        }


    }

    private fun showPermanentlyDeniedPopup(firstDialog: Dialog) {
        dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.layout_permanently_denied_popup)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val agreedBtn = dialog?.findViewById<Button>(R.id.btn_next)

        agreedBtn?.setOnClickListener {
            firstDialog?.dismiss()
            val intent = createAppSettingsIntent()
            startActivity(intent)
        }
        dialog?.show()
    }

    private fun doWork(connected: Boolean) {
        var mSnackBar: Snackbar? = null
        //Log.d("rakib", "connection in doWork $connected")
        if (!connected) {
            try {
                setContentView(R.layout.no_internet)
                mSnackBar = Snackbar
                        .make(noInternet, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.turn_on_wifi)) {
                            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                        }
                        .setActionTextColor(resources.getColor(R.color.colorWhite))
                mSnackBar.show()
            } catch (e: Exception) {
            }

        } else {
            //Log.d("rakib", "do work else")
            if (bdjobsUserSession.isLoggedIn!!) {
//                if (!Constants.isDeviceInfromationSent) {
//                    //Log.d("rakib", "token sent from splash")
//                    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
//                        val token = instanceIdResult.token
//                        Constants.sendDeviceInformation(token, this@SplashActivity)
//                    }
//                }
//                DatabaseUpdateJob.runJobImmediately()

//                scheduleNotification()

//                WorkManager.getInstance(applicationContext).cancelAllWorkByTag("test")
//                WorkManager.getInstance(applicationContext).cancelAllWorkByTag("live")

                val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                val databaseUpdateRequest = OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
                        .setConstraints(constraints)
                        .build()

                WorkManager.getInstance(applicationContext).enqueue(databaseUpdateRequest)

//                val request = PeriodicWorkRequestBuilder<AlertJobWorker>(5, TimeUnit.MINUTES)
//                        .build()
//                WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("test", ExistingPeriodicWorkPolicy.KEEP, request)

//                val liveInterviewRequest = OneTimeWorkRequestBuilder<LiveInterviewAlertWorker>()
//                        .addTag("live")
//                        .build()
//                WorkManager.getInstance(applicationContext).enqueue(liveInterviewRequest)

            }
            try {
                mSnackBar?.dismiss()
                setContentView(R.layout.activity_splash)
                version_name_tv?.text = "v${getAppVersion()} (${getAppVersionCode()})"
            } catch (e: Exception) {
            }
            showAdAndGoToNextActivity()
        }
    }

    private fun showAdAndGoToNextActivity() {
        checkUpdate()
    }

    private fun getNotification(content: String): Notification {
        val builder = Notification.Builder(this)
        builder.setContentTitle("Scheduled Notification")
        builder.setContentText(content)
        builder.setSmallIcon(R.drawable.bdjobs_app_logo)
        return builder.build()
    }

    private fun goToNextActivity() {
        Timber.d("Going to nextActivity")
        try {
            //Log.d("XZXfg", "The interstitial wasn't loaded yet.")
            //Log.d("XZXfg", "showAdAndGoToNextActivity :${bdjobsUserSession.isLoggedIn!!}")
            if (!bdjobsUserSession.isLoggedIn!!) {
                if (!isFinishing) {
                    startActivity<GuestUserJobSearchActivity>()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
            } else {
                try {
                    if (Build.VERSION.SDK_INT >= 25) {
                        createShortcut(this@SplashActivity)
                    }
                } catch (e: Exception) {
                }
                val intent = Intent(this@SplashActivity, MainLandingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finishAffinity()
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun checkUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this@SplashActivity)
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo

        appUpdateInfoTask?.addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        it.result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    appUpdateManager?.startUpdateFlowForResult(
                            it.result,
                            AppUpdateType.IMMEDIATE,
                            this@SplashActivity,
                            APP_UPDATE_REQUEST_CODE)
                } else {
                    //Log.d("UpdateCheck", "UPDATE_IS_NOT_AVAILABLE")
                    goToNextActivity()
                }
            } else {
                //Log.d("UpdateCheck", "came here else")
                goToNextActivity()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Log.d("UpdateCheck", "requestCode= $requestCode\nresultCode= $resultCode ")
        if (requestCode == APP_UPDATE_REQUEST_CODE && resultCode != RESULT_OK) {
            //Log.d("UpdateCheck", "UPDATE_AGAIN OR GO_TO_NEXT_ACTIVITY")
            //checkUpdate()
            goToNextActivity()
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        connectionStatus = isConnected
        //Log.d("rakib", "connection $isConnected")
        takeDecisions(isConnected)
        //Log.d("splash", "called")
    }

    private fun generateKeyHash() {
        try {
            val info = packageManager.getPackageInfo(applicationContext.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                debug("KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetBroadCastReceiver)
    }

    private fun createAppSettingsIntent() = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    }

    override fun onRestart() {
        super.onRestart()
        //Log.d("rakib", "called on restart")
        dialog?.dismiss()
        firstDialog?.dismiss()
        takeDecisions(connectionStatus)
    }
}


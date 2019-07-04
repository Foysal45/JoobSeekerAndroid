package com.bdjobs.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.DatabaseUpdateModel
import com.bdjobs.app.BackgroundJob.DatabaseUpdateJob
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.Databases.External.DBHelper.Companion.DB_NAME
import com.bdjobs.app.Databases.External.DBHelper.Companion.DB_PATH
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.GuestUserLanding.GuestUserJobSearchActivity
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.dfault_date_db_update
import com.bdjobs.app.Utilities.Constants.Companion.key_db_update
import com.bdjobs.app.Utilities.Constants.Companion.name_sharedPref
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.no_internet.*
import okhttp3.ResponseBody
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.security.MessageDigest


class SplashActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener {
    lateinit var pref: SharedPreferences
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private lateinit var dataStorage: DataStorage
    private lateinit var mPublisherInterstitialAd: PublisherInterstitialAd
    private val APP_UPDATE_REQUEST_CODE = 156


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        dataStorage = DataStorage(this@SplashActivity) // don't delete this line. It is used to copy db
        bdjobsUserSession = BdjobsUserSession(applicationContext)
        //generateKeyHash()
        getFCMtoken()
        subscribeToFCMTopic("Kamol")
        if (bdjobsUserSession.isLoggedIn!!) {
            DatabaseUpdateJob.runJobImmediately()
        }
        MobileAds.initialize(this@SplashActivity, Constants.ADMOB_APP_ID)
        /* mPublisherInterstitialAd = PublisherInterstitialAd(this)
         mPublisherInterstitialAd.adUnitId = "/6499/example/interstitial"
         mPublisherInterstitialAd.loadAd(PublisherAdRequest.Builder().build())*/
    }


    override fun onResume() {
        super.onResume()
        pref = getSharedPreferences(name_sharedPref, Context.MODE_PRIVATE)
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun takeDecisions(isConnected: Boolean) {

        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            val request = permissionsBuilder(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE).build()

            request.send()

            request.listeners {

                onAccepted { permissions ->
                    // Notified when the permissions are accepted.
                    doWork(isConnected)
                }

                onDenied { permissions ->
                    // Notified when the permissions are denied.
                }

                onPermanentlyDenied { permissions ->
                    // Notified when the permissions are permanently denied.
                }

                onShouldShowRationale { permissions, nonce ->
                    // Notified when the permissions should show a rationale.
                    // The nonce can be used to request the permissions again.
                    nonce.use()
                }
            }
        } else {
            doWork(isConnected)
        }

    }

    private fun doWork(connected: Boolean) {
        var mSnackBar: Snackbar? = null
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
            try {
                mSnackBar?.dismiss()
                setContentView(R.layout.activity_splash)
            } catch (e: Exception) {
            }

            val dbUpdateDate = pref.getString(key_db_update, dfault_date_db_update)
            //debug("getDbInfo: Update_date = $dbUpdateDate")

            ApiServiceJobs.create().getDbInfo(dbUpdateDate!!).enqueue(object : Callback<DatabaseUpdateModel> {
                override fun onFailure(call: Call<DatabaseUpdateModel>?, t: Throwable?) {
                    try {
                        debug("getDbInfo: ${t?.message!!}")
                        showAdAndGoToNextActivity()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                override fun onResponse(call: Call<DatabaseUpdateModel>?, response: Response<DatabaseUpdateModel>?) {

                    try {
                        if (response?.body()?.messageType == "1") {
                            if (response.body()?.update == "1") {
                                downloadDatabase(response.body()?.dblink!!, response.body()?.lastupdate!!)
                            } else {
                                showAdAndGoToNextActivity()
                            }
                        } else {
                            showAdAndGoToNextActivity()
                        }
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

            })
        }
    }

    fun downloadDatabase(dbDownloadLink: String, updateDate: String) {

        ApiServiceJobs.create().downloadDatabaseFile(dbDownloadLink).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                showAdAndGoToNextActivity()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                try {
                    if (response?.isSuccessful!!) {
                        //debug("getDbInfo: server contacted and has file")
                        val writtenToDisk = writeResponseBodyToDisk(response.body()!!)
                        // debug("getDbInfo: file download was a success? $writtenToDisk")

                        if (writtenToDisk) {
                            pref.edit {
                                putString(key_db_update, updateDate)
                            }
                        }
                        showAdAndGoToNextActivity()

                    } else {
                        debug("getDbInfo: server contact failed")
                        showAdAndGoToNextActivity()
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    fun showAdAndGoToNextActivity() {
        /* mPublisherInterstitialAd.adListener = object : AdListener() {
             override fun onAdLoaded() {
                 // Code to be executed when an ad finishes loading.
                 mPublisherInterstitialAd.show()
             }

             override fun onAdFailedToLoad(errorCode: Int) {
                 checkUpdate()
             }

             override fun onAdOpened() {
                 // Code to be executed when the ad is displayed.
             }

             override fun onAdLeftApplication() {
                 // Code to be executed when the user has left the app.

             }

             override fun onAdClosed() {
                 checkUpdate()
             }
         }*/
        checkUpdate()
    }

    private fun goToNextActivity() {
        try {
            Log.d("XZXfg", "The interstitial wasn't loaded yet.")
            Log.d("XZXfg", "showAdAndGoToNextActivity :${bdjobsUserSession.isLoggedIn!!}")
            if (!bdjobsUserSession.isLoggedIn!!) {
                if (!isFinishing) {
                    startActivity<GuestUserJobSearchActivity>()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
            } else {
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
        //goToNextActivity()
        val appUpdateManager = AppUpdateManagerFactory.create(this@SplashActivity)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { it ->
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                Log.d("UpdateCheck", "UPDATE_AVAILABLE")
                appUpdateManager.startUpdateFlowForResult(
                        it,
                        AppUpdateType.IMMEDIATE,
                        this@SplashActivity,
                        APP_UPDATE_REQUEST_CODE)
            } else {
                Log.d("UpdateCheck", "UPDATE_IS_NOT_AVAILABLE")
                goToNextActivity()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("UpdateCheck", "requestCode= $requestCode\nresultCode= $resultCode ")
        if (requestCode == APP_UPDATE_REQUEST_CODE && resultCode != RESULT_OK) {
            Log.d("UpdateCheck", "UPDATE_AGAIN OR GO_TO_NEXT_ACTIVITY")
            //checkUpdate()
            goToNextActivity()
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        takeDecisions(isConnected)
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        try {

            val dbFile = File(DB_PATH + DB_NAME)

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(dbFile)

                while (true) {
                    val read = inputStream!!.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream.write(fileReader, 0, read)

                    fileSizeDownloaded += read.toLong()

                    debug("dbFile download: $fileSizeDownloaded of $fileSize")
                }

                outputStream.flush()

                return true
            } catch (e: IOException) {
                logException(e)
                return false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }

                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            logException(e)
            return false
        }
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
}


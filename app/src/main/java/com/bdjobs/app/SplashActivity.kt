package com.bdjobs.app

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageInfo
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
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.GuestUserLanding.GuestUserJobSearchActivity
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.dfault_date_db_update
import com.bdjobs.app.Utilities.Constants.Companion.key_db_update
import com.bdjobs.app.Utilities.Constants.Companion.name_sharedPref
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.no_internet.*
import okhttp3.ResponseBody
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.security.MessageDigest
import android.os.Build
import android.hardware.usb.UsbDevice.getDeviceId
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import java.text.SimpleDateFormat
import java.util.*


class SplashActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener {
    lateinit var pref: SharedPreferences
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private val internetBroadCastReceiver = ConnectivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        bdjobsUserSession = BdjobsUserSession(applicationContext)
        generateKeyHash()
        getFCMtoken()
        subscribeToFCMTopic("Kamol")
        if (bdjobsUserSession.isLoggedIn!!) {
            DatabaseUpdateJob.runJobImmediately()
        }

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
            setContentView(R.layout.no_internet)
            mSnackBar = Snackbar
                    .make(noInternet, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.turn_on_wifi)) {
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                    .setActionTextColor(resources.getColor(R.color.colorWhite))
            mSnackBar?.show()

        } else {
            mSnackBar?.dismiss()
            setContentView(R.layout.activity_splash)

            val dbUpdateDate = pref.getString(key_db_update, dfault_date_db_update)
            debug("getDbInfo: Update_date = $dbUpdateDate")

            ApiServiceJobs.create().getDbInfo(dbUpdateDate!!).enqueue(object : Callback<DatabaseUpdateModel> {
                override fun onFailure(call: Call<DatabaseUpdateModel>?, t: Throwable?) {
                    debug("getDbInfo: ${t?.message!!}")
                    goToNextActivity()
                }

                override fun onResponse(call: Call<DatabaseUpdateModel>?, response: Response<DatabaseUpdateModel>?) {

                    try {
                        if (response?.body()?.messageType == "1") {
                            if (response.body()?.update == "1") {
                                downloadDatabase(response.body()?.dblink!!, response.body()?.lastupdate!!)
                            } else {
                                goToNextActivity()
                            }
                        } else {
                            goToNextActivity()
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
                debug("getDbInfo: " + t?.message!!)
                goToNextActivity()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                try {
                    if (response?.isSuccessful!!) {
                        debug("getDbInfo: server contacted and has file")
                        val writtenToDisk = writeResponseBodyToDisk(response?.body()!!)
                        debug("getDbInfo: file download was a success? $writtenToDisk")

                        if (writtenToDisk) {
                            pref?.edit {
                                putString(key_db_update, updateDate)
                            }
                        }
                        goToNextActivity()

                    } else {
                        debug("getDbInfo: server contact failed")
                        goToNextActivity()
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    fun goToNextActivity() {
        try {
            Log.d("XZXfg", "goToNextActivity :${bdjobsUserSession?.isLoggedIn!!}")
            if (!bdjobsUserSession?.isLoggedIn!!) {
                if (!isFinishing) {
                    startActivity<GuestUserJobSearchActivity>()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
            } else {
                /* val databaseSync = DatabaseSync(context = this@SplashActivity)
                 databaseSync.insertDataAndGoToHomepage()*/

                val intent = Intent(this@SplashActivity, MainLandingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finishAffinity()
            }
        } catch (e: Exception) {
            logException(e)
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

                    outputStream!!.write(fileReader, 0, read)

                    fileSizeDownloaded += read.toLong()

                    debug("dbFile download: $fileSizeDownloaded of $fileSize")
                }

                outputStream!!.flush()

                return true
            } catch (e: IOException) {
                logException(e)
                return false
            } finally {
                if (inputStream != null) {
                    inputStream!!.close()
                }

                if (outputStream != null) {
                    outputStream!!.close()
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

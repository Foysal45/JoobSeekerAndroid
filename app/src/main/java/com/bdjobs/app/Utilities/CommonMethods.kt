package com.bdjobs.app.Utilities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.View
import android.widget.ProgressBar
import com.bdjobs.app.SplashActivity
import java.util.*


class CommonMethods {

    companion object {

        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

        fun setLanguage(context: Context,localeName: String) {
            val myLocale = Locale(localeName)
            val res = context.resources
                val dm = res.displayMetrics
                val conf = res.configuration
                conf.locale = myLocale
                res.updateConfiguration(conf, dm)
                val refresh = Intent(context, SplashActivity::class.java)
                context.startActivity(refresh)
        }


        fun showProgressBar(progressBar: ProgressBar, activity: Activity) {
            progressBar.visibility = View.VISIBLE
            activity.disableUserInteraction()

        }

        fun stopProgressBar(progressBar: ProgressBar, activity: Activity) {
            progressBar.visibility = View.GONE
            activity.enableUserInteraction()
        }

    }
}


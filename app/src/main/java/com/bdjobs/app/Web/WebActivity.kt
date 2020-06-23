package com.bdjobs.app.Web

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.core.view.isVisible
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.CookieModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_web.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WebActivity : Activity() {
    var url: String? = null
    var from: String = ""
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var cookieSyncManager :CookieSyncManager
    lateinit var cookieManager :CookieManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        bdjobsUserSession = BdjobsUserSession(this@WebActivity)

//        val adRequest = AdRequest.Builder().build()
//        adView?.loadAd(adRequest)

        backIV.setOnClickListener {
            onBackPressed()
        }
        getIntentData()

        cookieSyncManager = CookieSyncManager.createInstance(bdjobsWeb.context)
        cookieManager = CookieManager.getInstance()
        cookieManager?.setAcceptCookie(true)
        cookieManager?.removeSessionCookie()

    }

    private fun onClick() {
        shareIMGV.show()
        shareIMGV.setOnClickListener {
            shareHotJob(url)
        }
    }

    private fun shareHotJob(url: String?) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "${jobList!!.get(position).jobTitle}")
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url)
        startActivity(Intent.createChooser(sharingIntent, "Share"))
    }

    private fun getIntentData() {
        try {
            url = intent.getStringExtra("url")
        } catch (e: Exception) {
            logException(e)
        }

        try {
            from = intent.getStringExtra("from")
        } catch (e: Exception) {
            logException(e)
        }

        ////Log.d("bdjobsWeb", "Url: $url \nfrom: $from")

        when (from) {
            "forgotuserid" -> {
                suggestiveSearchET.text = "Forgot username"
                loadUrlWithoutCookie(url)
            }
            "forgotpassword" -> {
                suggestiveSearchET.text = "Forgot password"
                loadUrlWithoutCookie(url)
            }
            "training" -> {
                suggestiveSearchET.text = "Training"
                loadUrlWithCookie(url)
                onClick()
            }
            "video"->{
                suggestiveSearchET.text = "Video Interview"
                loadUrlWithCookie(url)
            }
            "hotjobs" -> {
                suggestiveSearchET.text = "Hot Jobs"
                try {
                    if (!BdjobsUserSession(context = this).isLoggedIn!!){
                        loadUrlWithoutCookie(url)
                    }else {
                        loadUrlWithCookie(url)

                    }
                } catch (e: Exception) {
                }
                onClick()
            }
            "cvview" -> {
                suggestiveSearchET.text = "View Resume"
                loadUrlWithoutCookie(url)
            }
            "reportJob"->{
                suggestiveSearchET.text = "Report this Job / Company"
                if(BdjobsUserSession(this@WebActivity).isLoggedIn!!){
                    loadUrlWithCookie(url)
                    //Log.d("reportJob","loadUrlWithCookie")
                }else{
                    loadUrlWithoutCookie(url)
                    //Log.d("reportJob","loadUrlWithoutCookie")
                }
            }
            "assessment"-> {
                suggestiveSearchET.text = "Assessment"
                loadUrlWithCookie(url)
            }

            "videoInterview"->{
                suggestiveSearchET.text = "Video Interview"
                loadUrlWithCookie(url)
            }

            else -> {
                loadUrlWithoutCookie(url)
            }
        }

    }

    private fun loadUrlWithoutCookie(url: String?) {
        bdjobsWeb?.settings?.javaScriptEnabled = true
        bdjobsWeb?.settings?.setSupportZoom(true)
        bdjobsWeb?.settings?.builtInZoomControls = true
        bdjobsWeb?.settings?.displayZoomControls = false
        bdjobsWeb?.settings?.loadWithOverviewMode = true
        bdjobsWeb?.settings?.useWideViewPort = true
        bdjobsWeb?.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                bdjobsWeb?.hide()
                shimmer_view_container_JobList?.show()
                shimmer_view_container_JobList?.startShimmer()
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                bdjobsWeb?.show()
                shimmer_view_container_JobList?.hide()
                shimmer_view_container_JobList?.stopShimmer()
            }
        }
        url?.let { ur ->
            bdjobsWeb?.loadUrl(ur)
        }

    }

    private fun loadUrlWithCookie(url: String?) {


        //Log.d("rakib","came here")

        bdjobsWeb?.hide()
        shimmer_view_container_JobList?.show()
        shimmer_view_container_JobList?.startShimmer()

        ApiServiceMyBdjobs.create().getCookies(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId
        ).enqueue(object : Callback<CookieModel> {

            override fun onFailure(call: Call<CookieModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<CookieModel>, response: Response<CookieModel>) {
                try {
                    if (response.body()?.statuscode?.equalIgnoreCase(Constants.api_request_result_code_ok)!!) {

                        response.body()?.data?.forEach { cookie ->
                            val cookieName = cookie.cookieName
                            val cookieValue = cookie.cookieValue
                            val domain = cookie.domain
                            val expires = cookie.expires
                            val cookie = "$cookieName =$cookieValue; Domain=$domain;Path=/; Expires=$expires"
                            //Log.d("LOGTAG", "cookie ------>$cookie")
                            //cookieManager.setCookie(link, "MybdjobsUserId =8%5E0%5B4%5D2%5F1%5B2%5D4; Domain=.bdjobs.com;Path=/; Expires=Wed, 20 Dec 2017 07:28:00 GMT");
                            cookieManager.setCookie(url, cookie)
                        }
                        cookieSyncManager.sync()
                        bdjobsWeb?.settings?.javaScriptEnabled = true
                        bdjobsWeb?.settings?.setSupportZoom(true)
                        bdjobsWeb?.settings?.builtInZoomControls = true
                        bdjobsWeb?.settings?.displayZoomControls = false
                        bdjobsWeb?.settings?.loadWithOverviewMode = true
                        bdjobsWeb?.settings?.useWideViewPort = true


                        bdjobsWeb?.webViewClient = object : WebViewClient() {

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                bdjobsWeb?.hide()
                                shimmer_view_container_JobList?.show()
                                shimmer_view_container_JobList?.startShimmer()

                                super.onPageStarted(view, url, favicon)
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                view?.loadUrl(url)
                                return true
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                                view?.loadUrl(request?.url.toString())
                                return true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                bdjobsWeb?.show()
                                shimmer_view_container_JobList?.hide()
                                shimmer_view_container_JobList?.stopShimmer()
                            }
                        }

                        bdjobsWeb?.loadUrl(url)
                        val cookieGet = cookieManager.getCookie(url)
                        //Log.d("LOGTAG", "cookieGET ------>$cookieGet")
                    }

                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        bdjobsWeb?.onResume()
    }

    override fun onPause() {
        super.onPause()
        bdjobsWeb?.onPause()
    }

    override fun onBackPressed() {
        if (bdjobsWeb.copyBackForwardList().currentIndex > 0 && !from.equalIgnoreCase("reportJob")) {
            bdjobsWeb.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

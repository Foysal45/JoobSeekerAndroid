package com.bdjobs.app.Web

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : Activity() {
    var url: String?=null
    var from: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        backIV.setOnClickListener {
            onBackPressed()
        }
        getIntentData()
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

        Log.d("bdjobsWeb", "Url: $url \nfrom: $from")

        when(from){
            "forgotuserid"->suggestiveSearchET.text = "Forgot username"
            "forgotpassword"->suggestiveSearchET.text = "Forgot password"
        }

        loadUrl(url)

    }


    private fun loadUrl(url:String?) {
        bdjobsWeb.settings.javaScriptEnabled = true
        bdjobsWeb.settings.setSupportZoom(true)
        bdjobsWeb.settings.builtInZoomControls = true
        bdjobsWeb.settings.displayZoomControls = false
        bdjobsWeb.settings.loadWithOverviewMode = true
        bdjobsWeb.settings.useWideViewPort = true
        bdjobsWeb?.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                bdjobsWeb?.hide()
                shimmer_view_container_JobList?.show()
                shimmer_view_container_JobList?.startShimmerAnimation()
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
                shimmer_view_container_JobList?.stopShimmerAnimation()
            }
        }
        url?.let{ur->
            bdjobsWeb.loadUrl(ur)
        }

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
        if (bdjobsWeb.copyBackForwardList().currentIndex > 0) {
            bdjobsWeb.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

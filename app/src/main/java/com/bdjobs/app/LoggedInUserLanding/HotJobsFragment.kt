package com.bdjobs.app.LoggedInUserLanding

import android.app.Fragment
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.CookieModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.fragment_hotjobs_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HotJobsFragment : Fragment() {
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var homeCommunicator: HomeCommunicator


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_hotjobs_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bdjobsUserSession = BdjobsUserSession(activity)
        homeCommunicator = activity as HomeCommunicator

        profilePicIMGV?.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)

        searchIMGV?.setOnClickListener {
            homeCommunicator.goToKeywordSuggestion()
        }

        setUpWebView()
    }

    private fun setUpWebView() {
        val cookieSyncManager = CookieSyncManager.createInstance(webView.context)
        val cookieManager = CookieManager.getInstance()
        cookieManager?.setAcceptCookie(true)
        cookieManager?.removeSessionCookie()

        webView?.hide()
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
                            cookieManager.setCookie(Constants.HOTJOBS_WEB_LINK, cookie)
                        }
                        cookieSyncManager.sync()
                        webView?.settings?.javaScriptEnabled = true
                        webView?.settings?.setSupportZoom(true)
                        webView?.settings?.builtInZoomControls = true
                        webView?.settings?.displayZoomControls = false
                        webView?.settings?.loadWithOverviewMode = true
                        webView?.settings?.useWideViewPort = true


                        webView?.webViewClient = object : WebViewClient() {

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                webView?.hide()
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
                                webView?.show()
                                shimmer_view_container_JobList?.hide()
                                shimmer_view_container_JobList?.stopShimmer()
                            }
                        }

                        webView?.loadUrl(Constants.HOTJOBS_WEB_LINK)
                        val cookieGet = cookieManager.getCookie(Constants.HOTJOBS_WEB_LINK)
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
        webView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    fun getWebviewBacKStack(): Boolean {
        try {
            if (webView?.copyBackForwardList()?.currentIndex!! > 0) {
                webView?.goBack()
                return false
            }
        } catch (e: Exception) {
            logException(e)
            return true
        }
        return true
    }


}
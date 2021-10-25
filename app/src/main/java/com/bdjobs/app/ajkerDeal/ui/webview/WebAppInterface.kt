package com.bdjobs.app.ajkerDeal.ui.webview

import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import androidx.appcompat.app.AppCompatActivity
import com.bdjobs.app.ajkerDeal.repository.AppRepository
import com.bdjobs.app.ajkerDeal.utilities.toast

class WebAppInterface(private val context: Context?, private val repository: AppRepository, private val bundle: Bundle? = null) {

    @JavascriptInterface
    fun showToast(msg: String?) {
        context?.toast(msg)
    }

    @JavascriptInterface
    fun SendToBackButtonInApp(isClicked: String) {
        if (isClicked == "Clicked") {
            (context as AppCompatActivity).onBackPressed()
        }
    }

    @JavascriptInterface
    fun goToAppTrackOrder(CouponId: String) {

    }

    @JavascriptInterface
    fun goToAppHomePage() {

    }

    @JavascriptInterface
    fun goToOrderConfirmationPageInAppForBkash(id: String, message: String, CouponId: String, OrderType: String, TotalPoint: String) {

    }


}
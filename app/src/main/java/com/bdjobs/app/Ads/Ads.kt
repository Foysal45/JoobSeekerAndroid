package com.bdjobs.app.Ads

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.util.Log
import android.widget.FrameLayout
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import org.jetbrains.anko.windowManager

class Ads {


    companion object {
        val ADMOB_NATIVE_AD_UNIT_ID = "ca-app-pub-5130888087776673/8613851148"
        val ADMOB_APP_ID = "ca-app-pub-5130888087776673~6094744346"
        val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-5130888087776673/3622884741"
        var nativeAdvertisement: UnifiedNativeAd? = null
        var mInterstitialAd: InterstitialAd? = null


        fun showNativeAd(nativeAdTemplete: TemplateView, context: Context) {

            try {
                if (nativeAdvertisement != null) {
                    val styles = NativeTemplateStyle
                            .Builder()
                            .withMainBackgroundColor(ColorDrawable(Color.parseColor("#FFFFFF")))
                            .build()
                    nativeAdTemplete?.setStyles(styles)
                    nativeAdTemplete?.setNativeAd(nativeAdvertisement)
                } else {
                    MobileAds.initialize(context, ADMOB_APP_ID)
                    val adLoader = AdLoader.Builder(context, ADMOB_NATIVE_AD_UNIT_ID)
                            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                                nativeAdvertisement = ad
                                val styles = NativeTemplateStyle
                                        .Builder()
                                        .withMainBackgroundColor(ColorDrawable(Color.parseColor("#FFFFFF")))
                                        .build()
                                nativeAdTemplete?.setStyles(styles)
                                nativeAdTemplete?.setNativeAd(ad)

                            }
                            .withAdListener(object : AdListener() {
                                override fun onAdFailedToLoad(errorCode: Int) {
                                    //Log.d("adLoader", "error code: $errorCode")
                                }
                            })
                            .withNativeAdOptions(NativeAdOptions
                                    .Builder()
                                    .build())
                            .build()
                    adLoader.loadAd(AdRequest.Builder().build())
                }
            } catch (e: Exception) {
            }
        }


        fun loadAdaptiveBanner(context: Context, ad_view_container: FrameLayout){
            try {
                MobileAds.initialize(context) { }

                val adView = AdView(context)
                ad_view_container.addView(adView)

                adView.adUnitId = "ca-app-pub-5130888087776673/4478410348"

                val display = context.windowManager.defaultDisplay
                val outMetrics = DisplayMetrics()
                display.getMetrics(outMetrics)

                val density = outMetrics.density

                var adWidthPixels = ad_view_container.width.toFloat()
                if (adWidthPixels == 0f) {
                    adWidthPixels = outMetrics.widthPixels.toFloat()
                }

                val adWidth = (adWidthPixels / density).toInt()

                adView.adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)

                val adRequest = AdRequest
                        .Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
                adView.loadAd(adRequest)
            } catch (e: Exception) {
            }

        }


    }



}
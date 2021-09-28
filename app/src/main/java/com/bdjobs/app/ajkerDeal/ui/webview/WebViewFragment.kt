package com.bdjobs.app.ajkerDeal.ui.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.bdjobs.app.ajkerDeal.repository.AppRepository
import com.bdjobs.app.databinding.FragmentWebViewBinding
import org.koin.android.ext.android.inject
import timber.log.Timber

class WebViewFragment : Fragment() {

    private val repository: AppRepository by inject()
    private var binding: FragmentWebViewBinding? = null

    private var webTitle: String = ""
    private var loadUrl: String = ""
    private var bundle: Bundle? = null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): WebViewFragment = WebViewFragment().apply {
            this.bundle = bundle
            this.arguments = bundle
        }

        @JvmField
        val tag: String = WebViewFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentWebViewBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("WebView Url: $loadUrl")
        Timber.d("WebView Bundle: ${bundle.toString()}")

        this.loadUrl = arguments?.getString("loadUrl") ?: ""
        this.webTitle = arguments?.getString("webTitle") ?: ""

        Timber.d("WebView Url: $loadUrl")
        Timber.d("WebView Bundle: ${bundle}")

        binding?.webView!!.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            //loadWithOverviewMode = true
            //useWideViewPort = true
        }
        with(binding?.webView!!) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            clearHistory()
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            addJavascriptInterface(WebAppInterface(requireContext(), repository, bundle), "Android")
            webViewClient = Callback()
            //clearCache(true)
        }

        binding?.webView?.loadUrl(loadUrl)
    }

    inner class Callback : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            //return super.shouldOverrideUrlLoading(view, request)
            val url = request?.url
            view?.loadUrl(url.toString())
            return true
            // Url base logic here
            /*val url = request?.url?.path
            if (url?.startsWith("intent://scan/") == true) {
                // Do Stuff
                return true
            }*/
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding?.progressBar?.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            binding?.progressBar?.visibility = View.GONE
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            Timber.d(error.toString())
            binding?.progressBar?.visibility = View.GONE
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            super.onReceivedSslError(view, handler, error)

            handler?.proceed()

            /*val builder = AlertDialog.Builder(requireContext())
            var message = when (error?.primaryError) {
                SslError.SSL_UNTRUSTED -> "The certificate authority is not trusted."
                SslError.SSL_EXPIRED -> "The certificate has expired."
                SslError.SSL_IDMISMATCH -> "The certificate Hostname mismatch."
                SslError.SSL_NOTYETVALID -> "The certificate is not yet valid."
                else -> "SSL Error."
            }
            message += " Do you want to continue anyway?"

            builder.setTitle("SSL Certificate Error")
            builder.setMessage(message)
            builder.setPositiveButton("continue") { _, _ -> handler?.proceed() }
            builder.setNegativeButton("cancel") { _, _ -> handler?.cancel() }
            val dialog = builder.create()
            dialog.show()*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}

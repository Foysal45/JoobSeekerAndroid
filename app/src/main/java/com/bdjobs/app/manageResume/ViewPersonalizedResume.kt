package com.bdjobs.app.manageResume

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bdjobs.app.manageResume.utils.getRootDirPath
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.hide
import com.bdjobs.app.utilities.show
import com.bdjobs.app.databinding.ActivityViewPersonalizedResumeBinding
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import kotlinx.android.synthetic.main.activity_view_personalized_resume.*
import timber.log.Timber
import java.io.File

class ViewPersonalizedResume : AppCompatActivity() {

    private lateinit var binding: ActivityViewPersonalizedResumeBinding
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private var pdfUrl:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_personalized_resume)
        bdjobsUserSession = BdjobsUserSession(this)

        pdfUrl = intent.getStringExtra("PDF_URL")

        setSupportActionBar(binding.toolbar3)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        PRDownloader.initialize(this)

        downloadPdfFromInternet(
            pdfUrl!!, getRootDirPath(this),"${bdjobsUserSession.fullName}.pdf"
        )

    }


    private fun downloadPdfFromInternet(url: String, dirPath: String, fileName: String) {
        PRDownloader.download(
            url,
            dirPath,
            fileName
        ).build()
            .start(object : OnDownloadListener {
                @SuppressLint("SetTextI18n")
                override fun onDownloadComplete() {
                    val downloadedFile = File(dirPath, fileName)
                    binding.progressBar.visibility = View.GONE
                    try {

                        cl_success.show()
                        cl_error_view.hide()
                        showPdfFromFile(downloadedFile)
                    } catch (e: Exception) {
                        Timber.tag("ViewPersonalizedResume").d("onDownloadComplete Error $e")
                        cl_success.hide()
                        cl_error_view.show()
                        tv_error_message.text = "Sorry! This format is not supported! Only PDF file can be viewed."
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onError(error: com.downloader.Error?) {
                    binding.progressBar.visibility = View.GONE
                    Timber.tag("ViewPersonalizedResume").d("onError Error $error")
                    cl_success.hide()
                    cl_error_view.show()
                    tv_error_message.text = "Sorry! File can't be downloaded, Please check your internet connection and try again!"
//                    Toast.makeText(this@ViewPersonalizedResume, "Error in downloading file : $error", Toast.LENGTH_LONG).show()
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun showPdfFromFile(file: File) {
        binding.pdfView.fromFile(file)
            .password(null)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .onError {
                Timber.tag("ViewPersonalizedResume").d("showPdfFromFile Error $it")
                cl_success.hide()
                cl_error_view.show()
                tv_error_message.text = "Sorry! This format is not supported! Only PDF file can be viewed."
//                Toast.makeText(
//                    this@ViewPersonalizedResume,
//                    "File format is not supported to preview Personalized Resume",
//                    Toast.LENGTH_LONG
//                ).show()
            }
            .onPageError { page, _ ->
                Toast.makeText(
                    this@ViewPersonalizedResume,
                    "Error at page: $page", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(event?.action == KeyEvent.ACTION_DOWN) {
            onBackPressed()
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}
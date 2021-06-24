package com.bdjobs.app.ManageResume

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bdjobs.app.ManageResume.utils.getRootDirPath
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.databinding.ActivityViewPersonalizedResumeBinding
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
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
                override fun onDownloadComplete() {
                    val downloadedFile = File(dirPath, fileName)
                    binding.progressBar.visibility = View.GONE
                    showPdfFromFile(downloadedFile)
                }

                override fun onError(error: com.downloader.Error?) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ViewPersonalizedResume,
                        "Error in downloading file : $error",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            })
    }

    private fun showPdfFromFile(file: File) {
        binding.pdfView.fromFile(file)
            .password(null)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
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
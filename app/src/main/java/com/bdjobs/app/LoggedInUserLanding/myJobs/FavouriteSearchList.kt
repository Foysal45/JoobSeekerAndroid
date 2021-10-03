package com.bdjobs.app.LoggedInUserLanding.myJobs

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bdjobs.app.FavouriteSearch.FavouriteSearchFilterAdapter
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.FavouriteSearch
import com.bdjobs.app.sms.BaseActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.favourite_search_list_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.uiThread

class FavouriteSearchList : Fragment() {

    lateinit var bdJobsUserSession: BdjobsUserSession
    lateinit var bdJobsDB: BdjobsDB
    var favListSize = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favourite_search_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bdJobsUserSession = BdjobsUserSession(requireContext())
        bdJobsDB = BdjobsDB.getInstance(requireContext())

        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)

        btn_sms_settings?.setOnClickListener {
            goToSMSBaseActivity()
        }

        btn_sms_alert_fab.setOnClickListener {
            openSmsAlertDialog()
        }

        btn_job_list.setOnClickListener {
            startActivity<JobBaseActivity>("from" to "alljobsearch")
        }

    }

    override fun onResume() {
        super.onResume()

        doAsync {
            val favouriteSearchFilters = bdJobsDB.favouriteSearchFilterDao().getAllFavouriteSearchFilter()
            uiThread {
                try {
                    favListSize = favouriteSearchFilters.size
                    var data = "filter"
                    if (favListSize > 1) {
                        data = "filters"
                    }


                    if (favListSize > 0) {
                        favouriteFilterNoDataLL?.hide()
                        favRV?.show()
                        //Log.d("totalJobs", "data ase")
                    } else {
                        favouriteFilterNoDataLL?.show()
                        btn_sms_alert_fab.hide()
                        favRV?.hide()
                        //Log.d("totalJobs", "zero")
                    }

                    val styledText = "<b><font color='#13A10E'>$favListSize</font></b> favourite search $data"
                    favCountTV?.text = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY)
                    val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(items = favouriteSearchFilters as MutableList<FavouriteSearch>, context = requireContext())
                    favRV?.adapter = favouriteSearchFilterAdapter
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }

    }

    fun scrollToUndoPosition(position:Int){
        favRV?.scrollToPosition(position)
        favListSize++
        val styledText = "<b><font color='#13A10E'>$favListSize</font></b> favourite search filter"
        favCountTV.text = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY)
    }

    fun decrementCounter(){
        favListSize--
        val styledText = "<b><font color='#13A10E'>$favListSize</font></b> favourite search filter"
        favCountTV.text = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun openSmsAlertDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater = requireContext().layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_sms_alert, null))
        builder.create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            findViewById<ImageView>(R.id.img_close).setOnClickListener {
                this.cancel()
            }
            findViewById<MaterialButton>(R.id.btn_purchase).setOnClickListener {
                requireContext().startActivity(Intent(requireContext(), BaseActivity::class.java))
                this.cancel()
            }
            findViewById<MaterialButton>(R.id.btn_sms_settings).setOnClickListener {
                requireContext().startActivity<BaseActivity>("from" to "employer")
                this.cancel()
            }
            findViewById<TextView>(R.id.tv_body).text =
                "Buy SMS to get job alert from subscribed Favourite Search"
        }
    }


    private fun goToSMSBaseActivity() {
        startActivity<BaseActivity>("from" to "favourite")
    }

}
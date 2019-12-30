package com.bdjobs.app.GuestUserLanding

import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.Login.LoginBaseActivity
import com.bdjobs.app.Login2.Login2BaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SuggestiveSearch.SuggestiveSearchActivity
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.BdjobsUserRequestCode
import com.bdjobs.app.Utilities.Constants.Companion.key_categoryET
import com.bdjobs.app.Utilities.Constants.Companion.key_from
import com.bdjobs.app.Utilities.Constants.Companion.key_go_to_home
import com.bdjobs.app.Utilities.Constants.Companion.key_jobtitleET
import com.bdjobs.app.Utilities.Constants.Companion.key_loacationET
import com.bdjobs.app.Utilities.Constants.Companion.key_typedData
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_guest_user_job_search.*
import kotlinx.android.synthetic.main.activity_guest_user_job_search.version_name_tv
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class GuestUserJobSearchActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var dataStorage: DataStorage
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_user_job_search)
        version_name_tv.text = "v${getAppVersion()} (${getAppVersionCode()})"
        dataStorage = DataStorage(applicationContext)
        initialization()
        onClicks()
        textView?.setOnClickListener {
            version_name_tv?.show()
        }
//        secretTV?.isEnabled = true

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            mSnackBar = Snackbar
                    .make(root, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.turn_on_wifi)) {
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }
                    .setActionTextColor(resources.getColor(R.color.colorWhite))

            mSnackBar?.show()

        } else {
            mSnackBar?.dismiss()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetBroadCastReceiver)
    }

    private fun initialization() {
        jobtitleET?.addTextChangedListener(GuestUserJobSearchTextWatcher(jobtitleET))
        loacationET?.addTextChangedListener(GuestUserJobSearchTextWatcher(loacationET))
        categoryET?.addTextChangedListener(GuestUserJobSearchTextWatcher(categoryET))
    }

    private fun onClicks() {
        profileIMGV?.setOnClickListener {
            startActivity(intentFor<LoginBaseActivity>(key_go_to_home to true))
        }

        jobtitleET?.setOnClickListener {
            goToSuggestiveSearchActivityForResult(key_jobtitleET, jobtitleET)
        }
        loacationET?.setOnClickListener {
            goToSuggestiveSearchActivityForResult(key_loacationET, loacationET)
        }
        categoryET?.setOnClickListener {
            goToSuggestiveSearchActivityForResult(key_categoryET, categoryET)
        }

        guestSearchBTN?.setOnClickListener {
            val keyWord = jobtitleET?.getString()
            val locationName = loacationET?.getString()
            val categoryName = categoryET?.getString()


            val locationId = locationName?.let { string ->
                dataStorage.getLocationIDByName(string)
            }

            val catID = categoryName?.let { string ->
                dataStorage.getCategoryIDByName(string)
            }


            //Log.d("wtji", "keyWord: $keyWord \n locationName: $locationName \n categoryName: $categoryName \n locationId: $locationId \n catID: $catID")


            startActivity<JobBaseActivity>(
                    key_jobtitleET to keyWord,
                    key_loacationET to locationId,
                    key_categoryET to catID,
                    "from" to "guestuser"
            )
        }

        guestHotJobsSearchBTN?.setOnClickListener {
            startActivity<HotJobsActivity>()
        }

//        secretTV?.setOnClickListener {
//            var time: Long = System.currentTimeMillis()
//
//
//            //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
//            if (startMillis.toInt() == 0 || (time - startMillis > 3000)) {
//                startMillis = time
//                count = 1
//                //Log.d("rakib", "secret if")
//            }
//            //it is not the first, and it has been  less than 3 seconds since the first
//            else { //  time-startMillis< 3000
//                count++
//                //Log.d("rakib", "secret else $count")
//            }
//
//            if (count == 10) {
//                secretTV?.isEnabled = false
//                openDialog()
//            }
//        }
    }

    private fun openDialog() {
        val dialog = Dialog(this)
        dialog?.setContentView(R.layout.dialog_pass)
        dialog?.setCancelable(false)
        dialog?.show()
        val passET = dialog?.findViewById<EditText>(R.id.pass_et)
        val okBtn = dialog?.findViewById<Button>(R.id.ok_btn)

        okBtn.setOnClickListener {
            passET?.let {
                if (passET.text.toString() == "secret") {
                    dialog?.dismiss()
                    startActivity(intentFor<Login2BaseActivity>(key_go_to_home to true))
                } else {
                    dialog?.dismiss()
                }
            }

        }
    }


    private fun goToSuggestiveSearchActivityForResult(from: String, editText: EditText?) {
        val intent = Intent(this, SuggestiveSearchActivity::class.java)
        intent.putExtra(key_from, from)
        intent.putExtra(key_typedData, editText?.getString())
        val options = ActivityOptions.makeSceneTransitionAnimation(this, editText!!, "robotx")
        window.exitTransition = null
        startActivityForResult(intent, BdjobsUserRequestCode, options.toBundle())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BdjobsUserRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val typedData = data?.getStringExtra(key_typedData)
                val from = data?.getStringExtra(key_from)
                when (from) {
                    key_jobtitleET -> jobtitleET?.setText(typedData)
                    key_loacationET -> loacationET?.setText(typedData)
                    key_categoryET -> categoryET?.setText(typedData)
                }
            }
        }
    }

    private inner class GuestUserJobSearchTextWatcher(private val editText: EditText) : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            showHideCrossButton(editText)
            debug(editText.getString())
        }
    }

    private fun showHideCrossButton(editText: EditText) {
        if (editText.text.isBlank()) {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_advance_search_24dp, 0)
        } else {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_ash, 0)
            editText.clearTextOnDrawableRightClick()
        }
    }

    override fun onPause() {
        super.onPause()
        version_name_tv?.hide()
    }


}



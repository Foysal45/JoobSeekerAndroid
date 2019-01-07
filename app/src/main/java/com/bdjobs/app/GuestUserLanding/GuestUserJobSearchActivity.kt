package com.bdjobs.app.GuestUserLanding

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.Login.LoginBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SuggestiveSearch.SuggestiveSearchActivity
import com.bdjobs.app.Utilities.Constants.Companion.BdjobsUserRequestCode
import com.bdjobs.app.Utilities.Constants.Companion.key_categoryET
import com.bdjobs.app.Utilities.Constants.Companion.key_from
import com.bdjobs.app.Utilities.Constants.Companion.key_go_to_home
import com.bdjobs.app.Utilities.Constants.Companion.key_jobtitleET
import com.bdjobs.app.Utilities.Constants.Companion.key_loacationET
import com.bdjobs.app.Utilities.Constants.Companion.key_typedData
import com.bdjobs.app.Utilities.clearTextOnDrawableRightClick
import com.bdjobs.app.Utilities.debug
import com.bdjobs.app.Utilities.getString
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_guest_user_job_search.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity

class GuestUserJobSearchActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var dataStorage: DataStorage
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_user_job_search)

        dataStorage = DataStorage(applicationContext)
        initialization()
        onClicks()
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


            Log.d("wtji", "keyWord: $keyWord \n locationName: $locationName \n categoryName: $categoryName \n locationId: $locationId \n catID: $catID")


            startActivity<JobBaseActivity>(
                    key_jobtitleET to keyWord,
                    key_loacationET to locationId,
                    key_categoryET to catID)
        }
    }


    private fun goToSuggestiveSearchActivityForResult(from: String, editText: EditText?) {
        val intent = Intent(this, SuggestiveSearchActivity::class.java)
        intent.putExtra(key_from, from)
        intent.putExtra(key_typedData, editText?.getString())
        val options = ActivityOptions.makeSceneTransitionAnimation(this, editText!!, "robot")
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
        if (editText?.text.isBlank()) {
            editText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            editText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_ash, 0)
            editText?.clearTextOnDrawableRightClick()
        }
    }
}

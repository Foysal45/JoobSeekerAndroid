package com.bdjobs.app.GuestUserLanding

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.Login.LoginBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SuggestiveSearch.SuggestiveSearchActivity
import com.bdjobs.app.Utilities.CommonMethods
import com.bdjobs.app.Utilities.Constants.Companion.guestUserRequestCode
import com.bdjobs.app.Utilities.Constants.Companion.key_categoryET
import com.bdjobs.app.Utilities.Constants.Companion.key_from
import com.bdjobs.app.Utilities.Constants.Companion.key_jobtitleET
import com.bdjobs.app.Utilities.Constants.Companion.key_loacationET
import com.bdjobs.app.Utilities.Constants.Companion.key_typedData
import com.bdjobs.app.Utilities.clearTextOnDrawableRightClick
import com.bdjobs.app.Utilities.debug
import com.bdjobs.app.Utilities.getString
import kotlinx.android.synthetic.main.activity_guest_user_job_search.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class GuestUserJobSearchActivity : Activity() {


    private lateinit var dataStorage: DataStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_user_job_search)

        dataStorage = DataStorage(applicationContext)
        initialization()
        onClicks()
    }

    private fun initialization() {
        jobtitleET.addTextChangedListener(GuestUserJobSearchTextWatcher(jobtitleET))
        loacationET.addTextChangedListener(GuestUserJobSearchTextWatcher(loacationET))
        categoryET.addTextChangedListener(GuestUserJobSearchTextWatcher(categoryET))
    }

    private fun onClicks() {
        profileIMGV.onClick {
            startActivity(intentFor<LoginBaseActivity>())
        }

        jobtitleET.onClick {
            goToSuggestiveSearchActivityForResult(key_jobtitleET, jobtitleET)
        }
        loacationET.onClick {
            goToSuggestiveSearchActivityForResult(key_loacationET, loacationET)
        }
        categoryET.onClick {
            goToSuggestiveSearchActivityForResult(key_categoryET, categoryET)
        }

        guestSearchBTN.onClick {
           startActivity<JobBaseActivity>()
            //CommonMethods.setLanguage(this@GuestUserJobSearchActivity,"bn")
        }
    }


    private fun goToSuggestiveSearchActivityForResult(from: String, editText: EditText?) {
        val intent = Intent(this, SuggestiveSearchActivity::class.java)
        intent.putExtra(key_from, from)
        intent.putExtra(key_typedData, editText?.getString())
        val options = ActivityOptions.makeSceneTransitionAnimation(this, editText!!, "robot")
        window.exitTransition = null
        startActivityForResult(intent, guestUserRequestCode, options.toBundle())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == guestUserRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val typedData = data?.getStringExtra(key_typedData)
                val from = data?.getStringExtra(key_from)
                when (from) {
                    key_jobtitleET -> jobtitleET.setText(typedData)
                    key_loacationET -> loacationET.setText(typedData)
                    key_categoryET -> categoryET.setText(typedData)
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
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_ash, 0)
            editText.clearTextOnDrawableRightClick()
        }
    }
}

package com.bdjobs.app.SuggestiveSearch


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.Suggestion
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.REQ_CODE_SPEECH_INPUT
import com.bdjobs.app.Utilities.Constants.Companion.key_categoryET
import com.bdjobs.app.Utilities.Constants.Companion.key_from
import com.bdjobs.app.Utilities.Constants.Companion.key_industryET
import com.bdjobs.app.Utilities.Constants.Companion.key_jobtitleET
import com.bdjobs.app.Utilities.Constants.Companion.key_loacationET
import com.bdjobs.app.Utilities.Constants.Companion.key_newspaperET
import com.bdjobs.app.Utilities.Constants.Companion.key_special_categoryET
import com.bdjobs.app.Utilities.Constants.Companion.key_typedData
import kotlinx.android.synthetic.main.activity_suggestive_search.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList


class SuggestiveSearchActivity : Activity(), SuggestionCommunicator {

    private lateinit var textData: String
    private lateinit var from: String
    private lateinit var dataStorage: DataStorage
    private val suggestionList = ArrayList<String>()
    private lateinit var bdjobsInternalDB: BdjobsDB
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private val historyList = ArrayList<String>()
    private lateinit var historyListAdapter: HistoryListAdapter

    private lateinit var suggestionAdapter: SuggestionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestive_search)
        initialization()
        onClicks()
        getIntentData()
        setHistoryAdapter(from)
        setFilterAdapter(from)
        setTextWatcher()

        historyViewCL.show()
        suggestiveSearchET.requestFocus()
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun initialization() {
        dataStorage = DataStorage(applicationContext)
        bdjobsInternalDB = BdjobsDB.getInstance(applicationContext)
        bdjobsUserSession = BdjobsUserSession(this)
    }


    private fun setTextWatcher() {

        suggestiveSearchET?.easyOnTextChangedListener { e: CharSequence ->
            if (TextUtils.isEmpty(e)) {
                //Log.d("susu", "susu")
                suggestiveSearchET.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                if (from == key_categoryET || from == key_special_categoryET || from == key_industryET) {
                    filterRV.show()
                    historyViewCL.show()
                } else {
                    filterRV.hide()
                    historyViewCL.show()
                }
            } else {
                suggestiveSearchET?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_white, 0)
                suggestiveSearchET?.clearTextOnDrawableRightClick()
                if (from == key_categoryET || from == key_special_categoryET || from == key_industryET) {
                    filterRV.show()
                    historyViewCL.hide()
                } else {
                    filterRV.show()
                    historyViewCL.hide()
                }
            }

            try {
                suggestionAdapter.filter.filter(e)
            } catch (e:Exception){
                e.printStackTrace()
            }

        }
        suggestiveSearchET?.setText(textData)
        suggestiveSearchET?.setSelection(suggestiveSearchET?.text?.length!!)
    }

    private fun setHistoryAdapter(from: String) {
        doAsync {
            val suggestions = bdjobsInternalDB.suggestionDAO().getSuggestionByFlag(from)
            for (history in suggestions) {
                historyList.add(history.suggestion)
            }
            uiThread {
                if (historyList.size == 0) {
                    lineView.hide()
                    clearAllBTN.hide()
                }

                historyListAdapter = HistoryListAdapter(historyList, this@SuggestiveSearchActivity)
                historyRV?.adapter = historyListAdapter
                historyListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setFilterAdapter(from: String) {
        lateinit var suggestionItems: Array<String>
        when (from) {
            key_jobtitleET -> {
                suggestiveSearchET?.hint = getString(R.string.hint_keyword)
                suggestionItems = dataStorage.allKeywordSuggestions
            }
            key_loacationET -> {
                suggestiveSearchET?.hint = getString(R.string.hint_location)
                suggestionItems = dataStorage.allLocationsExceptPostOffice
            }
            key_categoryET -> {
                suggestiveSearchET?.hint = getString(R.string.hint_Category)
                suggestionItems = dataStorage.allWhiteCollarCategories.toTypedArray()
            }
            key_special_categoryET -> {
                suggestiveSearchET?.hint = getString(R.string.hint_Category)
                suggestionItems = dataStorage.allBlueCollarCategoriesInBangla.toTypedArray()
            }
            key_industryET -> {
                suggestiveSearchET?.hint = "Industries"
                suggestionItems = dataStorage.getAllIndustries
            }
            key_newspaperET -> {
                suggestiveSearchET?.hint = "NewsPaper"
                suggestionItems = dataStorage.getAllNewspapers
            }

        }

        for (item in suggestionItems) {
            suggestionList.add(item)
        }
        //adapter = SuggestionAdapter(suggestionList, this)
        suggestionAdapter = SuggestionListAdapter(suggestionList, this)

        filterRV.adapter = suggestionAdapter
        try {
            suggestionAdapter.notifyDataSetChanged()
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getIntentData() {
        try {
            from = intent.getStringExtra(key_from).toString()
        } catch (e: Exception) {
        }
        try {
            textData = intent.getStringExtra(key_typedData).toString()
        } catch (e: Exception) {
        }
    }

    private fun onClicks() {
        BACKIMGV?.setOnClickListener { onBackPressed() }

        suggestiveSearchET?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->

            if (event.action === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                takeDecision()
                return@OnKeyListener true
            }
            false
        })


        filterIMGV?.setOnClickListener { promptSpeechInput() }
        clearAllBTN?.setOnClickListener {
            clearHistory(from)
        }

    }

    private fun clearHistory(from: String) {
        doAsync {
            bdjobsInternalDB.suggestionDAO().deleteAllKeywordSuggestion(from)
            uiThread {
                historyList.clear()
                historyListAdapter.notifyDataSetChanged()
                lineView?.hide()
                clearAllBTN?.hide()
            }
        }
    }

    private fun takeDecision() {
        when (from) {
            key_loacationET -> {
                if (suggestionList.contains(suggestiveSearchET.text.toString()) || TextUtils.isEmpty(suggestiveSearchET.text.toString())) {
                    textData = suggestiveSearchET.text.toString()
                    doAsync {
                        if (!textData.isBlank()) {
                            val data = Suggestion(textData, from, bdjobsUserSession.userId, Date())
                            bdjobsInternalDB.suggestionDAO().insertSuggestion(data)
                        }
                    }
                    onBackPressed()
                } else {
                    toast("Please select a valid location!")
                }
            }
            key_jobtitleET -> {
                textData = suggestiveSearchET.text.toString()
                doAsync {
                    if (!textData.isBlank()) {
                        val data = Suggestion(textData, from, bdjobsUserSession.userId, Date())
                        bdjobsInternalDB.suggestionDAO().insertSuggestion(data)
                    }
                }
                onBackPressed()
            }
            key_categoryET -> {
                if (suggestionList.contains(suggestiveSearchET.text.toString()) || TextUtils.isEmpty(suggestiveSearchET.text.toString())) {
                    textData = suggestiveSearchET.text.toString()
                    doAsync {
                        if (!textData.isBlank()) {
                            val data = Suggestion(textData, from, bdjobsUserSession.userId, Date())
                            bdjobsInternalDB.suggestionDAO().insertSuggestion(data)
                        }
                    }
                    onBackPressed()
                } else {
                    toast("Please select a valid category!")
                }
            }

            key_special_categoryET -> {
                if (suggestionList.contains(suggestiveSearchET.text.toString()) || TextUtils.isEmpty(suggestiveSearchET.text.toString())) {
                    textData = suggestiveSearchET.text.toString()
                    doAsync {
                        if (!textData.isBlank()) {
                            val data = Suggestion(textData, from, bdjobsUserSession.userId, Date())
                            bdjobsInternalDB.suggestionDAO().insertSuggestion(data)
                        }
                    }
                    onBackPressed()
                } else {
                    toast("Please select a valid category!")
                }
            }
            key_newspaperET -> {
                if (suggestionList.contains(suggestiveSearchET.text.toString()) || TextUtils.isEmpty(suggestiveSearchET.text.toString())) {
                    textData = suggestiveSearchET.text.toString()
                    doAsync {
                        if (!textData.isBlank()) {
                            val data = Suggestion(textData, from, bdjobsUserSession.userId, Date())
                            bdjobsInternalDB.suggestionDAO().insertSuggestion(data)
                        }
                    }
                    onBackPressed()
                } else {
                    toast("Please select a valid NewsPaper!")
                }
            }

            key_industryET -> {
                if (suggestionList.contains(suggestiveSearchET.text.toString()) || TextUtils.isEmpty(suggestiveSearchET.text.toString())) {
                    textData = suggestiveSearchET.text.toString()
                    doAsync {
                        if (!textData.isBlank()) {
                            val data = Suggestion(textData, from, bdjobsUserSession.userId, Date())
                            bdjobsInternalDB.suggestionDAO().insertSuggestion(data)
                        }
                    }
                    onBackPressed()
                } else {
                    toast("Please select a valid Industry!")
                }
            }

        }
    }


    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.hint_voiceInput))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            toast("Sorry! Your device doesn't support speech input")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    suggestiveSearchET.setText(result?.get(0))
                    suggestiveSearchET.setSelection(suggestiveSearchET?.text?.length!!)
                    takeDecision()
                }
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(key_typedData, textData)
        intent.putExtra(key_from, from)
        setResult(Activity.RESULT_OK, intent)
        window.exitTransition = null
        super.onBackPressed()
    }

    override fun suggestionSelected(selectedItem: String) {
        debug(selectedItem)
        suggestiveSearchET?.setText(selectedItem)
        takeDecision()
    }
}

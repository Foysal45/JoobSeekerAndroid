package com.bdjobs.app.SuggestiveSearch

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.Suggestion
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.REQ_CODE_SPEECH_INPUT
import com.bdjobs.app.Utilities.Constants.Companion.key_categoryET
import com.bdjobs.app.Utilities.Constants.Companion.key_from
import com.bdjobs.app.Utilities.Constants.Companion.key_jobtitleET
import com.bdjobs.app.Utilities.Constants.Companion.key_loacationET
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
    private var adapter: SuggestionAdapter? = null
    private lateinit var bdjobsInternalDB: BdjobsDB
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private val historyList = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestive_search)

        initialization()
        onClicks()
        getIntentData()
        setHistoryAdapter(from)
        setFilterAdapter(from)
        setTextWatcher()
    }

    private fun initialization() {
        dataStorage = DataStorage(applicationContext)
        bdjobsInternalDB = BdjobsDB.getInstance(applicationContext)
        bdjobsUserSession = BdjobsUserSession(this)
    }

    private fun setTextWatcher() {

        suggestiveSearchET.easyOnTextChangedListener { e: CharSequence ->
            if (TextUtils.isEmpty(e)) {
                suggestiveSearchET.setCompoundDrawablesWithIntrinsicBounds(0, 0,  0, 0)
                filterRV.hide()
                historyViewCL.show()
            } else {
                suggestiveSearchET.setCompoundDrawablesWithIntrinsicBounds(0, 0,  R.drawable.ic_close_white, 0)
                suggestiveSearchET.clearTextOnDrawableRightClick()
                filterRV.show()
                historyViewCL.hide()
            }
            adapter?.filter?.filter(e)
        }
        suggestiveSearchET.setText(textData)
        suggestiveSearchET.setSelection(suggestiveSearchET?.text?.length!!)
    }

    private fun setHistoryAdapter(from: String) {
        doAsync {
            val suggestions = bdjobsInternalDB.suggestionDAO().getSuggestionByFlag(from)!!
            for (history in suggestions) {
                historyList.add(history.suggestion)
            }
            uiThread {
                if (historyList.size == 0) {
                    lineView.hide()
                    clearAllBTN.hide()
                }
                historyAdapter = HistoryAdapter(historyList, this@SuggestiveSearchActivity)
                historyRV.layoutManager = LinearLayoutManager(this@SuggestiveSearchActivity)
                historyRV.adapter = historyAdapter
                historyRV.recycledViewPool.clear();
                historyAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setFilterAdapter(from: String) {
        lateinit var suggestionItems: Array<String>
        when (from) {
            key_jobtitleET -> {
                suggestiveSearchET.hint = getString(R.string.hint_keyword)
                suggestionItems = dataStorage.allSkills
            }
            key_loacationET -> {
                suggestiveSearchET.hint = getString(R.string.hint_location)
                suggestionItems = dataStorage.allDomesticLocations
            }
            key_categoryET -> {
                suggestiveSearchET.hint = getString(R.string.hint_Category)
                suggestionItems = dataStorage.allWhiteCollarCategories.toTypedArray()
            }
        }

        for (item in suggestionItems!!) {
            suggestionList.add(item)
        }
        adapter = SuggestionAdapter(suggestionList, this)
        filterRV.layoutManager = LinearLayoutManager(this@SuggestiveSearchActivity)
        filterRV.adapter = adapter
    }

    private fun getIntentData() {
        from = intent.getStringExtra(key_from)
        textData = intent.getStringExtra(key_typedData)
    }

    private fun onClicks() {
        backIMGV.setOnClickListener { onBackPressed() }

        suggestiveSearchET.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->

            if (event.getAction() === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                takeDecision()
                return@OnKeyListener true
            }
            false
        })


        filterIMGV.setOnClickListener { promptSpeechInput() }
        clearAllBTN.setOnClickListener {
            clearHistory(from)
        }

    }

    private fun clearHistory(from: String) {
        doAsync {
            bdjobsInternalDB.suggestionDAO().deleteAllKeywordSuggestion(from)
            uiThread {
                historyList.clear()
                historyRV.recycledViewPool.clear();
                historyAdapter.notifyDataSetChanged()
                lineView.hide()
                clearAllBTN.hide()
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
                    suggestiveSearchET.setText(result[0])
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
        suggestiveSearchET.setText(selectedItem)
        takeDecision()
    }
}

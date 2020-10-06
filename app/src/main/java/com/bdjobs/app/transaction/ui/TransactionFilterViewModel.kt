package com.bdjobs.app.transaction.ui

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel



class TransactionFilterViewModel(application: Application) : AndroidViewModel(application) {
    var startDateNTime = String()
    var endDateNTime = String()
    var transactionType = String()
    fun onTextChangeType(editable: Editable?) {
        transactionType = editable.toString()
    }


}
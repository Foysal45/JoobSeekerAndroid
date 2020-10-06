package com.bdjobs.app.transaction.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class TransactionFilterModelFactory( private val application: Application)  :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TransactionFilterViewModel(application) as T
    }


}
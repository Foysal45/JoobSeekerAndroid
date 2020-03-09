package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.assessment.models.CertificateData


class ResultViewModelFactory(
        private val certificateData: CertificateData,
        private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(certificateData, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


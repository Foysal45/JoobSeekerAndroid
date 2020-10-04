package com.bdjobs.app.transaction.ui

import android.app.Application
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber


class TransactionFilterViewModel(application: Application) : AndroidViewModel(application) {


    private val context = getApplication<Application>().applicationContext
    private val typeArray = arrayOf("Employability Assessment", "SMS Job Alert")


    private val _filterType = MutableLiveData<String>()
    val filterType: MutableLiveData<String>
        get() = _filterType



    init {


    }

     fun showToast() {
        Log.d("lkhmlhkm", "Clicked")
        Toast.makeText(context, "test toast", Toast.LENGTH_SHORT).show()
       /* context.selector("Select Transaction type", typeArray.toList()) { _, i ->


            _filterType.value = typeArray[i]


        }*/

    }

    fun showPackageType(edittext: TextInputEditText) {




    }


    fun onImageTouch(event: MotionEvent?): Boolean {
        Timber.e("OnImageTouch is called")
        Log.d("lkhmlhkm", "Clicked")
        return true
    }

}
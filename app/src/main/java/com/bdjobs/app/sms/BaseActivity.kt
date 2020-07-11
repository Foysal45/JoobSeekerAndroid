package com.bdjobs.app.sms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bdjobs.app.R

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
}
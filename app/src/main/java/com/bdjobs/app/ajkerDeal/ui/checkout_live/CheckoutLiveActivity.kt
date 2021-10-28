package com.bdjobs.app.ajkerDeal.ui.checkout_live

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.utilities.hideKeyboard
import com.bdjobs.app.databinding.ActivityCheckoutLiveBinding
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber

@SuppressLint("SetTextI18n")
class CheckoutLiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutLiveBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.navHostFragment)
        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds = setOf(), fallbackOnNavigateUpListener = ::onSupportNavigateUp)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.d("navigationDebug onSupportNavigateUp called")
        finish()
        return true // navigation successful
        //return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        Timber.d("navigationDebug onBackPressed called")
        finish()
        /*if (navController.currentDestination?.id != R.id.nav_checkout) {
            super.onBackPressed()
        }*/
    }

    private val fallbackListener = AppBarConfiguration.OnNavigateUpListener {
        onBackPressed()
        true
    }

    fun updateToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is TextInputEditText || view is EditText) {
                if (!isPointInsideView(event.rawX, event.rawY, view)) {
                    view.clearFocus()
                    hideKeyboard(view)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun isPointInsideView(x: Float, y: Float, view: View): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewX = location[0]
        val viewY = location[1]

        // point is inside view bounds
        return ((x > viewX && x < (viewX + view.width)) && (y > viewY && y < (viewY + view.height)))
    }
}
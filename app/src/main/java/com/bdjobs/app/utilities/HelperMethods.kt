package com.bdjobs.app.utilities

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun showSnackBar(root: View, message: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(root, message, length).show()
}
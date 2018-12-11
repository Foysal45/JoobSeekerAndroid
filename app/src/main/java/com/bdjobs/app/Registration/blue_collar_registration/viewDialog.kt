package com.bdjobs.app.Registration.blue_collar_registration

import android.R.drawable.btn_dialog
import android.widget.TextView
import android.view.Window.FEATURE_NO_TITLE
import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.Button
import com.bdjobs.app.R


class viewDialog {

    fun showDialog(activity: Activity, msg: String) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
       /* dialog.setContentView(R.layout.dialog)

        val text = dialog.findViewById(R.id.text_dialog) as TextView
        text.text = msg

        val dialogButton = dialog.findViewById(R.id.btn_dialog) as Button
        dialogButton.setOnClickListener(object : View.OnClickListener() {
            fun onClick(v: View) {
                dialog.dismiss()
            }
        })

        dialog.show()*/

    }

}
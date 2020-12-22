package com.bdjobs.app.transaction

import android.content.Context
import android.view.View
import androidx.databinding.BindingAdapter
import com.loopj.android.http.AsyncHttpClient.log

@BindingAdapter("android:Click")
fun setOnClick(view: View, context: Context){
       log.d("safnha", "sngsgn")


}


@BindingAdapter("onClick")
fun onImageTouch(view: View, onTouchListener: View.OnClickListener?) {
        view.setOnClickListener(onTouchListener)
}

@BindingAdapter("onImageTouch")
fun onImageTouch(view: View, onTouchListener: View.OnTouchListener?) {
        view.setOnTouchListener(onTouchListener)
}

@BindingAdapter("android:onClick", "android:clickable")
fun setOnClick(view: View, clickListener: View.OnClickListener?,
               clickable: Boolean) {
    view.setOnClickListener(clickListener)
    view.isClickable = clickable
}
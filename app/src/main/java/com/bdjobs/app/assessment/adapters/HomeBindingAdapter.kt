package com.bdjobs.app.assessment.adapters

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bdjobs.app.R
import com.bdjobs.app.assessment.models.HomeData
import com.google.android.material.button.MaterialButton
import org.jetbrains.anko.textColor
import java.text.ParseException
import java.text.SimpleDateFormat

@BindingAdapter("paymentStatusBackground")
fun bindStatusBackground(constraintLayout: ConstraintLayout, homeData: HomeData?) {

    homeData?.let {
        if (homeData.isTestFree.equals("0")) {
            when (homeData.paymentStatus) {
                "Paid" -> constraintLayout.background = ContextCompat.getDrawable(constraintLayout.context, R.drawable.paid_background)
                "Unpaid" -> constraintLayout.background = ContextCompat.getDrawable(constraintLayout.context, R.drawable.unpaid_background)
                //else -> constraintLayout.visibility = View.GONE
            }
        } else {
            constraintLayout.background = ContextCompat.getDrawable(constraintLayout.context, R.drawable.first_time_background)
        }
    }
}

@BindingAdapter("paymentStatusImage")
fun bindStatusImage(imageView: ImageView, homeData: HomeData?) {

    homeData?.let {
        if (homeData.isTestFree.equals("0")) {
            when (homeData.paymentStatus) {
                "Paid" -> imageView.background = ContextCompat.getDrawable(imageView.context, R.drawable.ic_paid)
                "Unpaid" -> imageView.background = ContextCompat.getDrawable(imageView.context, R.drawable.ic_unpaid)
               // else -> imageView.visibility = View.GONE
            }
        } else {
            imageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("paymentStatusText")
fun bindStatusText(textView: TextView, homeData: HomeData?) {

    homeData?.let {
        if (homeData.isTestFree.equals("0")) {
            when (homeData.paymentStatus) {
                "Paid" -> textView.apply {
                    text = "Paid"
                    textColor = Color.parseColor("#155724")
                }
                else -> textView.apply {
                    text = "Unpaid"
                    textColor = Color.parseColor("#721C24")
                }
                //else -> textView.visibility = View.GONE
            }
        } else {
            textView.apply {
                text = "FREE for 1st test"
                textColor = Color.parseColor("#B11176")
            }
        }
    }
}

@BindingAdapter("venue")
fun bindVenueText(textView: TextView, venue: String?) {
    venue?.let {
        textView.text = HtmlCompat.fromHtml(venue, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

@BindingAdapter("date")
fun bindExamDate(textView: TextView, date: String?) {
    date?.let {
        Log.d("rakib", "$it")
//        var originalFormat: DateFormat = SimpleDateFormat("MM/dd/YYYY", Locale.US)
//        var targetFormat: DateFormat = SimpleDateFormat("dd MMM YYYY")
//
//        if (it != "") {
//            var formattedDate: Date = originalFormat.parse(it)
//            textView.text = targetFormat.format(formattedDate)
//        }
        textView.text = date
    }
}



@BindingAdapter("fee")
fun bindFeeText(textView: TextView, fee: String?) {
    fee?.let {
        if (it != "")
            textView.text = "BDT $fee/-"
    }
}

@BindingAdapter("cancelButtonBehaviour")
fun bindSecondButtonBehaviour(button: MaterialButton, homeData: HomeData?) {
    homeData?.let {
        button.text = when (homeData.resumeTestBtnFormat) {
            "1" -> "Take Test from home"
            "2" -> "Start Test"
            "3" -> "Resume Test"
            "4" -> "Pay Online"
            else -> "Pay Online"
        }
    }
}

@BindingAdapter("takeNewTestVisibility")
fun bindTakeNewTestCardVisibility(constraintLayout: ConstraintLayout, homeData: HomeData?) {

    homeData?.let {
        if (homeData.isUserPermittedForSchldBooking.equals("1") && homeData.isProceedForNewTest.equals("1")) {
            constraintLayout.visibility = View.VISIBLE
        } else {
            constraintLayout.visibility = View.GONE
        }
    }
}

@BindingAdapter("testDataVisibility")
fun bindTestDataVisibility(constraintLayout: ConstraintLayout, homeData: HomeData?) {

    homeData?.let {
        if (homeData.isUserPermittedForSchldBooking.equals("1") && homeData.isProceedForNewTest.equals("0")) {
            constraintLayout.visibility = View.VISIBLE
        } else {
            constraintLayout.visibility = View.GONE
        }
    }
}

@BindingAdapter("firstTimeButtonVisibility")
fun bindFirstTimeTestButtonVisibility(constraintLayout: ConstraintLayout, homeData: HomeData?) {
    homeData?.let {
        if (homeData.isUserFirstTimeInAssessmentPanel.equals("0")) {
            constraintLayout.visibility = View.GONE
        } else {
            constraintLayout.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("firstTime")
fun bindFirstTimeView(scrollView: ScrollView, homeData: HomeData?) {
    homeData?.let {
        if (homeData.isUserFirstTimeInAssessmentPanel.equals("0"))
            scrollView.visibility = View.GONE
        else
            scrollView.visibility = View.VISIBLE
    }
}

@BindingAdapter("notFirstTime")
fun bindNotFirstTimeView(scrollView: ScrollView, homeData: HomeData?) {
    homeData?.let {
        if (homeData.isUserFirstTimeInAssessmentPanel.equals("0"))
            scrollView.visibility = View.VISIBLE
        else
            scrollView.visibility = View.GONE
    }
}

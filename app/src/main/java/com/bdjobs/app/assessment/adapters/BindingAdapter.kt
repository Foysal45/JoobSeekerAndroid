package com.bdjobs.app.assessment.adapters

import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.*
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.jetbrains.anko.textColor

@BindingAdapter("list")
fun bindPostsRecyclerView(recyclerView: RecyclerView, data: List<CertificateData?>?) {
    val adapter = recyclerView.adapter as CertificateListAdapter
    data?.let {
        adapter.submitList(data)
    }
}

@BindingAdapter("scheduleList", "scheduleStatus")
fun bindScheduleList(recyclerView: RecyclerView, data: List<ScheduleData?>?, status: Status?) {
//    data?.let {
//        adapter.submitList(data)
//    }

    when (status) {
        Status.LOADING -> {
            recyclerView.visibility = View.GONE
        }

        Status.DONE -> {
            if (data == null) {
                recyclerView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.VISIBLE
                val adapter = recyclerView.adapter as ScheduleListAdapter
                adapter.submitList(data)
            }
        }

        Status.ERROR -> {
            recyclerView.visibility = View.GONE
        }
    }
}

@BindingAdapter("emptyView", "status")
fun bindEmptyView(constraintLayout: ConstraintLayout, data: List<ScheduleData?>?, status: Status?) {

    when (status) {
        Status.LOADING -> {
            constraintLayout.visibility = View.GONE
        }

        Status.DONE -> {
            if (data == null) {
                constraintLayout.visibility = View.VISIBLE
            } else {
                constraintLayout.visibility = View.GONE
            }
        }

        Status.ERROR -> {
            constraintLayout.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("scoreText")
fun bindScoreTextView(textView: TextView, result: ResultData?) {
    result?.let {
        val spannable = SpannableString("Total Score: ${result.totalScore}")
        spannable.setSpan(
                StyleSpan(BOLD),
                13, spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable
    }
}

@BindingAdapter("totalCertificates")
fun bindTotalCertificatesTextView(textView: TextView, certificateList: List<CertificateData?>?) {

    if (certificateList.isNullOrEmpty())
        textView.visibility = View.GONE
    else {
        textView.visibility = View.VISIBLE
        textView.text = "My Certificate List (${certificateList.size} Certificates)"
    }
}

@BindingAdapter("graph")
fun bindGraph(chart: HorizontalBarChart, moduleWiseScore: List<ModuleWiseScore?>?) {
    moduleWiseScore?.let {

        chart.setDrawBarShadow(false)

        val description = Description()
        description.text = ""

        chart.legend.isEnabled = false

        chart.setPinchZoom(true)

        chart.isDoubleTapToZoomEnabled = false

        chart.setDrawValueAboveBar(false)

        //Display the axis on the left (contains the labels 1*, 2* and so on)
        val xAxis = chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.isEnabled = true
        xAxis.setDrawAxisLine(true)
        xAxis.isAvoidFirstLastClippingEnabled
        xAxis.gridColor = Color.parseColor("#004445")


        val yLeft = chart.axisLeft

        //Set the minimum and maximum bar lengths as per the values that they represent
        yLeft.axisMaximum = 900f
        yLeft.axisMinimum = 0f
        yLeft.isEnabled = false
        yLeft.axisLineColor = Color.parseColor("#004445")

        //Set label count to 5 as we are displaying 5 star rating
        xAxis.labelCount = moduleWiseScore.size

        val subjectNameList = ArrayList<String>()
        moduleWiseScore?.forEach {
            subjectNameList.add(it?.moduleName!!)
        }

        try {
            xAxis.valueFormatter = IndexAxisValueFormatter(subjectNameList)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val yRight = chart.axisRight
        yRight.setDrawAxisLine(true)
        yRight.setDrawGridLines(false)
        yRight.isEnabled = true
        yRight.axisMaximum = 900f
        yRight.axisMinimum = 0f
        yRight.isGranularityEnabled = true
        yRight.granularity = 1f
        yRight.axisLineColor = Color.parseColor("#004445")
        yRight.zeroLineColor = Color.parseColor("#004445")

        yRight.setDrawZeroLine(true)

        //Set bar entries and add necessary formatting
        //setGraphData()

        val entries = ArrayList<BarEntry>()

        for (i in moduleWiseScore.indices) {
            entries.add(BarEntry(i.toFloat(), moduleWiseScore[i]?.moduleWiseScore!!.toFloat()))
        }

        val barDataSet = BarDataSet(entries, "Bar Data Set")

        chart.setDrawBarShadow(true)
        barDataSet.barShadowColor = Color.argb(0, 150, 150, 150)
        barDataSet.color = Color.parseColor("#2C786C")

        val data = BarData(barDataSet)

        //Set the bar width
        //Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.barWidth = .35f

        //Finally set the data and refresh the graph

        data?.let {
            chart.data = data
            chart.invalidate()
        }


        //Add animation to the graph
        chart.animateY(1000)
    }
}


@BindingAdapter("certificateStatus", "data")
fun bindCertificateStatus(constraintLayout: ConstraintLayout, status: Status, certificateList: List<CertificateData?>?) {
    when (status) {
        Status.LOADING -> constraintLayout.visibility = View.INVISIBLE
        Status.DONE -> {
            if (certificateList.isNullOrEmpty())
                constraintLayout.visibility = View.VISIBLE
            else
                constraintLayout.visibility = View.GONE
        }
        Status.ERROR -> constraintLayout.visibility = View.GONE
    }
}

package com.bdjobs.app.assessment.adapters

import android.graphics.Canvas
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
import com.bdjobs.app.R
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.CertificateData
import com.bdjobs.app.assessment.models.ModuleWiseScore
import com.bdjobs.app.assessment.models.ResultData
import com.bdjobs.app.assessment.models.ScheduleData
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler


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
//                adapter.notifyDataSetChanged()
                adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onChanged() {
                        recyclerView.scrollToPosition(0)
                    }

                    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                        recyclerView.scrollToPosition(0)
                    }

                    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                        recyclerView.scrollToPosition(0)
                    }

                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        recyclerView.scrollToPosition(0)
                    }

                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                        recyclerView.scrollToPosition(0)
                    }

                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                        recyclerView.scrollToPosition(0)
                    }
                })

                adapter.notifyDataSetChanged()
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

    //Log.d("rakib", "empty view called $data")

    when (status) {
        Status.LOADING -> {
            constraintLayout.visibility = View.GONE
        }

        Status.DONE -> {
            if (data.isNullOrEmpty()) {
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
        if (textView.id != R.id.heading_tv)
            textView.text = if (certificateList.size > 1) "(${certificateList.size} Certificates)" else "(${certificateList.size} Certificate)"
        else
        {
            if (certificateList.isNotEmpty())
                textView.visibility = View.VISIBLE
            else
                textView.visibility = View.GONE

        }
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
        xAxis.axisLineColor = Color.parseColor("#004445")

        xAxis.axisLineWidth = 1.5f

        val yLeft = chart.axisLeft

        //Set the minimum and maximum bar lengths as per the values that they represent
        yLeft.axisMaximum = 900f
        yLeft.axisMinimum = 0f
        yLeft.isEnabled = false
        yLeft.axisLineColor = Color.parseColor("#004445")

        yLeft.gridColor = Color.parseColor("#004445")

        //Set label count to 5 as we are displaying 5 star rating
        xAxis.labelCount = moduleWiseScore.size

        val subjectNameList = ArrayList<String>()
        moduleWiseScore.forEach {
            subjectNameList.add(it?.moduleName!!)
        }

        try {
            xAxis.valueFormatter = IndexAxisValueFormatter(subjectNameList)
            //chart.setXAxisRenderer(CustomXAxisRenderer(chart.viewPortHandler, chart.xAxis, chart.getTransformer(YAxis.AxisDependency.LEFT)))
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
        yRight.axisLineWidth = 1.5f

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

        data.let {
            chart.data = data
            chart.invalidate()
        }

        chart.description.isEnabled = false

        chart.legend.isEnabled = false

        barDataSet.valueTextColor = Color.parseColor("#FFFFFF")
        barDataSet.valueTextSize = 8f

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

class CustomXAxisRenderer(viewPortHandler: ViewPortHandler?, xAxis: XAxis?, trans: Transformer?) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    override fun drawLabel(c: Canvas?, formattedLabel: String, x: Float, y: Float, anchor: MPPointF?, angleDegrees: Float) {
        try {
            val line = formattedLabel.split("\n").toTypedArray()
            Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees)
            Utils.drawXAxisValue(c, line[1], x + mAxisLabelPaint.textSize, y + mAxisLabelPaint.textSize, mAxisLabelPaint, anchor, angleDegrees)
        } catch (e: Exception) {
        }
    }
}

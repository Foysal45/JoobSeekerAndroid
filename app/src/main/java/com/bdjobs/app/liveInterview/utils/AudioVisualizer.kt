package com.bdjobs.app.liveInterview.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class VisualizerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var amplitudes: FloatArray?=null
    private var vectors: FloatArray?=null
    private var insertIdx = 0
    private val pointPaint: Paint
    private val linePaint: Paint = Paint()
    private var width1 = 0
    private var height1 = 0

    override fun onSizeChanged(width: Int, h: Int, oldw: Int, oldh: Int) {
        this.width1 = width
        height1 = h
        amplitudes = FloatArray(this.width1 * 2) // xy for each point across the width
        vectors = FloatArray(this.width1 * 4) // xxyy for each line across the width
    }

    /**
     * modifies draw arrays. cycles back to zero when amplitude samples reach max screen size
     */
    fun addAmplitude(amplitude: Int) {
        invalidate()
        val scaledHeight = amplitude.toFloat() / MAX_AMPLITUDE * (height1 - 1)
        var ampIdx = insertIdx * 2
        amplitudes!![ampIdx++] = insertIdx.toFloat() // x
        amplitudes!![ampIdx] = scaledHeight // y
        var vectorIdx = insertIdx * 4
        vectors!![vectorIdx++] = insertIdx.toFloat() // x0
        vectors!![vectorIdx++] = 0F // y0
        vectors!![vectorIdx++] = insertIdx.toFloat() // x1
        vectors!![vectorIdx] = scaledHeight // y1
        // insert index must be shorter than screen width
        insertIdx = if (++insertIdx >= width1) 0 else insertIdx
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawLines(vectors!!, linePaint)
        canvas.drawPoints(amplitudes!!, pointPaint)
    }

    companion object {
        private const val MAX_AMPLITUDE = 32767
    }

    init {
        linePaint.color = Color.parseColor("#007FE3")
        linePaint.strokeWidth = 1F
        pointPaint = Paint()
        pointPaint.color = Color.parseColor("#007FE3")
        pointPaint.strokeWidth = 1F
    }
}
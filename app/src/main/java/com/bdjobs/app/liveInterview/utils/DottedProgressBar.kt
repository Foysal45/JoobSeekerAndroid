package com.bdjobs.app.liveInterview.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import timber.log.Timber

class DottedProgressBar : View {
    //actual dot radius
    private val mDotRadius = 10F

    //Bounced Dot Radius
    private val mBounceDotRadius = 8

    //to get identified in which position dot has to bounce
    private var mDotPosition = 0

    //specify how many dots you need in a progressbar
    private val mDotAmount = 10

    private var mNumOfColoredDot = 0
        get() = field
        set(value) {
            field = value
        }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    //Method to draw your customized dot on the canvas
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()
        val bluePaint = Paint()

        //set the color for the dot that you want to draw
        paint.color = Color.parseColor("#CCCCCC")
        bluePaint.color = Color.parseColor("#007FE3")

        //function to create dot
        createDot(canvas, paint)
        createBlueDot(canvas,bluePaint)
    }

    fun createBlueDot(canvas: Canvas, bluePaint: Paint) {

    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //Animation called when attaching to the window, i.e to your screen
        startAnimation()
    }

    private fun createDot(canvas: Canvas, paint: Paint) {

        //here i have setted progress bar with 10 dots ,
        // so repeat and wnen i = mDotPosition  then increase the radius of dot i.e mBounceDotRadius
        for (i in 0 until mDotAmount) {
            if (i == mDotPosition) {
                paint.color = Color.parseColor("#007FE3")
                canvas.drawCircle(10F + i * 20F, mBounceDotRadius.toFloat(), mBounceDotRadius.toFloat(), paint)
            } else {
                paint.color = Color.parseColor("#CCCCCC")
                canvas.drawCircle(10F + i * 20F, mBounceDotRadius.toFloat(), mDotRadius, paint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width: Int

        //calculate the view width
        val calculatedWidth = 20 * 9
        width = calculatedWidth
        val height = mBounceDotRadius * 2


        //MUST CALL THIS
        setMeasuredDimension(width, height)
    }

    private fun startAnimation() {
        val bounceAnimation = BounceAnimation()
        bounceAnimation.duration = 100
        bounceAnimation.repeatCount = Animation.INFINITE
        bounceAnimation.interpolator = LinearInterpolator()
        bounceAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {
                mDotPosition++
                //when mDotPosition == mDotAmount , then start again applying animation from 0th positon , i.e  mDotPosition = 0;
                if (mDotPosition == mDotAmount) {
                    mDotPosition = 0
                }
                Timber.d("----On Animation Repeat----")
            }
        })
        startAnimation(bounceAnimation)
    }

    private inner class BounceAnimation : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
            //call invalidate to redraw your view againg.
            invalidate()
        }
    }
}


/*
package com.bdjobs.app.liveInterview.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.bdjobs.app.R


/**
 * Created by Soumik on 5/06/20.
 */
class DottedProgressBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var mDotSize = 0f
    private var mSpacing = 0f
    private var mJumpingSpeed = 0
    private var mEmptyDotsColor = 0
    private var mActiveDotColor = 0
    private var mActiveDot: Drawable? = null
    private var mInactiveDot: Drawable? = null
    private var isInProgress: Boolean
    private var isActiveDrawable = false
    private var isInactiveDrawable = false
    private var mActiveDotIndex = 0
    private var mNumberOfDots = 0
    private var mPaint: Paint? = null
    private var mPaddingLeft = 0
    private val mHandler: Handler = Handler(Looper.myLooper()!!)
    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mNumberOfDots != 0) mActiveDotIndex = (mActiveDotIndex + 1) % mNumberOfDots
            this@DottedProgressBar.invalidate()
            mHandler.postDelayed(this, mJumpingSpeed.toLong())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until mNumberOfDots) {
            val x = (paddingLeft + mPaddingLeft + mSpacing / 2 + i * (mSpacing + mDotSize)).toInt()
            if (isInactiveDrawable) {
                mInactiveDot!!.setBounds(x, paddingTop, (x + mDotSize).toInt(), paddingTop + mDotSize.toInt())
                mInactiveDot!!.draw(canvas)
            } else {
                mPaint!!.color = mEmptyDotsColor
                canvas.drawCircle(x + mDotSize / 2,
                        paddingTop + mDotSize / 2, mDotSize / 2, mPaint!!)
            }
        }
        if (isInProgress) {
            val x = (paddingLeft + mPaddingLeft + mSpacing / 2 + mActiveDotIndex * (mSpacing + mDotSize)).toInt()
            if (isActiveDrawable) {
                mActiveDot!!.setBounds(x, paddingTop, (x + mDotSize).toInt(), paddingTop + mDotSize.toInt())
                mActiveDot!!.draw(canvas)
            } else {
                mPaint!!.color = mActiveDotColor
                canvas.drawCircle(x + mDotSize / 2,
                        paddingTop + mDotSize / 2, mDotSize / 2, mPaint!!)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        val widthWithoutPadding = parentWidth - paddingLeft - paddingRight
        val heigthWithoutPadding = parentHeight - paddingTop - paddingBottom

        //setMeasuredDimension(parentWidth, calculatedHeight);
        val calculatedHeight = paddingTop + paddingBottom + mDotSize.toInt()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(parentWidth, calculatedHeight)
        mNumberOfDots = calculateDotsNumber(widthWithoutPadding)
    }

    private fun calculateDotsNumber(width: Int): Int {
        val number = (width / (mDotSize + mSpacing)).toInt()
        mPaddingLeft = (width % (mDotSize + mSpacing) / 2).toInt()
        //setPadding(getPaddingLeft() + (int) mPaddingLeft, getPaddingTop(), getPaddingRight() + (int) mPaddingLeft, getPaddingBottom());
        return number
    }

    fun startProgress() {
        isInProgress = true
        mActiveDotIndex = -1
        mHandler.removeCallbacks(mRunnable)
        mHandler.post(mRunnable)
    }

    fun stopProgress() {
        isInProgress = false
        mHandler.removeCallbacks(mRunnable)
        invalidate()
    }

    init {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.DottedProgressBar,
                0, 0)
        isInProgress = false
        try {
//            mEmptyDotsColor = a.getColor(R.styleable.DottedProgressBar_emptyDotsColor, Color.WHITE);
//            mActiveDotColor = a.getColor(R.styleable.DottedProgressBar_activeDotColor, Color.BLUE);
            val value = TypedValue()
            a.getValue(R.styleable.DottedProgressBar_activeDot, value)
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                // It's a color
                isActiveDrawable = false
                mActiveDotColor = resources.getColor(value.resourceId)
            } else if (value.type == TypedValue.TYPE_STRING) {
                // It's a reference, hopefully to a drawable
                isActiveDrawable = true
                mActiveDot = resources.getDrawable(value.resourceId)
            }
            a.getValue(R.styleable.DottedProgressBar_inactiveDot, value)
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                // It's a color
                isInactiveDrawable = false
                mEmptyDotsColor = resources.getColor(value.resourceId)
            } else if (value.type == TypedValue.TYPE_STRING) {
                // It's a reference, hopefully to a drawable
                isInactiveDrawable = true
                mInactiveDot = resources.getDrawable(value.resourceId)
            }
            mDotSize = a.getDimensionPixelSize(R.styleable.DottedProgressBar_dotSize, 5).toFloat()
            mSpacing = a.getDimensionPixelSize(R.styleable.DottedProgressBar_spacing, 10).toFloat()
            mActiveDotIndex = a.getInteger(R.styleable.DottedProgressBar_activeDotIndex, 0)
            mJumpingSpeed = a.getInt(R.styleable.DottedProgressBar_jumpingSpeed, 500)
            mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            mPaint!!.style = Paint.Style.FILL
        } finally {
            a.recycle()
        }
    }
}
 */
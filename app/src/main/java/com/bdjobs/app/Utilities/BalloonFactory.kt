package com.bdjobs.app.Utilities

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import com.bdjobs.app.R
import com.skydoves.balloon.ArrowConstraints
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon

class BalloonFactory : Balloon.Factory() {
    override fun create(context: Context, lifecycle: LifecycleOwner?): Balloon {
        return createBalloon(context) {
            setArrowSize(10)
            setWidthRatio(1.0f)
            setArrowPosition(0.00f)
            setCornerRadius(8f)
            setHeight(100)
            setMarginLeft(4)
            setMarginRight(20)
            setPadding(16)
            setAlpha(0.9f)
            setText("To participate in the online interview please use updated mobile browser(Chrome, Mozilla) or you can access web from desktop / laptop.")
            setTextColorResource(R.color.colorWhite)
            arrowConstraints = ArrowConstraints.ALIGN_ANCHOR
            setBackgroundColorResource(R.color.black)
            setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            setLifecycleOwner(lifecycleOwner)
        }
    }
}
package at.shockbytes.corey.common.core.util.view

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View

object CoreyViewManager {

    fun backgroundColorTransition(v: View, from: Int, to: Int) {

        val anim = ValueAnimator.ofFloat(0.toFloat(), 1.toFloat())
        anim.addUpdateListener { animation ->
            // Get current position
            val position = animation.animatedFraction

            // Blend colors and apply to view
            val blended = blendColors(from, to, position)
            v.background = ColorDrawable(blended)
        }
        anim.setDuration(250).start()
    }

    private fun blendColors(from: Int, to: Int, ratio: Float): Int {

        val inverseRatio = 1f - ratio

        val r = Color.red(to) * ratio + Color.red(from) * inverseRatio
        val g = Color.green(to) * ratio + Color.green(from) * inverseRatio
        val b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio

        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }
}
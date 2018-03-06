package at.shockbytes.corey.common.core.util.view

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.animation.OvershootInterpolator

object CoreyViewManager {

    fun animateDoubleTap(v: View) {

        val action4 = Runnable {
            //Mouse click up
            v.animate().scaleX(1f).scaleY(1f).rotationX(0f)
                    .setStartDelay(0).setDuration(200)
                    .setInterpolator(OvershootInterpolator(2f))
                    .start()
        }
        val action3 = Runnable {
            //Mouse click down
            v.animate().scaleX(0.8f).scaleY(0.8f).rotationX(20f)
                    .setDuration(200).setStartDelay(30)
                    .setInterpolator(OvershootInterpolator(2f))
                    .withEndAction(action4).start()
        }

        val action2 = Runnable {
            //Mouse click up
            v.animate().scaleX(1f).scaleY(1f).rotationX(0f)
                    .setStartDelay(0).setDuration(70)
                    .setInterpolator(OvershootInterpolator(2f))
                    .withEndAction(action3).start()
        }
        val action1 = Runnable {
            //Mouse click down
            v.animate().scaleX(0.8f).scaleY(0.8f).rotationX(20f)
                    .setDuration(200)
                    .setInterpolator(OvershootInterpolator(2f))
                    .withEndAction(action2).start()
        }

        //Start all animations, starting with this one
        v.animate().alpha(1f)
                .setStartDelay(300)
                .setInterpolator(OvershootInterpolator(2f))
                .withEndAction(action1).start()
    }

    fun backgroundColorTransition(v: View, from: Int, to: Int) {

        val anim = ValueAnimator.ofFloat(0.toFloat(), 1.toFloat())
        anim.addUpdateListener { animation ->
            //Get current position
            val position = animation.animatedFraction

            //Blend colors and apply to view
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

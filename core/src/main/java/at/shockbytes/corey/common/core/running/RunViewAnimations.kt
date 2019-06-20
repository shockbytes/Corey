package at.shockbytes.corey.common.core.running

import android.view.View
import android.view.animation.OvershootInterpolator

object RunViewAnimations {

    fun animateDoubleTap(v: View) {

        val action4 = Runnable {
            // Mouse click up
            v.animate().scaleX(1f).scaleY(1f).rotationX(0f)
                .setStartDelay(0).setDuration(200)
                .setInterpolator(OvershootInterpolator(2f))
                .start()
        }
        val action3 = Runnable {
            // Mouse click down
            v.animate().scaleX(0.8f).scaleY(0.8f).rotationX(20f)
                .setDuration(200).setStartDelay(30)
                .setInterpolator(OvershootInterpolator(2f))
                .withEndAction(action4).start()
        }

        val action2 = Runnable {
            // Mouse click up
            v.animate().scaleX(1f).scaleY(1f).rotationX(0f)
                .setStartDelay(0).setDuration(70)
                .setInterpolator(OvershootInterpolator(2f))
                .withEndAction(action3).start()
        }
        val action1 = Runnable {
            // Mouse click down
            v.animate().scaleX(0.8f).scaleY(0.8f).rotationX(20f)
                .setDuration(200)
                .setInterpolator(OvershootInterpolator(2f))
                .withEndAction(action2).start()
        }

        // Start all animations, starting with this one
        v.animate().alpha(1f)
            .setStartDelay(300)
            .setInterpolator(OvershootInterpolator(2f))
            .withEndAction(action1).start()
    }
}
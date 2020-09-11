package at.shockbytes.corey.ui.fragment.body

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.cardview.widget.CardView
import android.view.animation.AnticipateOvershootInterpolator
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent

/**
 * Author:  Martin Macheiner
 * Date:    05.03.2018
 */
abstract class BodySubFragment : BaseFragment<AppComponent>() {

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    protected fun animateCard(cardView: CardView, startDelay: Long = 0L) {
        val cardAnimAlpha = ObjectAnimator.ofFloat(cardView, "alpha", 0.1f, 1f)
        val cardAnimScaleX = ObjectAnimator.ofFloat(cardView, "scaleX", 0.1f, 1f)
        val cardAnimScaleY = ObjectAnimator.ofFloat(cardView, "scaleY", 0.1f, 1f)
        val cardSet = AnimatorSet()
        cardSet.play(cardAnimAlpha).with(cardAnimScaleX).with(cardAnimScaleY)
        cardSet.duration = 500
        cardSet.startDelay = startDelay
        cardSet.interpolator = AnticipateOvershootInterpolator(1f, 1.2f)
        cardSet.start()
    }
}
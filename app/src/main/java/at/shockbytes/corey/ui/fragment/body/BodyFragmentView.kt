package at.shockbytes.corey.ui.fragment.body

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.support.v7.widget.CardView
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.body.info.BodyInfo
import at.shockbytes.corey.storage.live.LiveBodyUpdateListener
import at.shockbytes.corey.ui.fragment.BaseFragment
import at.shockbytes.corey.user.CoreyUser
import butterknife.ButterKnife
import butterknife.Unbinder

/**
 * @author Martin Macheiner
 * Date: 05-Mar-18.
 */

abstract class BodyFragmentView(protected val fragment: BaseFragment,
                                protected val bodyInfo: BodyInfo,
                                protected val bodyManager: BodyManager,
                                protected val user: CoreyUser) : LiveBodyUpdateListener {

    private var unbinder: Unbinder? = null

    val view: View
        get() {
            val v = fragment.layoutInflater.inflate(layoutId, null, false)
            unbinder = ButterKnife.bind(this, v)
            return v
        }

    protected val weightUnit = bodyManager.weightUnit

    abstract val layoutId: Int

    abstract fun setupView()

    abstract fun animateView(startDelay: Long)

    fun destroyView() {
        unbinder?.unbind()
    }

    protected fun animateCard(cardView: CardView, startDelay: Long) {
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
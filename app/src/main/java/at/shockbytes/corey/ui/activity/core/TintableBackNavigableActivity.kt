package at.shockbytes.corey.ui.activity.core

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import at.shockbytes.corey.R


/**
 * @author Martin Macheiner
 * Date: 02.01.2018.
 */

abstract class TintableBackNavigableActivity : BackNavigableActivity() {

    interface OnTintSystemBarListener {

        fun tint(to: Int, toDark: Int)
    }

    private var abDefColor = R.color.colorPrimary
    private var abTextDefColor = android.R.color.white
    private var sbDefColor = R.color.colorPrimaryDark

    private var upIndicator: Int = R.drawable.ic_back_arrow

    @JvmOverloads
    fun tintHomeAsUpIndicator(@DrawableRes indicator: Int = upIndicator,
                              tint: Boolean = false,
                              @ColorInt tintColor: Int = Color.WHITE) {

        upIndicator = indicator // Store for next time if just tinting is necessary
        if (tint) {
            val drawable = ContextCompat.getDrawable(applicationContext, indicator)
            drawable?.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
            supportActionBar?.setHomeAsUpIndicator(drawable)
        } else {
            supportActionBar?.setHomeAsUpIndicator(indicator)
        }
    }

    fun tintSystemBarsWithText(@ColorRes abColor: Int = abDefColor,
                               @ColorRes sbColor: Int = sbDefColor,
                               @ColorRes abtColor: Int = abTextDefColor,
                               newTitle: String = title.toString(),
                               animated: Boolean = false) {

        val actionBarColor = ContextCompat.getColor(applicationContext, abColor)
        val statusBarColor = ContextCompat.getColor(applicationContext, sbColor)
        val actionBarTextColor = ContextCompat.getColor(applicationContext, abtColor)

        // Set and tint text of action bar
        val text = SpannableString(newTitle)
        text.setSpan(ForegroundColorSpan(actionBarTextColor), 0, text.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        supportActionBar?.title = text

        if (animated) {
            tintSystemBarsAnimated(actionBarColor, statusBarColor)
        } else {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(actionBarColor))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = statusBarColor
            }
        }
        tintHomeAsUpIndicator(tint = true, tintColor = actionBarTextColor)

        // Store reference if colors are changing multiple times
        abDefColor = abColor
        sbDefColor = sbColor
        abTextDefColor = abtColor
    }

    private fun tintSystemBarsAnimated(@ColorInt newColor: Int, @ColorInt newColorDark: Int) {

        val primary = ContextCompat.getColor(this, abDefColor)
        val primaryDark = ContextCompat.getColor(this, sbDefColor)

        val animatorToolbar = ValueAnimator.ofObject(ArgbEvaluator(), primary, newColor)
                .setDuration(300)
        animatorToolbar.addUpdateListener { valueAnimator ->
            supportActionBar?.setBackgroundDrawable(ColorDrawable(valueAnimator.animatedValue as Int))
        }
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), primaryDark, newColorDark)
                .setDuration(300)
        // Suppress lint, because we are only setting applyListener, when api is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAnimation.addUpdateListener { valueAnimator ->
                window.statusBarColor = valueAnimator.animatedValue as Int
            }
        }

        val set = AnimatorSet()
        set.playTogether(animatorToolbar, colorAnimation)
        set.start()
    }

}
package at.shockbytes.corey.ui.fragment

import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent
import com.github.florent37.viewanimator.ViewAnimator
import kotlinx.android.synthetic.main.fragment_notification_settings.*

class NotificationSettingsFragment : BaseFragment<AppComponent>() {

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_background

    override val layoutId: Int = R.layout.fragment_notification_settings

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun setupViews() {

        layout_fragment_notification_settings.setOnClickListener {
            closeFragment()
        }

        animateCardIn()
    }

    private fun animateCardIn() {

        val fromTranslationY = 150f
        val fromAlpha = 0f

        card_fragment_notification_settings.apply {
            translationY = fromTranslationY
            alpha = fromAlpha
        }

        ViewAnimator.animate(card_fragment_notification_settings)
            .translationY(fromTranslationY, 0f)
            .alpha(fromAlpha, 1f)
            .startDelay(300)
            .decelerate()
            .duration(300)
            .start()
    }

    override fun bindViewModel() {
    }

    override fun unbindViewModel() = Unit

    private fun closeFragment() {
        fragmentManager?.popBackStack()
    }

    companion object {

        fun newInstance(): NotificationSettingsFragment {
            return NotificationSettingsFragment()
        }
    }
}
package at.shockbytes.corey.ui.fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.adapter.spinner.DayPickerSpinnerAdapter
import at.shockbytes.corey.ui.viewmodel.ReminderViewModel
import com.github.florent37.viewanimator.ViewAnimator
import kotlinx.android.synthetic.main.fragment_notification_settings.*
import javax.inject.Inject

class ReminderFragment : BaseFragment<AppComponent>() {

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_background

    override val layoutId: Int = R.layout.fragment_notification_settings

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: ReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[ReminderViewModel::class.java]
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun setupViews() {

        layout_fragment_reminder.setOnClickListener {
            closeFragment()
        }

        context?.let { ctx ->
            spinner_fragment_reminder_weigh.adapter = DayPickerSpinnerAdapter(
                ctx,
                resources.getStringArray(R.array.daysFull)
            )
        }

        cb_fragment_reminder_workout.setOnCheckedChangeListener { _, isChecked ->
            viewModel.enableWorkoutReminder(isChecked)
        }

        cb_fragment_reminder_weigh.setOnCheckedChangeListener { _, isChecked ->
            viewModel.enableWeighReminder(isChecked)
        }

        animateCardIn()
    }

    private fun animateCardIn() {

        val fromTranslationY = 150f
        val fromAlpha = 0f

        card_fragment_reminder.apply {
            translationY = fromTranslationY
            alpha = fromAlpha
        }

        ViewAnimator.animate(card_fragment_reminder)
            .translationY(fromTranslationY, 0f)
            .alpha(fromAlpha, 1f)
            .startDelay(300)
            .decelerate()
            .duration(300)
            .start()
    }

    private fun animateCardOut(endAction: (() -> Unit)) {

        ViewAnimator.animate(card_fragment_reminder)
            .translationY(0f, -1000f)
            .alpha(1f, 0.0f)
            .accelerate()
            .duration(300)
            .onStop { endAction.invoke() }
            .start()
    }

    override fun bindViewModel() {

        viewModel.isWorkoutReminderEnabled().observe(this, Observer { isWorkoutReminderEnabled ->
            enableWorkoutViews(isWorkoutReminderEnabled)
        })

        viewModel.isWeighReminderEnabled().observe(this, Observer { isWeighReminderEnabled ->
            enableWeighViews(isWeighReminderEnabled)
        })
    }

    private fun enableWeighViews(isEnabled: Boolean) {
        cb_fragment_reminder_weigh.isChecked = isEnabled
        btn_fragment_reminder_weigh.isEnabled = isEnabled
        spinner_fragment_reminder_weigh.isEnabled = isEnabled
    }

    private fun enableWorkoutViews(isEnabled: Boolean) {
        cb_fragment_reminder_workout.isChecked = isEnabled
        btn_fragment_reminder_workout.isEnabled = isEnabled
    }

    override fun unbindViewModel() = Unit

    private fun closeFragment() {

        animateCardOut {
            fragmentManager?.popBackStack()
        }
    }

    companion object {

        fun newInstance(): ReminderFragment {
            return ReminderFragment()
        }
    }
}
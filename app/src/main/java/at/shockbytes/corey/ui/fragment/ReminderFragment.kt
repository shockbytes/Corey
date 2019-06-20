package at.shockbytes.corey.ui.fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.adapter.spinner.DayPickerSpinnerAdapter
import at.shockbytes.corey.ui.viewmodel.ReminderViewModel
import com.github.florent37.viewanimator.ViewAnimator
import com.michaldrabik.classicmaterialtimepicker.CmtpDialogFragment
import com.michaldrabik.classicmaterialtimepicker.OnTime24PickedListener
import com.michaldrabik.classicmaterialtimepicker.model.CmtpTime24
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

        spinner_fragment_reminder_weigh.apply {
            adapter = DayPickerSpinnerAdapter(
                requireContext(),
                resources.getStringArray(R.array.daysFull)
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.setDayOfWeighReminder(requireContext(), position)
                }
            }
        }

        cb_fragment_reminder_workout.setOnCheckedChangeListener { _, isChecked ->
            viewModel.enableWorkoutReminder(isChecked)
        }

        cb_fragment_reminder_weigh.setOnCheckedChangeListener { _, isChecked ->
            viewModel.enableWeighReminder(isChecked)
        }

        btn_fragment_reminder_workout.setOnClickListener { v ->
            openTimePicker { hour, _ ->
                viewModel.setHourOfWorkoutReminder(v.context, hour)
            }
        }

        btn_fragment_reminder_weigh.setOnClickListener { v ->
            openTimePicker { hour, _ ->
                viewModel.setHourOfWeighReminder(v.context, hour)
            }
        }

        animateCardIn()
    }

    private fun openTimePicker(onSelect: (hour: Int, minute: Int) -> Unit) {
        CmtpDialogFragment
            .newInstance().apply {
                setInitialTime24(hour = 6, minute = 0)
                setOnTime24PickedListener(object : OnTime24PickedListener {
                    override fun onTimePicked(time: CmtpTime24) {
                        onSelect(time.hour, time.minute)
                    }
                })
            }
            .show(childFragmentManager, "hour-picker-fragment")
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
            .onStop { endAction() }
            .start()
    }

    override fun bindViewModel() {

        viewModel.isWorkoutReminderEnabled().observe(this, Observer { isWorkoutReminderEnabled ->
            enableWorkoutViews(isWorkoutReminderEnabled)
        })

        viewModel.isWeighReminderEnabled().observe(this, Observer { isWeighReminderEnabled ->
            enableWeighViews(isWeighReminderEnabled)
        })

        viewModel.getHourOfWeighReminder().observe(this, Observer { hourOfWeighing ->
            btn_fragment_reminder_weigh.text = hourOfWeighing
        })

        viewModel.getDayOfWeighReminder().observe(this, Observer { dayIndexOfWeighing ->
            spinner_fragment_reminder_weigh.setSelection(dayIndexOfWeighing, true)
        })

        viewModel.getHourOfWorkoutReminder().observe(this, Observer { hourOfWorkout ->
            btn_fragment_reminder_workout.text = hourOfWorkout
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
package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import android.view.HapticFeedbackConstants
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.WearAppComponent
import kotlinx.android.synthetic.main.fragment_running.*

class RunningFragment : WearableBaseFragment() {

    override val layoutId = R.layout.fragment_running

    override fun setupViews() {
        // Setup views

        btn_fragment_running_start.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            showToast("Coming soon...")
        }
    }

    override fun injectToGraph(appComponent: WearAppComponent) {
        appComponent.inject(this)
    }

    companion object {

        fun newInstance(): RunningFragment {
            return RunningFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}

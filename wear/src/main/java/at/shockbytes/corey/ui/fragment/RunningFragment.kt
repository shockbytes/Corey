package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.dagger.WearAppComponent
import at.shockbytes.corey.ui.viewmodel.WearRunningViewModel
import kotlinx.android.synthetic.main.fragment_running.*
import timber.log.Timber
import javax.inject.Inject

class RunningFragment : WearableBaseFragment() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: WearRunningViewModel

    override val layoutId = R.layout.fragment_running

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[WearRunningViewModel::class.java]
    }

    override fun setupViews() {

        btn_fragment_running_start.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            viewModel.startRun()
        }
    }

    override fun bindViewModel() {

        viewModel.onStartEvent
            .subscribe ({
                removeStartButton()
            }, { throwable ->
                Timber.e(throwable)
            })
            .addTo(compositeDisposable)
    }

    private fun removeStartButton() {
        btn_fragment_running_start
            .animate()
            .alpha(0f)
            .start()
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

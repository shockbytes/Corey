package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import android.os.SystemClock
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.dagger.WearAppComponent
import at.shockbytes.corey.ui.viewmodel.WearRunningViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_running.*
import timber.log.Timber
import javax.inject.Inject

class WearRunningFragment : WearableBaseFragment() {

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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                removeStartButton()
                showRunningViewRootLayout()
                startChronometer()
            }, { throwable ->
                Timber.e(throwable)
            })
            .addTo(compositeDisposable)
    }

    private fun startChronometer() {
        chronometer_fragment_running.apply {
            base = SystemClock.elapsedRealtime()
            start()
        }
    }

    private fun showRunningViewRootLayout() {

        layout_fragment_running_run_views
            .animate()
            .withStartAction {
                layout_fragment_running_run_views.apply {
                    visibility = View.VISIBLE
                }
            }
            .alpha(1f)
            .setDuration(500)
            .start()
    }

    private fun removeStartButton() {
        btn_fragment_running_start
            .animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                btn_fragment_running_start.visibility = View.GONE
            }
            .start()
    }

    override fun injectToGraph(appComponent: WearAppComponent) {
        appComponent.inject(this)
    }

    companion object {

        fun newInstance(): WearRunningFragment {
            return WearRunningFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}

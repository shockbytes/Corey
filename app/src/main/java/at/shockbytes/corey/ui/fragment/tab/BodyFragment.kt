package at.shockbytes.corey.ui.fragment.tab

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.body.*
import at.shockbytes.corey.ui.viewmodel.BodyViewModel
import kotterknife.bindView
import javax.inject.Inject

class BodyFragment : TabBaseFragment<AppComponent>() {

    @Inject
    protected lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BodyViewModel

    private val profileBodyFragment = ProfileBodyFragmentView()
    private val dreamWeightBodyFragmentView = DreamWeightBodyFragmentView()
    private val weightHistoryBodyFragmentView = WeightHistoryBodyFragmentView()
    private val goalsFragment = GoalsFragment.newInstance()

    private val fragmentViews: List<BodySubFragment> by lazy {
        listOf(
                profileBodyFragment,
                dreamWeightBodyFragmentView,
                weightHistoryBodyFragmentView,
                goalsFragment
        )
    }

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    private val errorView: View by bindView(R.id.fragment_body_error_layout)
    private val btnError: Button by bindView(R.id.fragment_body_error_layout_btn_refresh)
    private val txtError: TextView by bindView(R.id.fragment_body_error_layout_txt_cause)

    override val layoutId = R.layout.fragment_body

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[BodyViewModel::class.java]

        for (fragment in childFragmentManager.fragments) {
            childFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun bindViewModel() {
        viewModel.requestBodyInfo()

        viewModel.getBodyInfo().observe(this, { state ->
            when (state) {
                is BodyViewModel.BodyInfoState.SuccessState -> {
                    hideErrorView()

                    profileBodyFragment.setProfileData(
                            state.userBody,
                            state.user,
                            state.weightUnit
                    )

                    dreamWeightBodyFragmentView.setDreamWeightData(
                            state.userBody.desiredWeight,
                            state.userBody.currentWeight,
                            state.weightUnit
                    )

                    weightHistoryBodyFragmentView.setWeightData(
                            state.weightLines,
                            state.userBody.desiredWeight,
                            state.weightUnit
                    )
                }
                is BodyViewModel.BodyInfoState.ErrorState -> {
                    showErrorView(state.throwable.localizedMessage ?: "Unknown error")
                }
            }
        })
    }

    override fun unbindViewModel() {
        viewModel.getBodyInfo().removeObservers(this)
    }

    override fun setupViews() {
        fragmentViews.forEach { fragment ->
            childFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_body_nsv_content, fragment, fragment.tag)
                    .commit()
        }
    }

    // ------------------------------------------------------------------------------

    private fun showErrorView(cause: String) {
        errorView.visibility = View.VISIBLE
        errorView.animate().alpha(1f).start()

        btnError.setOnClickListener {
            viewModel.requestBodyInfo()
        }
        txtError.text = cause
    }

    private fun hideErrorView() {
        errorView.visibility = View.GONE
        errorView.alpha = 0f
    }

    companion object {

        private const val cardAnimStartDelay = 200

        fun newInstance(): BodyFragment {
            return BodyFragment()
        }
    }
}

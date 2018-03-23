package at.shockbytes.corey.ui.fragment.pager

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.body.LiveBodyUpdateListener
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.body.*
import at.shockbytes.corey.user.UserManager
import at.shockbytes.util.AppUtils
import kotterknife.bindView
import javax.inject.Inject

class BodyFragment : BasePagerFragment(), LiveBodyUpdateListener {

    private val container: LinearLayout by bindView(R.id.fragment_body_nsv_content)
    private val errorView: View by bindView(R.id.fragment_body_error_layout)
    private val btnError: Button by bindView(R.id.fragment_body_error_layout_btn_refresh)
    private val txtError: TextView by bindView(R.id.fragment_body_error_layout_txt_cause)

    @Inject
    protected lateinit var bodyManager: BodyManager

    @Inject
    protected lateinit var userManager: UserManager

    private var fragmentViews: List<BodyFragmentView>? = null

    override val layoutId = R.layout.fragment_body


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentViews?.forEach {
            it.destroyView()
        }
        container.removeAllViews()
    }

    override fun injectToGraph(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun registerForLiveEvents() {
        bodyManager.registerLiveBodyUpdates(this)
    }

    override fun unregisterForLiveEvents() {
        bodyManager.unregisterLiveBodyUpdates()
    }

    override fun onDesiredWeightChanged(changed: Int) {
        fragmentViews?.forEach { it.onDesiredWeightChanged(changed) }
    }

    override fun onBodyGoalAdded(g: Goal) {
        fragmentViews?.forEach { it.onBodyGoalAdded(g) }
    }

    override fun onBodyGoalDeleted(g: Goal) {
        fragmentViews?.forEach { it.onBodyGoalDeleted(g) }
    }

    override fun onBodyGoalChanged(g: Goal) {
        fragmentViews?.forEach { it.onBodyGoalChanged(g) }
    }

    public override fun setupViews() {

        val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val px4 = AppUtils.convertDpInPixel(4, context!!)
        val px8 = AppUtils.convertDpInPixel(8, context!!)
        layoutParams.setMargins(px8, px8, px8, px4)

        fragmentViews?.forEach {
            container.addView(it.view, layoutParams)
            it.setupView()
        }
        animateViews()
    }

    // ------------------------------------------------------------------------------

    private fun loadViews() {
        bodyManager.bodyInfo.subscribe({ info ->

            fragmentViews = listOf(ProfileBodyFragmentView(this, info, bodyManager, userManager.user),
                    DreamWeightBodyFragmentView(this, info, bodyManager, userManager.user),
                    WeightHistoryBodyFragmentView(this, info, bodyManager, userManager.user),
                    GoalBodyFragmentView(this, info, bodyManager, userManager.user),
                    StatisticsBodyFragmentView(this, info, bodyManager, userManager.user))

            hideErrorView()
            setupViews()
        }) { throwable ->
            throwable.printStackTrace()
            showErrorView(throwable.localizedMessage)
        }
    }

    private fun animateViews() {
        fragmentViews?.forEachIndexed { idx, view ->
            val startDelay = (cardAnimStartDelay * (idx + 1)).toLong()
            view.animateView(startDelay)
        }
    }

    private fun showErrorView(cause: String) {
        errorView.visibility = View.VISIBLE
        errorView.animate().alpha(1f).start()

        btnError.setOnClickListener { loadViews() }
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

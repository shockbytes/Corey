package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import at.shockbytes.corey.R
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.storage.live.LiveBodyUpdateListener
import at.shockbytes.corey.ui.fragment.body.*
import at.shockbytes.corey.user.UserManager
import at.shockbytes.util.AppUtils
import kotterknife.bindView
import javax.inject.Inject

class BodyFragment : BaseFragment(), LiveBodyUpdateListener {

    private val container: LinearLayout by bindView(R.id.fragment_body_nsv_content)

    @Inject
    protected lateinit var bodyManager: BodyManager

    @Inject
    protected lateinit var userManager: UserManager

    private var fragmentViews: List<BodyFragmentView>? = null

    override val layoutId = R.layout.fragment_body



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bodyManager.bodyInfo.subscribe({
            fragmentViews = listOf(ProfileBodyFragmentView(this, it, bodyManager, userManager.user),
                    DreamWeightBodyFragmentView(this, it, bodyManager, userManager.user),
                    WeightHistoryBodyFragmentView(this, it, bodyManager, userManager.user),
                    GoalBodyFragmentView(this, it, bodyManager, userManager.user))
            setupViews()
        }) { throwable ->
            throwable.printStackTrace()
            // TODO Show error screen
        }
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

    override fun onStop() {
        super.onStop()
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
        bodyManager.registerLiveBodyUpdates(this)
    }

    // ------------------------------------------------------------------------------

    private fun animateViews() {
        fragmentViews?.forEachIndexed { idx, view ->
            val startDelay = (cardAnimStartDelay * (idx + 1)).toLong()
            view.animateView(startDelay)
        }
    }

    companion object {

        private const val cardAnimStartDelay = 200

        fun newInstance(): BodyFragment {
            return BodyFragment()
        }
    }
}

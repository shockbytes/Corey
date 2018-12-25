package at.shockbytes.corey.ui.fragment.pager

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.GoalAdapter
import at.shockbytes.corey.data.goal.Goal
import at.shockbytes.corey.dagger.DaggerAppComponent
import at.shockbytes.corey.ui.viewmodel.GoalsViewModel
import kotlinx.android.synthetic.main.fragment_goals.*
import javax.inject.Inject

class GoalsFragment: BaseFragment<DaggerAppComponent>(), GoalAdapter.OnGoalActionClickedListener {

    @Inject
    protected lateinit var vmFactory: ViewModelProvider.Factory

    override val layoutId: Int = R.layout.fragment_goals

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    private lateinit var viewModel: GoalsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[GoalsViewModel::class.java]
    }

    override fun bindViewModel() {
        viewModel.requestGoals()

        viewModel.getBodyGoals().observe(this, Observer {
            it?.let { goals ->
                (fragment_body_card_goals_rv.adapter as GoalAdapter).data = goals.toMutableList()
                fragment_body_card_goals_rv.invalidate()
            }
        })
    }

    override fun injectToGraph(appComponent: DaggerAppComponent?) {
        appComponent?.inject(this)
    }

    override fun setupViews() {
        context?.let { ctx ->
            fragment_body_card_goals_rv.layoutManager = LinearLayoutManager(ctx)
            val goalAdapter = GoalAdapter(ctx, listOf())
            goalAdapter.setOnGoalActionClickedListener(this)
            fragment_body_card_goals_rv.adapter = goalAdapter
        }
    }

    override fun unbindViewModel() {
        viewModel.getBodyGoals().removeObservers(this)
    }

    override fun onDeleteGoalClicked(g: Goal) {
        viewModel.deleteGoal(g)
    }

    override fun onFinishGoalClicked(g: Goal) {
        viewModel.setGoalFinished(g)
        fragment_body_card_goals_rv.invalidate()
    }

    companion object {

        fun newInstance(): GoalsFragment {
            return GoalsFragment()
        }
    }
}
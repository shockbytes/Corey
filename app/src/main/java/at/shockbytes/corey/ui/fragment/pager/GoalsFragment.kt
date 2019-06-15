package at.shockbytes.corey.ui.fragment.pager

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.GoalAdapter import at.shockbytes.corey.dagger.DaggerAppComponent
import at.shockbytes.corey.ui.model.GoalItem
import at.shockbytes.corey.ui.viewmodel.GoalsViewModel
import kotlinx.android.synthetic.main.fragment_goals.*
import javax.inject.Inject
import androidx.recyclerview.widget.DividerItemDecoration

class GoalsFragment : BaseFragment<DaggerAppComponent>(), GoalAdapter.OnGoalActionClickedListener {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

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
                fragment_body_card_goals_rv.apply {
                    (adapter as GoalAdapter).data = goals.toMutableList()
                    invalidate()
                    scrollToPosition(0)
                }
            }
        })

        viewModel.selectHideFinishedGoals().observe(this, Observer {
            fragment_goals_cb_hide_finished.apply {
                isChecked = (it == true)
                invalidate()
            }
        })
    }

    override fun injectToGraph(appComponent: DaggerAppComponent?) {
        appComponent?.inject(this)
    }

    override fun setupViews() {
        context?.let { ctx ->
            fragment_body_card_goals_rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
            fragment_body_card_goals_rv.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(activity, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
            val goalAdapter = GoalAdapter(ctx)
            goalAdapter.setOnGoalActionClickedListener(this)
            fragment_body_card_goals_rv.adapter = goalAdapter
        }

        fragment_goals_cb_hide_finished.setOnCheckedChangeListener { _, isChecked ->
            viewModel.showFinishedGoals(isChecked)
        }
    }

    override fun unbindViewModel() {
        viewModel.getBodyGoals().removeObservers(this)
    }

    override fun onDeleteGoalClicked(g: GoalItem) {
        viewModel.deleteGoal(g)
    }

    override fun onFinishGoalClicked(g: GoalItem) {
        viewModel.setGoalFinished(g)
        fragment_body_card_goals_rv.invalidate()
    }

    companion object {

        fun newInstance(): GoalsFragment {
            return GoalsFragment()
        }
    }
}
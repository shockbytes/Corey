package at.shockbytes.corey.ui.fragment.body

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.GoalAdapter
import at.shockbytes.corey.ui.model.GoalItem
import at.shockbytes.corey.ui.viewmodel.GoalsViewModel
import kotlinx.android.synthetic.main.fragment_goals.*
import javax.inject.Inject
import androidx.recyclerview.widget.DividerItemDecoration
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.dialog.AddGoalDialogFragment

class GoalsFragment : BodySubFragment(), GoalAdapter.OnGoalActionClickedListener {

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

        viewModel.getBodyGoals().observe(this, { goals ->
            fragment_body_card_goals_rv.apply {
                (adapter as GoalAdapter).data = goals.toMutableList()
                invalidate()
                scrollToPosition(0)
            }
        })

        viewModel.selectHideFinishedGoals().observe(this, { hideGoals ->
            fragment_goals_cb_hide_finished.apply {
                isChecked = hideGoals
                invalidate()
            }
        })
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun setupViews() {
        fragment_body_card_goals_rv.layoutManager = LinearLayoutManager(requireContext())
        fragment_body_card_goals_rv.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        val goalAdapter = GoalAdapter(requireContext())
        goalAdapter.setOnGoalActionClickedListener(this)
        fragment_body_card_goals_rv.adapter = goalAdapter

        fragment_goals_cb_hide_finished.setOnCheckedChangeListener { _, isChecked ->
            viewModel.showFinishedGoals(isChecked)
        }

        btn_fragment_goals_new_goal.setOnClickListener {
            AddGoalDialogFragment.newInstance()
                    .setOnGoalCreatedListener(viewModel::storeBodyGoal)
                    .show(childFragmentManager, "dialog-fragment-add-goal")
        }

        animateCard(fragment_body_card_goals)
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
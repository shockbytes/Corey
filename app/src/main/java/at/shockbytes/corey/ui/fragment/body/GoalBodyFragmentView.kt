package at.shockbytes.corey.ui.fragment.body

import android.support.v7.widget.LinearLayoutManager
import at.shockbytes.corey.R
import at.shockbytes.corey.adapter.GoalAdapter
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.dialog.AddGoalDialogFragment
import kotlinx.android.synthetic.main.fragment_body_view_goals.*

/**
 * Author:  Martin Macheiner
 * Date:    05.03.2018
 */
class GoalBodyFragmentView : BodySubFragment(), GoalAdapter.OnGoalActionClickedListener {
    override fun bindViewModel() {
        // TODO        bodyManager.bodyGoals.subscribe { goals -> goals.forEach { onBodyGoalAdded(it) } }
        //                //.addTo(compositeDisposable)
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        // TODO
    }

    override fun unbindViewModel() {
        // TODO
    }

    private lateinit var goalAdapter: GoalAdapter

    override val layoutId = R.layout.fragment_body_view_goals

    override fun onDeleteGoalClicked(g: Goal) {
    }

    override fun onFinishGoalClicked(g: Goal) {
    }

    override fun setupViews() {

        fragment_body_card_goals_rv.layoutManager = LinearLayoutManager(context!!)
        goalAdapter = GoalAdapter(context!!, listOf())
        goalAdapter.setOnGoalActionClickedListener(this)
        fragment_body_card_goals_rv.adapter = goalAdapter

        fragment_body_card_goals_btn_add.setOnClickListener {
            onClickAddGoal()
        }

        animateCard(fragment_body_card_goals, 0)
    }

    override fun animateView(startDelay: Long) {
    }

    private fun onClickAddGoal() {
        AddGoalDialogFragment.newInstance()
                .setOnGoalMessageAddedListener { msg ->
                    // bodyManager.storeBodyGoal(Goal(msg, false, ""))
                }.show(fragmentManager, "dialog-fragment-add-goal")
    }


}
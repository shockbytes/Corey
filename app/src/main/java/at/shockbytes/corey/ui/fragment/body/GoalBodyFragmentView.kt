package at.shockbytes.corey.ui.fragment.body

import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import at.shockbytes.corey.R
import at.shockbytes.corey.adapter.GoalAdapter
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.body.info.BodyInfo
import at.shockbytes.corey.ui.fragment.BaseFragment
import at.shockbytes.corey.ui.fragment.dialogs.AddGoalDialogFragment
import at.shockbytes.corey.user.CoreyUser
import butterknife.BindView
import butterknife.OnClick

/**
 * @author Martin Macheiner
 * Date: 05-Mar-18.
 */
class GoalBodyFragmentView(fragment: BaseFragment,
                                  bodyInfo: BodyInfo,
                                  bodyManager: BodyManager,
                                  user: CoreyUser) : BodyFragmentView(fragment, bodyInfo, bodyManager, user),
        GoalAdapter.OnGoalActionClickedListener {

    private lateinit var goalAdapter: GoalAdapter

    @BindView(R.id.fragment_body_card_goals)
    protected lateinit var cardView: CardView

    @BindView(R.id.fragment_body_card_goals_rv)
    protected lateinit var recyclerViewGoals: RecyclerView

    override val layoutId = R.layout.fragment_body_view_goals

    override fun onDesiredWeightChanged(changed: Int) {
        // Not interesting...
    }

    override fun onBodyGoalAdded(g: Goal) {
        if (!g.isDone) {
            goalAdapter.addEntityAtFirst(g)
        } else {
            goalAdapter.addEntityAtLast(g)
        }
    }

    override fun onBodyGoalDeleted(g: Goal) {
        goalAdapter.deleteEntity(g)
    }

    override fun onBodyGoalChanged(g: Goal) {
        goalAdapter.updateEntity(g)
    }

    override fun onDeleteGoalClicked(g: Goal) {
        bodyManager.removeBodyGoal(g)
    }

    override fun onFinishGoalClicked(g: Goal) {
        g.isDone = true
        bodyManager.updateBodyGoal(g)
    }

    override fun setupView() {

        recyclerViewGoals.layoutManager = LinearLayoutManager(fragment.context)
        goalAdapter = GoalAdapter(fragment.context!!, listOf())
        goalAdapter.setOnGoalActionClickedListener(this)
        recyclerViewGoals.adapter = goalAdapter

        bodyManager.bodyGoals.subscribe { goals -> goals.forEach { onBodyGoalAdded(it) } }
    }

    override fun animateView(startDelay: Long) {
        animateCard(cardView, startDelay)
    }

    @OnClick(R.id.fragment_body_card_goals_btn_add)
    fun onClickAddGoal() {
        AddGoalDialogFragment.newInstance()
                .setOnGoalMessageAddedListener { msg ->
                    bodyManager.storeBodyGoal(Goal(msg, false, ""))
                }.show(fragment.fragmentManager, "dialog-fragment-add-goal")
    }


}
package at.shockbytes.corey.ui.fragment.body

import android.support.v7.widget.CardView
import at.shockbytes.corey.R
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.body.info.BodyInfo
import at.shockbytes.corey.ui.fragment.BaseFragment
import at.shockbytes.corey.user.CoreyUser
import butterknife.BindView

/**
 * @author  Martin Macheiner
 * Date:    23.03.2018
 */

class StatisticsBodyFragmentView(fragment: BaseFragment,
                                 bodyInfo: BodyInfo,
                                 bodyManager: BodyManager,
                                 user: CoreyUser) : BodyFragmentView(fragment, bodyInfo, bodyManager, user) {

    @BindView(R.id.fragment_body_card_statistics)
    protected lateinit var cardView: CardView

    override val layoutId = R.layout.fragment_body_view_statistics


    override fun onDesiredWeightChanged(changed: Int) {
        // Do nothing...
    }

    override fun onBodyGoalAdded(g: Goal) {
        // Do nothing...
    }

    override fun onBodyGoalDeleted(g: Goal) {
        // Do nothing...
    }

    override fun onBodyGoalChanged(g: Goal) {
        // Do nothing...
    }

    override fun setupView() {
        // TODO Implement Statistics card
    }

    override fun animateView(startDelay: Long) {
        animateCard(cardView, startDelay)
    }
}
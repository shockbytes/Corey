package at.shockbytes.corey.ui.fragment.body

import android.support.v7.widget.CardView
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.body.info.BodyInfo
import at.shockbytes.corey.ui.fragment.BaseFragment
import at.shockbytes.corey.user.CoreyUser
import at.shockbytes.util.AppUtils
import butterknife.BindView
import java.util.*

/**
 * @author  Martin Macheiner
 * Date:    05.03.2018
 */

class DreamWeightBodyFragmentView(fragment: BaseFragment,
                                  bodyInfo: BodyInfo,
                                  bodyManager: BodyManager,
                                  user: CoreyUser) : BodyFragmentView(fragment, bodyInfo, bodyManager, user) {

    @BindView(R.id.fragment_body_card_dream_weight)
    protected lateinit var cardView: CardView

    @BindView(R.id.fragment_body_card_dream_weight_txt_headline)
    protected lateinit var txtHeadline: TextView

    @BindView(R.id.fragment_body_card_dream_weight_txt_content)
    protected lateinit var txtContent: TextView

    @BindView(R.id.fragment_body_card_dream_weight_txt_diff)
    protected lateinit var txtDiff: TextView


    override val layoutId = R.layout.fragment_body_view_dream_weight

    override fun onDesiredWeightChanged(changed: Int) {
        bodyInfo.dreamWeight = changed
        setupView()
    }

    override fun onBodyGoalAdded(g: Goal) {
        // Not interesting...
    }

    override fun onBodyGoalDeleted(g: Goal) {
        // Not interesting...
    }

    override fun onBodyGoalChanged(g: Goal) {
        // Not interesting...
    }

    override fun setupView() {

        val titles = fragment.resources.getStringArray(R.array.dreamweight_motivation)
        val title = titles[Random().nextInt(titles.size - 1)]
        txtHeadline.text = title

        val diff = AppUtils.roundDouble(bodyInfo.latestWeightPoint.weight - bodyInfo.dreamWeight, 1)
        txtDiff.text = "$diff $weightUnit"

        txtContent.text = fragment.getString(R.string.dreamweight_card_text,
                "${bodyInfo.dreamWeight} $weightUnit")
    }

    override fun animateView(startDelay: Long) {
        animateCard(cardView, startDelay)
    }

}
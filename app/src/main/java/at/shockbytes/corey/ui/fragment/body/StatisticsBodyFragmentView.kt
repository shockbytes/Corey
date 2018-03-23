package at.shockbytes.corey.ui.fragment.body

import android.support.v7.widget.CardView
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.body.info.BodyInfo
import at.shockbytes.corey.ui.fragment.BaseFragment
import butterknife.BindView

/**
 * @author  Martin Macheiner
 * Date:    23.03.2018
 */

class StatisticsBodyFragmentView(fragment: BaseFragment,
                                 bodyInfo: BodyInfo,
                                 bodyManager: BodyManager) : BodyFragmentView(fragment, bodyInfo, bodyManager) {

    @BindView(R.id.fragment_body_card_statistics)
    protected lateinit var cardView: CardView

    @BindView(R.id.fragment_body_card_statistics_txt_workouts)
    protected lateinit var txtWorkouts: TextView

    @BindView(R.id.fragment_body_card_statistics_txt_pulse)
    protected lateinit var txtAvgPulse: TextView

    @BindView(R.id.fragment_body_card_statistics_txt_calories)
    protected lateinit var txtCalories: TextView

    @BindView(R.id.fragment_body_card_statistics_txt_workout_time)
    protected lateinit var txtTime: TextView

    @BindView(R.id.fragment_body_card_statistics_txt_distance)
    protected lateinit var txtDistance: TextView

    @BindView(R.id.fragment_body_card_statistics_txt_longest_workout)
    protected lateinit var txtLongestWorkout: TextView

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
        // TODO Replace with API calls
        txtWorkouts.text = "14\nworkouts"
        txtAvgPulse.text = "98 bpm\navg. pulse"
        txtCalories.text = "10.000\nburned calories"

        txtTime.text = "14:00:00\nhours worked out"
        txtDistance.text = "140km\ncovered so far"
        txtLongestWorkout.text = "00:45:00\nlongest workout"
    }

    override fun animateView(startDelay: Long) {
        animateCard(cardView, startDelay)
    }
}
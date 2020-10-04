package at.shockbytes.corey.ui.fragment.body

import at.shockbytes.corey.R
import at.shockbytes.corey.common.roundDouble
import at.shockbytes.corey.common.setVisible
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.viewmodel.BodyViewModel.BodyState.SuccessState.DesiredWeightState
import kotlinx.android.synthetic.main.fragment_body_view_dream_weight.*

/**
 * Author:  Martin Macheiner
 * Date:    05.03.2018
 */
class DesiredWeightBodyFragmentView : BodySubFragment() {

    override fun bindViewModel() = Unit
    override fun injectToGraph(appComponent: AppComponent?) = Unit
    override fun unbindViewModel() = Unit

    override val layoutId = R.layout.fragment_body_view_dream_weight

    override fun setupViews() = Unit

    fun setDesiredWeightState(desiredWeightState: DesiredWeightState) {

        when (desiredWeightState) {
            is DesiredWeightState.Reached -> showReachedState(desiredWeightState)
            is DesiredWeightState.NotReached -> showNotReachedState(desiredWeightState)
        }

        animateCard(fragment_body_card_dream_weight, 0)
    }

    private fun showReachedState(desiredWeightState: DesiredWeightState.Reached) {
        with(desiredWeightState) {
            fragment_body_card_dream_weight_txt_headline.setText(R.string.congrats_with_emoji)
            fragment_body_card_dream_weight_txt_diff.text = getString(
                    R.string.desired_weight_card_text_reached,
                    desiredWeight,
                    weightUnit
            )
            fragment_body_card_dream_weight_txt_content.setVisible(false)
        }
    }

    private fun showNotReachedState(desiredWeightState: DesiredWeightState.NotReached) {

        with(desiredWeightState) {

            val title = resources.getStringArray(R.array.dreamweight_motivation).random()
            fragment_body_card_dream_weight_txt_headline.text = title

            val diff = currentWeight
                    .minus(desiredWeight)
                    .roundDouble(1)
                    .toString()

            fragment_body_card_dream_weight_txt_diff.text = getString(R.string.concat, diff, weightUnit)

            fragment_body_card_dream_weight_txt_content.apply {
                setVisible(true)
                text = getString(R.string.desired_weight_card_text, "$desiredWeight $weightUnit")
            }
        }
    }
}
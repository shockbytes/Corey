package at.shockbytes.corey.ui.fragment.body

import at.shockbytes.corey.R
import at.shockbytes.corey.common.roundDouble
import at.shockbytes.corey.dagger.AppComponent
import kotlinx.android.synthetic.main.fragment_body_view_dream_weight.*
import java.util.Random

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

    fun setDreamWeightData(desiredWeight: Int, currentWeight: Double, weightUnit: String) {

        val titles = resources.getStringArray(R.array.dreamweight_motivation)
        val title = titles[Random().nextInt(titles.size - 1)]
        fragment_body_card_dream_weight_txt_headline.text = title

        val diff = currentWeight
                .minus(desiredWeight)
                .roundDouble(1)
                .toString()

        fragment_body_card_dream_weight_txt_diff.text = getString(R.string.concat, diff, weightUnit)

        fragment_body_card_dream_weight_txt_content.text = getString(R.string.dreamweight_card_text,
                "$desiredWeight $weightUnit")

        animateCard(fragment_body_card_dream_weight, 0)
    }
}
package at.shockbytes.corey.ui.fragment.body

import android.support.v7.widget.CardView
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.util.AppUtils
import kotterknife.bindView
import java.util.Random

/**
 * Author:  Martin Macheiner
 * Date:    05.03.2018
 */
class DreamWeightBodyFragmentView : BodySubFragment() {

    override fun bindViewModel() = Unit
    override fun injectToGraph(appComponent: AppComponent?) = Unit
    override fun unbindViewModel() = Unit

    private val cardView by bindView<CardView>(R.id.fragment_body_card_dream_weight)
    private val txtHeadline by bindView<TextView>(R.id.fragment_body_card_dream_weight_txt_headline)
    private val txtContent by bindView<TextView>(R.id.fragment_body_card_dream_weight_txt_content)
    private val txtDiff by bindView<TextView>(R.id.fragment_body_card_dream_weight_txt_diff)

    override val layoutId = R.layout.fragment_body_view_dream_weight

    override fun setupViews() = Unit

    fun setDreamWeightData(dreamWeight: Int, latestWeight: Double, weightUnit: String) {

        val titles = resources.getStringArray(R.array.dreamweight_motivation)
        val title = titles[Random().nextInt(titles.size - 1)]
        txtHeadline.text = title

        val diff = AppUtils.roundDouble(latestWeight - dreamWeight, 1)
        txtDiff.text = "$diff $weightUnit"

        txtContent.text = getString(R.string.dreamweight_card_text,
                "$dreamWeight $weightUnit")

        animateCard(cardView, 0)
    }

    override fun animateView(startDelay: Long) = Unit
}
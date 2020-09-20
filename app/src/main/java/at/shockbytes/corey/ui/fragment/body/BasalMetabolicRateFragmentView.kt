package at.shockbytes.corey.ui.fragment.body

import at.shockbytes.corey.R
import at.shockbytes.corey.common.roundDouble
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.data.body.bmr.Bmr
import kotlinx.android.synthetic.main.fragment_body_view_bmr.*
import kotlinx.android.synthetic.main.fragment_body_view_dream_weight.*
import java.util.Random

/**
 * Author:  Martin Macheiner
 * Date:    05.03.2018
 */
class BasalMetabolicRateFragmentView : BodySubFragment() {

    override fun bindViewModel() = Unit
    override fun injectToGraph(appComponent: AppComponent?) = Unit
    override fun unbindViewModel() = Unit

    override val layoutId = R.layout.fragment_body_view_bmr

    override fun setupViews() = Unit

    fun setBmr(bmr: Bmr) {

        with(bmr) {

        }

        animateCard(fragment_body_card_bmr, 0)
    }

    companion object {

        fun newInstance() = BasalMetabolicRateFragmentView()
    }
}
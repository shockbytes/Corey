package at.shockbytes.corey.ui.fragment.body

import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.data.body.bmr.Bmr
import kotlinx.android.synthetic.main.fragment_body_view_bmr.*

/**
 * Author:  Martin Macheiner
 * Date:    20.09.2020
 */
class BasalMetabolicRateFragmentView : BodySubFragment() {

    override fun bindViewModel() = Unit
    override fun injectToGraph(appComponent: AppComponent?) = Unit
    override fun unbindViewModel() = Unit

    override val layoutId = R.layout.fragment_body_view_bmr

    override fun setupViews() = Unit

    fun setBmr(bmr: Bmr) {

        with(bmr) {
            tv_fragment_body_card_bmr_current_bmr.text = getString(R.string.kcal_format_whitespace, kcal)
            tv_fragment_body_card_bmr_active_bmr.text = getString(R.string.kcal_format_whitespace, kcalWithActivityFactor)
            tv_fragment_body_card_bmr_computation_name.text = getString(R.string.body_card_bmr_computation_method, computationAlgorithm)
        }

        animateCard(fragment_body_card_bmr, 0)
    }

    companion object {

        fun newInstance() = BasalMetabolicRateFragmentView()
    }
}
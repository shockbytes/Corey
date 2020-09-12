package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import at.shockbytes.corey.R
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.viewmodel.NutritionViewModel
import at.shockbytes.corey.util.viewModelOf
import com.github.florent37.viewanimator.ViewAnimator
import kotlinx.android.synthetic.main.fragment_add_nutrition_entry.*
import javax.inject.Inject

class AddNutritionEntryFragment : BaseFragment<AppComponent>() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: NutritionViewModel

    override val layoutId: Int = R.layout.fragment_add_nutrition_entry

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModelOf(vmFactory)
    }

    override fun bindViewModel() {

        layout_fragment_add_nutrition_entry.setOnClickListener {
            closeFragment()
        }
    }

    private fun closeFragment() {

        animateCardOut {
            fragmentManager?.popBackStack()
        }
    }

    private fun animateCardIn() {

        val fromTranslationY = 150f
        val fromAlpha = 0f

        card_fragment_add_nutrition_entry.apply {
            translationY = fromTranslationY
            alpha = fromAlpha
        }

        ViewAnimator.animate(card_fragment_add_nutrition_entry)
                .translationY(fromTranslationY, 0f)
                .alpha(fromAlpha, 1f)
                .startDelay(300)
                .decelerate()
                .duration(300)
                .start()
    }

    private fun animateCardOut(endAction: (() -> Unit)) {

        ViewAnimator.animate(card_fragment_add_nutrition_entry)
                .translationY(0f, -1000f)
                .alpha(1f, 0.0f)
                .accelerate()
                .duration(300)
                .onStop { endAction() }
                .start()
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun setupViews() {
        animateCardIn()
    }

    override fun unbindViewModel() = Unit

    companion object {

        fun newInstance(): AddNutritionEntryFragment = AddNutritionEntryFragment()
    }
}
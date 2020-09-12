package at.shockbytes.corey.ui.fragment.tab

import at.shockbytes.corey.R
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.viewmodel.NutritionViewModel
import javax.inject.Inject

class NutritionFragment : TabBaseFragment<AppComponent>() {

    @Inject
    protected lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: NutritionViewModel

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[NutritionViewModel::class.java]
    }

    override val layoutId: Int = R.layout.fragment_nutrition

    override fun bindViewModel() {
        viewModel.requestNutritionHistory()
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun setupViews() {
    }

    override fun unbindViewModel() {
    }

    companion object {

        fun newInstance(): NutritionFragment = NutritionFragment()
    }
}
package at.shockbytes.corey.ui.fragment.tab

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.setVisible
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.adapter.nutrition.NutritionAdapter
import at.shockbytes.corey.ui.viewmodel.NutritionViewModel
import at.shockbytes.corey.util.observePositionChanges
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_nutrition.*
import timber.log.Timber
import javax.inject.Inject

class NutritionFragment : TabBaseFragment<AppComponent>() {

    @Inject
    protected lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: NutritionViewModel

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    private lateinit var nutritionAdapter: NutritionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[NutritionViewModel::class.java]
    }

    override val layoutId: Int = R.layout.fragment_nutrition

    override fun bindViewModel() {
        viewModel.requestNutritionHistory()
                .subscribe(nutritionAdapter::updateData, Timber::e)
                .addTo(compositeDisposable)

        viewModel.getCurrentWeekOverview().observe(this, ::handleWeekOverview)
    }

    private fun handleWeekOverview(weekOverview: NutritionViewModel.WeekOverview) {
        with(weekOverview) {
            tv_fragment_nutrition_week.text = "Week $week" // TODO
            tv_fragment_nutrition_year.text = year.toString()

            tv_fragment_nutrition_balance.text = balance.formatted()

            if (percentageToPreviousWeek != null) {

                tv_fragment_nutrition_balance_percentage.apply {
                    setVisible(true)
                    text = "+${percentageToPreviousWeek}%" // TODO
                }
            } else {
                tv_fragment_nutrition_balance_percentage.setVisible(false)
            }
        }
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun setupViews() {

        nutritionAdapter = NutritionAdapter(requireContext())

        rv_nutrition_data.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
            adapter = nutritionAdapter
            PagerSnapHelper().attachToRecyclerView(this)

            observePositionChanges(subscribeOn = Schedulers.io())
                    .subscribe { position ->
                        nutritionAdapter
                                .data
                                .getOrNull(position)
                                ?.weekBundle
                                ?.let { (weekOfYear, year) ->
                                    viewModel.showHeaderFor(weekOfYear, year)
                                }
                    }
                    .addTo(compositeDisposable)
        }
    }

    override fun unbindViewModel() {
    }

    companion object {

        fun newInstance(): NutritionFragment = NutritionFragment()
    }
}
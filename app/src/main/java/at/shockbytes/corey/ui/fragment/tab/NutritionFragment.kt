package at.shockbytes.corey.ui.fragment.tab

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.shockbytes.core.util.CoreUtils.colored
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.setVisible
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.adapter.nutrition.NutritionAdapter
import at.shockbytes.corey.ui.adapter.nutrition.NutritionIntakeAdapterItem
import at.shockbytes.corey.ui.viewmodel.NutritionViewModel
import at.shockbytes.corey.util.LastItemBottomMarginItemDecoration
import at.shockbytes.corey.util.ShockColors
import at.shockbytes.corey.util.observePositionChanges
import at.shockbytes.util.AppUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_nutrition.*
import timber.log.Timber
import javax.inject.Inject

class NutritionFragment : TabBaseFragment<AppComponent>() {

    @Inject
    protected lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: NutritionViewModel

    override val castsActionBarShadow: Boolean = false

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

        viewModel.onModifyEntryEvent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::handleModifyEvent)
                .addTo(compositeDisposable)

        viewModel.getCurrentWeekOverview().observe(this, ::handleWeekOverview)
    }

    private fun handleWeekOverview(weekOverview: NutritionViewModel.WeekOverview) {
        pb_nutrition.setVisible(false)

        with(weekOverview) {
            tv_fragment_nutrition_week.text = getString(R.string.week_placeholder, week)
            tv_fragment_nutrition_year.text = year.toString()

            tv_fragment_nutrition_balance.text = balance.formatted()

            if (percentageToPreviousWeekFormatted() != null) {

                tv_fragment_nutrition_balance_percentage.apply {
                    setVisible(true)
                    text = percentageToPreviousWeekFormatted()
                }
            } else {
                tv_fragment_nutrition_balance_percentage.setVisible(false)
            }
        }
    }

    private fun handleModifyEvent(event: NutritionViewModel.ModifyEntryEvent) {
        when (event) {
            is NutritionViewModel.ModifyEntryEvent.Delete -> {
                showToast(getString(R.string.item_deleted, event.entryName))
            }
            is NutritionViewModel.ModifyEntryEvent.Error -> {
                showToast(R.string.unable_to_perform_action)
            }
        }
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun setupViews() {
        pb_nutrition.setVisible(true)

        nutritionAdapter = NutritionAdapter(requireContext(), ::requestNutritionEntryDeletion)

        rv_nutrition_data.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
            adapter = nutritionAdapter

            addItemDecoration(LastItemBottomMarginItemDecoration(AppUtils.convertDpInPixel(122, requireContext())))

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

    private fun requestNutritionEntryDeletion(intakeItem: NutritionIntakeAdapterItem.Intake) {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete)
                .setMessage(getString(R.string.delete_nutrition_entry_message, intakeItem.entry.name))
                .setIcon(R.drawable.ic_delete)
                .setNegativeButton(getString(R.string.cancel).colored(Color.BLACK)) { _, _ -> Unit }
                .setPositiveButton(getString(R.string.delete).colored(ShockColors.ERROR)) { _, _ ->
                    viewModel.deleteNutritionEntry(intakeItem.entry)
                }
                .create()
                .show()
    }

    override fun unbindViewModel() = Unit

    companion object {

        fun newInstance(): NutritionFragment = NutritionFragment()
    }
}
package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import at.shockbytes.corey.R
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.common.core.CoreyDate
import at.shockbytes.corey.common.hideKeyboard
import at.shockbytes.corey.data.nutrition.NutritionEntry
import at.shockbytes.corey.data.nutrition.NutritionTime
import at.shockbytes.corey.data.nutrition.PortionSize
import at.shockbytes.corey.data.nutrition.lookup.KcalLookupResult
import at.shockbytes.corey.ui.custom.selection.CoreySingleSelectionItem
import at.shockbytes.corey.ui.fragment.dialog.NutritionLookupBottomsheetFragment
import at.shockbytes.corey.ui.viewmodel.NutritionViewModel
import at.shockbytes.corey.util.viewModelOf
import com.github.florent37.viewanimator.ViewAnimator
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_add_nutrition_entry.*
import org.joda.time.DateTime
import timber.log.Timber
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
        viewModel.onModifyEntryEvent()
                .subscribe(::handleSaveEntryEvent, Timber::e)
                .addTo(compositeDisposable)

        viewModel.onKcalLookupEvent()
                .subscribe(::handleKcalLookupEvent, Timber::e)
                .addTo(compositeDisposable)
    }

    private fun handleSaveEntryEvent(event: NutritionViewModel.ModifyEntryEvent) {

        when (event) {
            is NutritionViewModel.ModifyEntryEvent.Save -> {
                showSnackbar(event.entryName)
                closeFragment()
            }
            is NutritionViewModel.ModifyEntryEvent.Error -> {
                showSnackbar(event.throwable.localizedMessage ?: "Unknown error!")
            }
        }
    }

    private fun handleKcalLookupEvent(state: NutritionViewModel.KcalLookupResultState) {
        when (state) {
            is NutritionViewModel.KcalLookupResultState.Success -> {
                showLookupResult(state.result)
            }
            is NutritionViewModel.KcalLookupResultState.NoResults -> {
                showSnackbar(getString(R.string.nutrition_lookup_not_found, state.searchedText))
            }
            is NutritionViewModel.KcalLookupResultState.Error -> {
                showToast(getString(R.string.nutrition_error, state.throwable.localizedMessage))
            }
        }
    }

    private fun showLookupResult(result: KcalLookupResult) {
        NutritionLookupBottomsheetFragment.newInstance(result)
                .setOnLookupItemSelectedListener { item ->
                    et_add_nutrition_entry_estimated_kcal.setText(item.formattedKcal)
                }
                .show(childFragmentManager, "bottomsheet-nutrition-lookup")
    }

    private fun closeFragment() {
        animateCardOut {
            parentFragmentManager.popBackStack()
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

        til_add_nutrition_entry_name.setEndIconOnClickListener {
            requireActivity().hideKeyboard()
            viewModel.lookupEstimatedKcal(gatherTitle())
        }

        cssv_fragment_add_nutrition_entry_portion.apply {
            data = PortionSize.values().map { size ->
                CoreySingleSelectionItem(getString(size.nameRes), size.ordinal, size.code)
            }
            selectPosition(1)
        }

        cssv_fragment_add_nutrition_entry_time.apply {
            data = NutritionTime.values().map { time ->
                CoreySingleSelectionItem(getString(time.nameRes), time.ordinal, time.code)
            }
            selectPosition(0)
        }

        RxTextView.textChanges(et_add_nutrition_entry_name)
                .map { it.isNotEmpty() }
                .subscribe(til_add_nutrition_entry_name::setEndIconVisible)
                .addTo(compositeDisposable)

        Observable
                .combineLatest(
                        RxTextView.textChanges(et_add_nutrition_entry_name),
                        RxTextView.textChanges(et_add_nutrition_entry_estimated_kcal),
                        { name, kcal -> name.isNotBlank() && kcal.isNotEmpty() }
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(btn_add_nutrition_entry::setEnabled)
                .addTo(compositeDisposable)

        btn_add_nutrition_entry.setOnClickListener {

            val nutritionEntry = gatherNutritionEntry()
            viewModel.addNutritionEntry(nutritionEntry)
        }

        layout_fragment_add_nutrition_entry.setOnClickListener {
            closeFragment()
        }
    }

    private fun gatherNutritionEntry(): NutritionEntry {

        val title = gatherTitle()
        val estimatedKcal = et_add_nutrition_entry_estimated_kcal.text.toString().toInt()

        val portionCode = cssv_fragment_add_nutrition_entry_portion.selectedItem().tag
        val timeCode = cssv_fragment_add_nutrition_entry_time.selectedItem().tag

        val year = dp_add_nutrition_entry.year
        val month = dp_add_nutrition_entry.month.inc()
        val day = dp_add_nutrition_entry.dayOfMonth
        val weekOfYear = DateTime(year, month, day, 0, 0).weekOfWeekyear

        return NutritionEntry(
                id = "",
                name = title,
                kcal = estimatedKcal,
                portionCode = portionCode,
                timeCode = timeCode,
                date = CoreyDate(year, month, day, weekOfYear)
        )
    }

    private fun gatherTitle(): String = et_add_nutrition_entry_name.text?.toString()!!

    override fun unbindViewModel() = Unit

    companion object {

        fun newInstance(): AddNutritionEntryFragment = AddNutritionEntryFragment()
    }
}
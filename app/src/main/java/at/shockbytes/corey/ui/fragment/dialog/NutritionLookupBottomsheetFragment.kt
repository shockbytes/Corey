package at.shockbytes.corey.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.shockbytes.core.image.ImageLoader
import at.shockbytes.corey.R
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.data.nutrition.lookup.KcalLookupItem
import at.shockbytes.corey.data.nutrition.lookup.KcalLookupResult
import at.shockbytes.corey.ui.adapter.nutrition.NutritionLookupAdapter
import at.shockbytes.util.adapter.BaseAdapter
import kotterknife.bindView
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    26.09.2020
 */
class NutritionLookupBottomsheetFragment : BottomSheetDialogFragment(),
    BaseAdapter.OnItemClickListener<KcalLookupItem> {

    @Inject
    protected lateinit var imageLoader: ImageLoader

    private val behaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
    }

    private val tvDataSource: TextView by bindView(R.id.tv_nutrition_lookup_source)
    private val rvLookup: RecyclerView by bindView(R.id.rv_nutrition_lookup_picker)

    private var listener: ((item: KcalLookupItem) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as CoreyApp).appComponent.inject(this)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.bottomsheet_fragment_nutrition_lookup, null)
        dialog.setContentView(contentView)
        (contentView.parent as View)
            .setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.addBottomSheetCallback(behaviorCallback)
        }
        setupViews()
    }

    override fun onItemClick(t: KcalLookupItem, position: Int, v: View) {
        listener?.invoke(t)
        dismiss()
    }

    fun setOnLookupItemSelectedListener(
        listener: (item: KcalLookupItem) -> Unit
    ): NutritionLookupBottomsheetFragment {
        return apply {
            this.listener = listener
        }
    }

    private fun setupViews() {
        arguments?.getParcelable<KcalLookupResult>(ARG_RESULT)?.let { item ->
            tvDataSource.text = getString(R.string.data_provided, item.dataSource.name)
            rvLookup.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = NutritionLookupAdapter(context, imageLoader) { item ->
                    listener?.invoke(item)
                    dismiss()
                }.apply {
                    data.apply {
                        clear()
                        addAll(item.items)
                    }
                }
            }
        }
    }

    companion object {

        private const val ARG_RESULT = "arg_result"

        fun newInstance(result: KcalLookupResult): NutritionLookupBottomsheetFragment {
            return NutritionLookupBottomsheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_RESULT, result)
                }
            }
        }
    }
}

package at.shockbytes.corey.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.ui.adapter.AddScheduleItemAdapter
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.data.schedule.ScheduleRepository
import at.shockbytes.util.adapter.BaseAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotterknife.bindView
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    24.02.2017
 */
class InsertScheduleDialogFragment : BottomSheetDialogFragment(), TextWatcher,
        BaseAdapter.OnItemClickListener<AddScheduleItemAdapter.ScheduleDisplayItem> {

    @Inject
    lateinit var scheduleManager: ScheduleRepository

    private val compositeDisposable = CompositeDisposable()

    private val editTextFilter: EditText by bindView(R.id.fragment_create_workout_bottom_sheet_edit_filter)
    private val recyclerView: RecyclerView by bindView(R.id.fragment_create_workout_bottom_sheet_recyclerview)

    private val behaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
    }

    private var addScheduleItemAdapter: AddScheduleItemAdapter? = null
    private var onScheduleItemSelectedListener: ((item: AddScheduleItemAdapter.ScheduleDisplayItem) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as CoreyApp).appComponent.inject(this)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.dialogfragment_add_exercises, null)
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

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        addScheduleItemAdapter?.filter(charSequence.toString())
    }

    override fun afterTextChanged(editable: Editable) = Unit

    override fun onItemClick(t: AddScheduleItemAdapter.ScheduleDisplayItem, position: Int, v: View) {
        onScheduleItemSelectedListener?.invoke(t)
        dismiss()
    }

    fun setOnScheduleItemSelectedListener(listener: ((item: AddScheduleItemAdapter.ScheduleDisplayItem) -> Unit)): InsertScheduleDialogFragment {
        this.onScheduleItemSelectedListener = listener
        return this
    }

    private fun setupViews() {

        addScheduleItemAdapter = AddScheduleItemAdapter(
                requireContext(),
                onItemClickListener = this
        ) { item, query ->
            item.item.title.contains(query)
        }

        recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
            adapter = addScheduleItemAdapter
        }

        scheduleManager.schedulableItems
            .map { data ->
                data.map { item ->
                    AddScheduleItemAdapter.ScheduleDisplayItem(item)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data ->
                addScheduleItemAdapter?.setData(data, false)
            }
            .addTo(compositeDisposable)

        editTextFilter.addTextChangedListener(this)
    }

    companion object {

        fun newInstance(): InsertScheduleDialogFragment {
            val fragment = InsertScheduleDialogFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}

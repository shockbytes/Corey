package at.shockbytes.corey.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
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
import io.reactivex.disposables.CompositeDisposable
import kotterknife.bindView
import timber.log.Timber
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

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
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
                .setBackgroundColor(ContextCompat.getColor(context!!, android.R.color.transparent))
        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(behaviorCallback)
        }
        setupViews()
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        addScheduleItemAdapter?.filter(charSequence.toString())
    }

    override fun afterTextChanged(editable: Editable) = Unit

    override fun onItemClick(t: AddScheduleItemAdapter.ScheduleDisplayItem, v: View) {
        onScheduleItemSelectedListener?.invoke(t)
        dismiss()
    }

    fun setOnScheduleItemSelectedListener(listener: ((item: AddScheduleItemAdapter.ScheduleDisplayItem) -> Unit)): InsertScheduleDialogFragment {
        this.onScheduleItemSelectedListener = listener
        return this
    }

    private fun setupViews() {

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        addScheduleItemAdapter = AddScheduleItemAdapter(context!!, listOf()) { item, query -> item.item.title.contains(query) }
        addScheduleItemAdapter?.onItemClickListener = this
        recyclerView.adapter = addScheduleItemAdapter

        scheduleManager.schedulableItems
                .map { data -> data.map { AddScheduleItemAdapter.ScheduleDisplayItem(it) } }
                .subscribe { data ->
                    Timber.d("Schedulable items size: ${data.size}")
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

package at.shockbytes.corey.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import at.shockbytes.corey.R
import at.shockbytes.corey.adapter.AddScheduleItemAdapter
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.schedule.ScheduleManager
import kotterknife.bindView
import java.util.*
import javax.inject.Inject

/**
 * @author Martin Macheiner
 * Date: 24.02.2017.
 */

class InsertScheduleDialogFragment : BottomSheetDialogFragment(),
        AddScheduleItemAdapter.OnItemClickListener, TextWatcher {

    @Inject
    protected lateinit var scheduleManager: ScheduleManager

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
    private var onScheduleItemSelectedListener: ((item: String, day: Int) -> Unit)? = null

    private var editDay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as CoreyApp).appComponent.inject(this)
        editDay = arguments!!.getInt(ARG_DAY)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.dialogfragment_add_exercises, null)
        dialog.setContentView(contentView)
        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(behaviorCallback)
        }
        setupViews()
    }

    override fun onItemClick(item: String, v: View) {
        onScheduleItemSelectedListener?.invoke(item, editDay)
        dismiss()
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        addScheduleItemAdapter?.filter(charSequence.toString())
    }

    override fun afterTextChanged(editable: Editable) {}

    fun setOnScheduleItemSelectedListener(listener: ((item: String, day: Int) -> Unit)): InsertScheduleDialogFragment {
        this.onScheduleItemSelectedListener = listener
        return this
    }

    private fun setupViews() {

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        addScheduleItemAdapter = AddScheduleItemAdapter(context, ArrayList())
        addScheduleItemAdapter?.setOnItemClickListener(this)
        recyclerView.adapter = addScheduleItemAdapter

        scheduleManager.itemsForScheduling.subscribe { data ->
            addScheduleItemAdapter?.setData(data, false)
        }

        editTextFilter.addTextChangedListener(this)
    }

    companion object {

        private const val ARG_DAY = "arg_day"

        fun newInstance(day: Int): InsertScheduleDialogFragment {
            val fragment = InsertScheduleDialogFragment()
            val args = Bundle()
            args.putInt(ARG_DAY, day)
            fragment.arguments = args
            return fragment
        }
    }

}

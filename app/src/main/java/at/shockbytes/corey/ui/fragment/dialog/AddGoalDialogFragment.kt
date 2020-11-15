package at.shockbytes.corey.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import at.shockbytes.corey.R
import at.shockbytes.corey.data.goal.Goal
import at.shockbytes.corey.ui.model.GoalItem
import at.shockbytes.corey.util.accentColored

/**
 * Author:  Martin Macheiner
 * Date:    08.03.2017
 */
class AddGoalDialogFragment : DialogFragment() {

    private lateinit var etGoal: EditText
    private lateinit var etMonth: EditText
    private lateinit var etYear: EditText
    private lateinit var spinnerCategory: Spinner

    private val spinnerCategories: Array<String> by lazy {
        resources.getStringArray(R.array.goal_category_names)
    }

    private var listener: ((Goal) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.set_goal)
                .setIcon(R.drawable.ic_tab_goals)
                .setView(createView())
                .setPositiveButton(getString(R.string.add).accentColored()) { _, _ ->
                    if (validateInput()) {
                        listener?.invoke(craftGoal())
                        dismiss()
                    }
                }
                .create()
    }

    fun setOnGoalCreatedListener(listener: (Goal) -> Unit): AddGoalDialogFragment {
        this.listener = listener
        return this
    }

    private fun createView(): View? {
        val layout = LayoutInflater.from(context)
                .inflate(R.layout.dialogfragment_add_goal, null, false)

        etGoal = layout.findViewById(R.id.tv_add_goal)
        etYear = layout.findViewById(R.id.tv_add_goal_year)
        etMonth = layout.findViewById(R.id.tv_add_goal_month)
        spinnerCategory = layout.findViewById(R.id.spinner_add_goal)

        spinnerCategory.adapter = ArrayAdapter<String>(layout.context, R.layout.item_spinner_add_goal, spinnerCategories)

        return layout
    }

    private fun validateInput(): Boolean {
        return etGoal.text.toString().isNotEmpty() &&
                etMonth.text.toString().toIntOrNull() != null &&
                etYear.text.toString().toIntOrNull() != null
    }

    private fun craftGoal(): Goal {
        val msg = etGoal.text.toString()
        val year = etYear.text.toString().toInt()
        val month = etMonth.text.toString().toInt()

        val dueDate = "$year.$month"
        val category = GoalItem.Category.values()[spinnerCategory.selectedItemPosition]
        val categoryString = GoalItem.categoryToString(category)

        return Goal(message = msg, dueDate = dueDate, category = categoryString)
    }

    companion object {

        fun newInstance(): AddGoalDialogFragment {
            val fragment = AddGoalDialogFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}

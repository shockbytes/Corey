package at.shockbytes.corey.ui.fragment.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import at.shockbytes.corey.R

/**
 * @author Martin Macheiner
 * Date: 08.03.2017.
 */

class AddGoalDialogFragment : DialogFragment() {

    private lateinit var editGoal: EditText

    private var listener: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(context!!)
                .setTitle(R.string.set_goal)
                .setIcon(R.mipmap.ic_launcher)
                .setView(createView())
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    if (editGoal.text.toString().isNotEmpty()) {
                        listener?.invoke(editGoal.text.toString())
                        dismiss()
                    }
                }
                .create()
    }

    fun setOnGoalMessageAddedListener(listener: (String) -> Unit): AddGoalDialogFragment {
        this.listener = listener
        return this
    }

    private fun createView(): View? {
        editGoal = LayoutInflater.from(context)
                .inflate(R.layout.dialogfragment_add_goal, null, false) as EditText
        return editGoal
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

package at.shockbytes.corey.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import at.shockbytes.corey.R
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.core.CoreyApp
import com.shawnlin.numberpicker.NumberPicker
import javax.inject.Inject

/**
 * @author Martin Macheiner
 * Date: 26.02.2017.
 */

class DesiredWeightDialogFragment : DialogFragment() {

    @Inject
    protected lateinit var bodyManager: BodyManager

    private lateinit var numberPickerWeight: NumberPicker

    private val dialogView: View
        get() {
            val v = LayoutInflater.from(context).inflate(R.layout.dialogfragment_dreamweight, null, false)
            numberPickerWeight = v as NumberPicker
            numberPickerWeight.formatter = NumberPicker.Formatter { value -> value.toString() + " " + bodyManager.weightUnit }
            return v
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as CoreyApp).appComponent.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(context!!)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.dialogfragment_enter_desiredweight_title)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val dreamWeight = numberPickerWeight.value
                    bodyManager.desiredWeight = dreamWeight
                    dismiss()
                }
                .setNegativeButton(R.string.dialogfragment_enter_desiredweight_neg_btn, null)
                .create()
    }

    companion object {

        fun newInstance(): DesiredWeightDialogFragment {
            val fragment = DesiredWeightDialogFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}

package at.shockbytes.corey.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import at.shockbytes.core.util.CoreUtils.colored
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.util.accentColored
import com.shawnlin.numberpicker.NumberPicker
import javax.inject.Inject

/**
 * @author Martin Macheiner
 * Date: 26.02.2017.
 */

class DesiredWeightDialogFragment : DialogFragment() {

    @Inject
    protected lateinit var bodyRepository: BodyRepository

    @Inject
    protected lateinit var userSettings: UserSettings

    private lateinit var numberPickerWeight: NumberPicker

    private val dialogView: View
        get() {
            val v = LayoutInflater.from(context).inflate(R.layout.dialogfragment_dreamweight, null, false)
            numberPickerWeight = v as NumberPicker
            numberPickerWeight.formatter = NumberPicker.Formatter { value -> value.toString() + " " + userSettings.weightUnit.blockingFirst() }
            return v
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as CoreyApp).appComponent.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setIcon(R.drawable.ic_notification_weigh)
                .setTitle(R.string.dialogfragment_enter_desiredweight_title)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.apply).accentColored()) { _, _ ->
                    val desiredWeight = numberPickerWeight.value
                    bodyRepository.setDesiredWeight(desiredWeight)
                    dismiss()
                }
                .setNegativeButton(
                    getString(R.string.dialogfragment_enter_desiredweight_neg_btn)
                        .colored(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText)),
                    null
                )
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

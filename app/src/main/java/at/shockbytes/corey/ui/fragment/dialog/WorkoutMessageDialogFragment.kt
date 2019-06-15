package at.shockbytes.corey.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import at.shockbytes.corey.R

/**
 * @author Martin Macheiner
 * Date: 26.02.2017.
 */

class WorkoutMessageDialogFragment : DialogFragment() {

    enum class MessageType {
        QUIT, DONE
    }

    private var msgType: MessageType? = null

    private var listener: (() -> Unit)? = null

    private val title: Int
        get() = if (msgType == MessageType.QUIT)
            R.string.workout_message_title_quit
        else
            R.string.workout_message_title_done

    private val message: Int
        get() = if (msgType == MessageType.QUIT)
            R.string.workout_message_msg_quit
        else
            R.string.workout_message_msg_done

    private val positiveText: Int
        get() = if (msgType == MessageType.QUIT)
            R.string.workout_message_pos_quit
        else
            R.string.workout_message_pos_done

    private val negativeText: Int
        get() = if (msgType == MessageType.QUIT)
            R.string.workout_message_neg_quit
        else
            R.string.workout_message_neg_done

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msgType = arguments!!.getSerializable(ARG_MSG_TYPE) as MessageType
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(context!!)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveText) { _, _ ->
                    listener?.invoke()
                    dismiss()
                }
                .setNegativeButton(negativeText, null)
                .create()
    }

    fun setOnMessageAgreeClickedListener(listener: () -> Unit): WorkoutMessageDialogFragment {
        this.listener = listener
        return this
    }

    companion object {

        private const val ARG_MSG_TYPE = "arg_message_type"

        fun newInstance(type: MessageType): WorkoutMessageDialogFragment {
            val fragment = WorkoutMessageDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_MSG_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }
}

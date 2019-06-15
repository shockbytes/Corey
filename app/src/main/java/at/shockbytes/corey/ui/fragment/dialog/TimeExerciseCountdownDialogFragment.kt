package at.shockbytes.corey.ui.fragment.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import at.shockbytes.corey.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Author:  Martin Macheiner
 * Date:    21.03.2015
 */
class TimeExerciseCountdownDialogFragment : DialogFragment() {

    private lateinit var txtTimer: TextView

    private var countdown: Int = 0
    private var listener: (() -> Unit)? = null

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        countdown = arguments?.getInt(ARG_SECONDS) ?: 5
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialogfragment_countdown, container, false)
        txtTimer = v.findViewById(R.id.dialogfragment_countdown_txt_timer)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    fun setCountdownCompleteListener(listener: (() -> Unit)): TimeExerciseCountdownDialogFragment {
        this.listener = listener
        return this
    }

    private fun setupTimer() {

        txtTimer.text = countdown.toString()

        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe {
                    countdown--
                    if (countdown == 0) {
                        listener?.invoke()
                        disposable?.dispose()
                        dismiss()
                    }
                    txtTimer.text = countdown.toString()
                }
    }

    companion object {

        private const val ARG_SECONDS = "arg_seconds"

        fun newInstance(seconds: Int): TimeExerciseCountdownDialogFragment {
            val fragment = TimeExerciseCountdownDialogFragment()
            val args = Bundle(1)
            args.putInt(ARG_SECONDS, seconds)
            fragment.arguments = args
            return fragment
        }
    }
}

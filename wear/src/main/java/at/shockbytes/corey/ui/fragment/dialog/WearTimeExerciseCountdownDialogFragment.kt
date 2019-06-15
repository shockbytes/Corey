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
import kotterknife.bindView
import java.util.concurrent.TimeUnit

/**
 * Author:  Martin Macheiner
 * Date:    21.03.2015.
 */
class WearTimeExerciseCountdownDialogFragment : androidx.fragment.app.DialogFragment() {

    private val txtTimer: TextView by bindView(R.id.dialogfr_countdown_txt_timer)

    private var countdown: Int = 0
    private var listener: ((Unit) -> Unit)? = null

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Dialog_Presentation)
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
        return inflater.inflate(R.layout.dialogfragment_wear_countdown, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    fun setCountdownCompleteListener(listener: (Unit) -> Unit): WearTimeExerciseCountdownDialogFragment {
        this.listener = listener
        return this
    }

    private fun initialize() {

        txtTimer.text = countdown.toString()

        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe {
                    countdown--
                    if (countdown <= 0) {
                        listener?.invoke(Unit)
                        disposable?.dispose()
                        dismiss()
                    }
                    txtTimer.text = countdown.toString()
                }
    }

    companion object {

        private const val ARG_SECONDS = "arg_seconds"

        fun newInstance(seconds: Int): WearTimeExerciseCountdownDialogFragment {
            val fragment = WearTimeExerciseCountdownDialogFragment()
            val args = Bundle(1)
            args.putInt(ARG_SECONDS, seconds)
            fragment.arguments = args
            return fragment
        }
    }
}

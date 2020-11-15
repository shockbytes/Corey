package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import at.shockbytes.corey.core.WearCoreyApp
import at.shockbytes.corey.dagger.WearAppComponent
import io.reactivex.disposables.CompositeDisposable

/**
 * Author:  Martin Macheiner
 * Date:    07.03.2017.
 */
abstract class WearableBaseFragment : androidx.fragment.app.Fragment() {

    protected val compositeDisposable = CompositeDisposable()

    protected abstract val layoutId: Int

    protected abstract fun bindViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectToGraph((activity?.application as WearCoreyApp).appComponent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    override fun onPause() {
        compositeDisposable.clear()
        super.onPause()
    }

    override fun onResume() {
        bindViewModel()
        super.onResume()
    }

    protected abstract fun setupViews()

    protected abstract fun injectToGraph(appComponent: WearAppComponent)

    protected fun showToast(text: String, showLong: Boolean = true) {
        val duration = if (showLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(context, text, duration).show()
    }

    protected fun showToast(text: Int, showLong: Boolean = true) {
        showToast(getString(text), showLong)
    }
}
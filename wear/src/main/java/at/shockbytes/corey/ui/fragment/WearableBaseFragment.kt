package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import at.shockbytes.corey.core.WearCoreyApp
import at.shockbytes.corey.dagger.WearAppComponent

/**
 * Author:  Martin Macheiner
 * Date:    07.03.2017.
 */
abstract class WearableBaseFragment : Fragment() {

    abstract val layoutId: Int

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
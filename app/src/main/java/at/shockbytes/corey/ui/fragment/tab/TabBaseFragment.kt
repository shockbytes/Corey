package at.shockbytes.corey.ui.fragment.tab

import at.shockbytes.core.ShockbytesInjector
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.ui.activity.MainActivity

abstract class TabBaseFragment<T : ShockbytesInjector> : BaseFragment<T>() {

    protected open val castsActionBarShadow: Boolean = true

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.shouldCastActionBarShadow(castsActionBarShadow)
    }
}
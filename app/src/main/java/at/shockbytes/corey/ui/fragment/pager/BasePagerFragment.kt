package at.shockbytes.corey.ui.fragment.pager

import at.shockbytes.corey.ui.fragment.BaseFragment

/**
 * @author  Martin Macheiner
 * Date:    11.03.2018
 */


abstract class BasePagerFragment: BaseFragment() {

    abstract fun registerForLiveEvents()

    abstract fun unregisterForLiveEvents()


    override fun onStart() {
        super.onStart()
        registerForLiveEvents()
    }

    override fun onStop() {
        super.onStop()
        unregisterForLiveEvents()
    }

}
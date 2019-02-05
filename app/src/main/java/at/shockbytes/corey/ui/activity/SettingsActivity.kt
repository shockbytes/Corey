package at.shockbytes.corey.ui.activity

import android.content.Context
import android.content.Intent
import at.shockbytes.core.ui.activity.base.ContainerBackNavigableActivity
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.SettingsFragment

/**
 * Author:  Martin Macheiner
 * Date:    27.10.2015
 */
class SettingsActivity : ContainerBackNavigableActivity<AppComponent>() {

    override val displayFragment = SettingsFragment.newInstance()

    override fun bindViewModel() = Unit
    override fun unbindViewModel() = Unit

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}

package at.shockbytes.corey.ui.activity

import android.content.Context
import android.content.Intent
import at.shockbytes.corey.ui.activity.core.ContainerBackNavigableActivity
import at.shockbytes.corey.ui.fragment.SettingsFragment

/**
 * @author Martin Macheiner
 * Date: 27.10.2015.
 */
class SettingsActivity : ContainerBackNavigableActivity() {

    override val displayFragment = SettingsFragment.newInstance()

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

}

package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import android.preference.PreferenceFragment

import at.shockbytes.corey.R

/**
 * @author  Martin Macheiner
 * Date:    27.10.2015.
 */
class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}

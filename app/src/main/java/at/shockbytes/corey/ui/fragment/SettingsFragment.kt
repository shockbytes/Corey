package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import at.shockbytes.corey.R

/**
 * Author:  Martin Macheiner
 * Date:    27.10.2015.
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}

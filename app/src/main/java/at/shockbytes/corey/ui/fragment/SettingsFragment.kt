package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat

import at.shockbytes.corey.R
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.data.schedule.ScheduleRepository
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    27.10.2015.
 */
class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    @Inject
    protected lateinit var scheduleManager: ScheduleRepository

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        (activity?.application as? CoreyApp)?.appComponent?.inject(this)
        addPreferencesFromResource(R.xml.settings)

        findPreference(getString(R.string.prefs_workout_day_notification_daytime_key))
                .onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(p0: Preference?, p1: Any?): Boolean {
        // No matter what changed, just poke the ScheduleManager to update its timing
        scheduleManager.poke()
        return true
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}

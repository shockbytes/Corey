package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment

import at.shockbytes.corey.R
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.schedule.ScheduleManager
import javax.inject.Inject

/**
 * @author  Martin Macheiner
 * Date:    27.10.2015.
 */
class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {

    @Inject
    protected lateinit var scheduleManager: ScheduleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as CoreyApp).appComponent.inject(this)
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

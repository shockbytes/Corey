package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import at.shockbytes.core.ShockbytesApp
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.Gender
import at.shockbytes.corey.common.core.WeightUnit
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.dagger.AppComponent
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    27.10.2015
 */
@Suppress("UNCHECKED_CAST")
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var userSettings: UserSettings

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as? ShockbytesApp<AppComponent>)?.appComponent?.inject(this)
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onResume() {
        super.onResume()

        findPreference<ListPreference>(getString(R.string.prefs_key_gender))
                ?.setOnPreferenceChangeListener { _, newValue ->
                    syncGender(newValue as String)
                    true
                }

        findPreference<ListPreference>(getString(R.string.prefs_key_activity_level))
                ?.setOnPreferenceChangeListener { _, newValue ->
                    syncActivityLevel((newValue as String).toInt())
                    true
                }

        findPreference<ListPreference>(getString(R.string.prefs_key_weight_unit))
                ?.setOnPreferenceChangeListener { _, newValue ->
                    syncWeightUnit((newValue as String))
                    true
                }

        findPreference<EditTextPreference>(getString(R.string.prefs_key_birthday))
                ?.setOnPreferenceChangeListener { _, newValue ->
                    syncBirthday(newValue as String)
                    true
                }
    }

    private fun syncGender(genderStr: String) {
        userSettings.synchronizeGender(Gender.of(genderStr))
                .subscribe()
                .addTo(compositeDisposable)
    }

    private fun syncWeightUnit(weightUnitStr: String) {
        userSettings.synchronizeWeightUnit(WeightUnit.of(weightUnitStr))
                .subscribe()
                .addTo(compositeDisposable)
    }

    private fun syncActivityLevel(level: Int) {
        userSettings.synchronizeActivityLevel(ActivityLevel.ofLevel(level))
                .subscribe()
                .addTo(compositeDisposable)
    }

    private fun syncBirthday(birthday: String) {
        userSettings.synchronizeBirthdayFromString(birthday)
                .subscribe()
                .addTo(compositeDisposable)
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}

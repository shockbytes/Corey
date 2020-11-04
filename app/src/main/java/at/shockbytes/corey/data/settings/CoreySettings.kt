package at.shockbytes.corey.data.settings

import android.content.Context
import android.content.SharedPreferences
import at.shockbytes.corey.R
import at.shockbytes.corey.common.util.delegate.stringDelegate

/**
 * Author:  Martin Macheiner
 * Date:    11.02.2018
 */
class CoreySettings(
    context: Context,
    prefs: SharedPreferences
) {

    private val darkModeString: String by prefs.stringDelegate(context.getString(R.string.prefs_dark_mode_key), defaultValue = "system")
    private val accessModeString: String by prefs.stringDelegate(context.getString(R.string.prefs_key_firebase_access), defaultValue = "single_access")

    val themeState: ThemeState
        get() = ThemeState.ofString(darkModeString) ?: ThemeState.SYSTEM

    val firebaseAccessMode: FirebaseDatabaseAccessMode
        get() = FirebaseDatabaseAccessMode.ofString(accessModeString) ?: FirebaseDatabaseAccessMode.SINGLE_ACCESS
}
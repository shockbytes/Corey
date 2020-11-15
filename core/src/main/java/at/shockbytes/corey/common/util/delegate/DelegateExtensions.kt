package at.shockbytes.corey.common.util.delegate

import android.content.SharedPreferences

fun SharedPreferences.stringDelegate(
    key: String,
    defaultValue: String = ""
) = SharedPreferencesStringPropertyDelegate(this, key, defaultValue)
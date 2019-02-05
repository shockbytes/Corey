package at.shockbytes.corey.util

import android.content.res.Configuration
import android.support.v4.app.Fragment

fun Fragment.isPortrait(): Boolean {
    return this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}
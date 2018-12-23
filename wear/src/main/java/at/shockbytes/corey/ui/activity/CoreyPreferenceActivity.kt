package at.shockbytes.corey.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle

import at.shockbytes.corey.R
import preference.WearPreferenceActivity

/**
 * Author:  Martin Macheiner
 * Date:    23.03.2017.
 */

class CoreyPreferenceActivity : WearPreferenceActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, CoreyPreferenceActivity::class.java)
        }
    }
}

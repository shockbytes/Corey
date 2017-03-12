package at.shockbytes.corey.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import at.shockbytes.corey.R;

/**
 * @author Martin Macheiner
 *         Date: 27.10.2015.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}

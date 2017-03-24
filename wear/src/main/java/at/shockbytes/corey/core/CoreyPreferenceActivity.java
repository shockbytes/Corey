package at.shockbytes.corey.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import at.shockbytes.corey.R;
import preference.WearPreferenceActivity;

/**
 * @author Martin Macheiner
 *         Date: 23.03.2017.
 */

public class CoreyPreferenceActivity extends WearPreferenceActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, CoreyPreferenceActivity.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

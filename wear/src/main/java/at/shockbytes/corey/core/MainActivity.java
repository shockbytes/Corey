package at.shockbytes.corey.core;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import at.shockbytes.corey.R;
import at.shockbytes.corey.fragment.MainFragment;

public class MainActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((WearCoreyApp) getApplication()).getAppComponent().inject(this);
        setAmbientEnabled();

        getFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commit();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        // TODO Maybe do something for ambient mode
    }
}

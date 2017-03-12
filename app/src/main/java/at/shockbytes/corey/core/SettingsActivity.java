package at.shockbytes.corey.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;

import at.shockbytes.corey.fragment.SettingsFragment;

/**
 * @author Martin Macheiner
 *         Date: 27.10.2015.
 */
public class SettingsActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            getWindow().setExitTransition(new Slide(Gravity.BOTTOM));
            getWindow().setEnterTransition(new Explode());
        }
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                finishAfterTransition();
            }
            else{
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

}

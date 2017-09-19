package at.shockbytes.corey.core;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;

import at.shockbytes.corey.R;
import at.shockbytes.corey.fragment.RunningFragment;
import at.shockbytes.corey.util.AppParams;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RunningActivity extends AppCompatActivity {

    public enum RunningMode {
        FREE_RUN, DISTANCE, TIME, CALORIES
    }

    private static final String ARG_MODE = "arg_mode";

    public static Intent newIntent(Context context, RunningMode mode) {
        return new Intent(context, RunningActivity.class).putExtra(ARG_MODE, mode);
    }

    @Bind(R.id.running_toolbar)
    protected Toolbar toolbar;

    private RunningMode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            getWindow().setExitTransition(new Slide(Gravity.BOTTOM));
            getWindow().setEnterTransition(new Explode());
        }
        setContentView(R.layout.activity_running);
        ButterKnife.bind(this);
        setupActionBar();

        mode = (RunningMode) getIntent().getSerializableExtra(ARG_MODE);
        // TODO Switch mode
        showFragment(RunningFragment.newInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_running);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel);
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.running_content, fragment)
                .commit();
    }

    public void animateToolbar() {

        int primary = ContextCompat.getColor(this, R.color.colorPrimary);
        int bg = ContextCompat.getColor(this, R.color.help_background);

        // Color animation
        ObjectAnimator toolbarAnimatorIn = ObjectAnimator.ofObject(toolbar, "backgroundColor",
                new ArgbEvaluator(), primary, bg)
                .setDuration(400);
        ObjectAnimator toolbarAnimatorOut = ObjectAnimator.ofObject(toolbar, "backgroundColor",
                new ArgbEvaluator(), bg, primary)
                .setDuration(400);
        toolbarAnimatorOut.setStartDelay(AppParams.HELP_SHOW_DELAY);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(toolbarAnimatorIn, toolbarAnimatorOut);
        set.start();
    }

}

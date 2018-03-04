package at.shockbytes.corey.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import at.shockbytes.corey.R;
import at.shockbytes.corey.ui.fragment.SignUpFragment;

public class SignUpActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, SignUpFragment.Companion.newInstance())
                .commit();
    }
}

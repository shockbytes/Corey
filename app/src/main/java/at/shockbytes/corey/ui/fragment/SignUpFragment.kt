package at.shockbytes.corey.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.activity.MainActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_signup.*

class SignUpFragment : BaseFragment<AppComponent>() {

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    override val layoutId = R.layout.fragment_signup

    override fun injectToGraph(appComponent: AppComponent?) {
        // Do nothing
    }

    override fun onStart() {
        super.onStart()

        // Put this call into the ViewModel
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            openMainActivityWithoutAnimation()
        }
    }

    private fun onClickSignup() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                            listOf(
                                AuthUI.IdpConfig.GoogleBuilder()
                                        .build(),
                                AuthUI.IdpConfig.EmailBuilder()
                                        .setAllowNewAccounts(true)
                                        .setRequireName(true)
                                        .build()
                            )
                        )
                        .setIsSmartLockEnabled(true)
                        .build(),
                RC_SIGN_IN
        )
    }

    override fun bindViewModel() {
    }

    override fun unbindViewModel() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                openMainActivity()
            } else {
                if (response?.error != null) {
                    showSnackbar(response.error?.localizedMessage
                            ?: getString(R.string.unknown_error))
                } else {
                    // Sign in failed --> User pressed back button
                    showSnackbar(getString(R.string.sign_in_cancelled))
                }
            }
        }
    }

    private fun openMainActivity() {
        activity?.let { act ->
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(act)
            startActivity(MainActivity.newIntent(act), options.toBundle())
            act.supportFinishAfterTransition()
        }
    }

    private fun openMainActivityWithoutAnimation() {
        context?.let { ctx ->
            startActivity(MainActivity.newIntent(ctx))
            activity?.supportFinishAfterTransition()
        }
    }

    override fun setupViews() {
        fragment_signup_login.setOnClickListener {
            onClickSignup()
        }
    }

    companion object {

        private const val RC_SIGN_IN = 0x2341

        fun newInstance(): SignUpFragment {
            val fragment = SignUpFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}

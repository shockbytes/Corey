package at.shockbytes.corey.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.activity.MainActivity
import butterknife.OnClick
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_signup

    override fun injectToGraph(appComponent: AppComponent) {
        // Do nothing
    }

    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            openMainActivityWithoutAnimation()
        }
    }

    @OnClick(R.id.fragment_signup_login)
    fun onClickSignup() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(listOf(
                                AuthUI.IdpConfig.GoogleBuilder()
                                        .build(),
                                AuthUI.IdpConfig.EmailBuilder()
                                        .setAllowNewAccounts(true)
                                        .setRequireName(true)
                                        .build()))
                        .setIsSmartLockEnabled(true)
                        .setLogo(R.mipmap.ic_launcher)
                        .build(),
                RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

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
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!)
        startActivity(MainActivity.newIntent(activity), options.toBundle())
        activity?.supportFinishAfterTransition()
    }

    private fun openMainActivityWithoutAnimation() {
        startActivity(MainActivity.newIntent(activity))
        activity?.supportFinishAfterTransition()
    }

    override fun setupViews() {
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
}// Required empty public constructor

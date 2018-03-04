package at.shockbytes.corey.ui.activity

import android.support.v4.app.Fragment
import at.shockbytes.corey.ui.activity.core.ContainerBackNavigableActivity
import at.shockbytes.corey.ui.fragment.SignUpFragment

class SignUpActivity : ContainerBackNavigableActivity() {

    override val displayFragment: Fragment
        get() = SignUpFragment.newInstance()

}

package at.shockbytes.corey.ui.activity

import android.support.v4.app.Fragment
import at.shockbytes.corey.ui.activity.core.ContainerBackNavigableActivityCompat
import at.shockbytes.corey.ui.fragment.SignUpFragment

class SignUpActivity : ContainerBackNavigableActivityCompat() {

    override val displayFragment: Fragment
        get() = SignUpFragment.newInstance()

}

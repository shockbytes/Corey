package at.shockbytes.corey.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import at.shockbytes.core.ui.activity.base.ContainerBackNavigableActivity
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.SignUpFragment

class SignUpActivity : ContainerBackNavigableActivity<AppComponent>() {

    override fun bindViewModel() = Unit
    override fun unbindViewModel() = Unit

    override val displayFragment: Fragment
        get() = SignUpFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Signup)
        super.onCreate(savedInstanceState)
    }
}

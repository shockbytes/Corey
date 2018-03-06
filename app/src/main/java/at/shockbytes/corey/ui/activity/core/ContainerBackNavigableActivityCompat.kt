package at.shockbytes.corey.ui.activity.core

import android.os.Bundle
import android.support.v4.app.Fragment
import at.shockbytes.corey.dagger.AppComponent

/**
 * @author Martin Macheiner
 * Date: 23.12.2017.
 */
abstract class ContainerBackNavigableActivityCompat : BackNavigableActivity() {

    abstract val displayFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, displayFragment)
                .commit()
    }

    override fun injectToGraph(appComponent: AppComponent) {
        // Do nothing, nothings needs to be injected
    }

}
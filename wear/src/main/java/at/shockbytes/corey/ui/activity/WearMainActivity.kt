package at.shockbytes.corey.ui.activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.CoreyNavigationAdapter
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.core.CommunicationManager
import at.shockbytes.corey.core.WearCoreyApp
import at.shockbytes.corey.ui.fragment.WearRunningFragment
import at.shockbytes.corey.ui.fragment.WorkoutOverviewFragment
import kotterknife.bindView
import javax.inject.Inject
import kotlin.collections.ArrayList
import androidx.wear.ambient.AmbientModeSupport

class WearMainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return object : AmbientModeSupport.AmbientCallback() {
        }
    }

    interface OnWorkoutsLoadedListener {

        fun onWorkoutLoaded(workouts: List<Workout>)
    }

    @Inject
    protected lateinit var communicationManager: CommunicationManager

    private val navigationDrawer: WearableNavigationDrawerView by bindView(R.id.main_navigation_drawer)

    private var workoutListener: OnWorkoutsLoadedListener? = null

    private val navigationItems: List<CoreyNavigationAdapter.NavigationItem>
        get() = listOf(
            CoreyNavigationAdapter.NavigationItem(R.string.navigation_workout,
                R.drawable.ic_workout),
            CoreyNavigationAdapter.NavigationItem(R.string.navigation_running,
                R.drawable.ic_tab_running),
            CoreyNavigationAdapter.NavigationItem(R.string.navigation_settings,
                R.drawable.ic_settings_white)
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as WearCoreyApp).appComponent.inject(this)
        val controller = AmbientModeSupport.attach(this)

        setupNavigationDrawer()

        communicationManager.connectIfDeviceAvailable {
            workoutListener?.onWorkoutLoaded(it)
        }

        onNavigationItemSelected(0)
    }

    override fun onStart() {
        super.onStart()
        communicationManager.onStart()
    }

    override fun onPause() {
        super.onPause()
        communicationManager.onPause()
    }

    private fun onNavigationItemSelected(index: Int) {
        when (index) {
            0 -> showWorkoutFragment()
            1 -> showRunningFragment()
            2 -> showSettings()
        }
    }

    private fun showWorkoutFragment() {
        val workoutOverviewFragment = WorkoutOverviewFragment
            .newInstance(ArrayList(communicationManager.cachedWorkouts))
        workoutListener = workoutOverviewFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, workoutOverviewFragment)
            .commit()
    }

    private fun showRunningFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, WearRunningFragment.newInstance())
            .commit()
    }

    private fun showSettings() {
        startActivity(CoreyPreferenceActivity.newIntent(this))
    }

    private fun setupNavigationDrawer() {
        navigationDrawer.setAdapter(CoreyNavigationAdapter(this, navigationItems))
        navigationDrawer.addOnItemSelectedListener { onNavigationItemSelected(it) }
        navigationDrawer.controller.peekDrawer()
        navigationDrawer.setCurrentItem(0, true)
    }
}

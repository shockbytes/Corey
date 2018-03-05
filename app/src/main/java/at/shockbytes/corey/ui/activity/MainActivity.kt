package at.shockbytes.corey.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import at.shockbytes.corey.R
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.common.core.util.WatchInfo
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.activity.core.BaseActivity
import at.shockbytes.corey.ui.fragment.BodyFragment
import at.shockbytes.corey.ui.fragment.ScheduleFragment
import at.shockbytes.corey.ui.fragment.WorkoutOverviewFragment
import at.shockbytes.corey.ui.fragment.dialogs.DesiredWeightDialogFragment
import at.shockbytes.corey.user.UserManager
import at.shockbytes.corey.util.AppParams
import at.shockbytes.corey.util.schedule.ScheduleManager
import at.shockbytes.corey.wearable.WearableManager
import at.shockbytes.corey.workout.WorkoutManager
import at.shockbytes.util.AppUtils
import icepick.Icepick
import icepick.State
import kotterknife.bindView
import javax.inject.Inject

class MainActivity : BaseActivity(), TabLayout.OnTabSelectedListener {

    @State
    @JvmField
    protected var tabPosition: Int = 0

    @Inject
    protected lateinit var bodyManager: BodyManager

    @Inject
    protected lateinit var workoutManager: WorkoutManager

    @Inject
    protected lateinit var scheduleManager: ScheduleManager

    @Inject
    protected lateinit var wearableManager: WearableManager

    @Inject
    protected lateinit var userManager: UserManager


    private lateinit var menuItemAccount: MenuItem
    private lateinit var menuItemWatch: MenuItem

    private var watchInfo: WatchInfo = WatchInfo(null, false) // default is not connected

    private val mainLayout: View by bindView(R.id.main_layout)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val appBar: AppBarLayout by bindView(R.id.main_appbar)
    private val tabLayout: TabLayout by bindView(R.id.main_tablayout)
    private val fabNewWorkout: FloatingActionButton by bindView(R.id.main_fab_edit)

    private val nodeStateChangedListener: ((WatchInfo) -> Unit) = {
        watchInfo = it
        setupWatchMenuItem()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabPosition = 1 // Initialize it with one, can be overwritten by statement below
        Icepick.restoreInstanceState(this, savedInstanceState)

        initializeViews()
        bodyManager.poke(this)
        workoutManager.poke()
        scheduleManager.poke()

        if (bodyManager.desiredWeight <= 0) { // Ask for desired weight when not set
            askForDesiredWeight()
        }
    }

    override fun injectToGraph(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menuItemAccount = menu.findItem(R.id.action_account)
        menuItemWatch = menu.findItem(R.id.action_watch)

        // Do this here, because here is the only place where
        // the menu item is already initialized
        setupPersonalMenuItem()
        setupWatchMenuItem()

        return true
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> activityTransition(SettingsActivity.newIntent(applicationContext), -1)
            R.id.action_logout -> signOut()
            R.id.action_desired_weight -> askForDesiredWeight()
            R.id.action_watch -> showToast(menuItemWatch.title.toString())
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        wearableManager.onStart(nodeStateChangedListener)
    }

    override fun onPause() {
        super.onPause()
        wearableManager.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == AppParams.REQUEST_CODE_CREATE_WORKOUT && resultCode == Activity.RESULT_OK) {

            val w = data.getParcelableExtra<Workout>(AppParams.INTENT_EXTRA_NEW_WORKOUT)
            val isUpdated = data.getBooleanExtra(AppParams.INTENT_EXTRA_WORKOUT_UPDATED, false)

            if (!isUpdated) {
                workoutManager.addWorkout(w)
            } else {
                workoutManager.updateWorkout(w)
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {

        appBar.setExpanded(true, true)
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)

        tabPosition = tab.position
        when (tabPosition) {
            0 -> {
                fabNewWorkout.show()
                ft.replace(R.id.main_content, WorkoutOverviewFragment.newInstance())
            }
            1 -> {
                fabNewWorkout.hide()
                ft.replace(R.id.main_content, ScheduleFragment.newInstance())
            }
            2 -> {
                fabNewWorkout.hide()
                ft.replace(R.id.main_content, BodyFragment.newInstance())
            }
        }
        ft.commit()
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
    }

    // --------------------------------------------------------------------------------

    private fun initializeViews() {

        setSupportActionBar(toolbar)

        // Setup TabLayout
        tabLayout.addOnTabSelectedListener(this)
        val initialTab = tabLayout.getTabAt(tabPosition)
        initialTab?.select()

        fabNewWorkout.setOnClickListener {
            activityTransition(CreateWorkoutActivity.newIntent(applicationContext, null),
                    AppParams.REQUEST_CODE_CREATE_WORKOUT)
        }
    }

    private fun setupPersonalMenuItem() {
        userManager.loadAccountImage(this)
                .subscribe({ bm ->
                    menuItemAccount.icon = AppUtils.createRoundedBitmap(this, bm)
                }, { throwable: Throwable ->
                    throwable.printStackTrace()
                    showToast(R.string.error_account_photo)
                })
        menuItemAccount.title = userManager.user.name
        menuItemAccount.isEnabled = true
    }

    private fun setupWatchMenuItem() {
        menuItemWatch.isVisible = watchInfo.isConnected
        menuItemWatch.title = getString(R.string.watch_connected,watchInfo.name) ?: getString(R.string.menu_main_watch)
    }

    private fun askForDesiredWeight() {
        DesiredWeightDialogFragment.newInstance().show(supportFragmentManager, "frag-desired-weight")
    }

    private fun activityTransition(intent: Intent, reqCodeForResult: Int) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        if (reqCodeForResult > 0) {
            startActivityForResult(intent, reqCodeForResult, options.toBundle())
        } else {
            startActivity(intent, options.toBundle())
        }
    }

    private fun signOut() {
        userManager.signOut()
        supportFinishAfterTransition()
    }


    companion object {

        fun newIntent(context: FragmentActivity?): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}

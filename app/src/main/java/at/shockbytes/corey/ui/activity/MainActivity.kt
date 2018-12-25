package at.shockbytes.corey.ui.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.PagerAdapter
import at.shockbytes.core.image.GlideImageLoader
import at.shockbytes.core.image.ImageLoader
import at.shockbytes.core.model.ShockbytesUser
import at.shockbytes.core.ui.activity.BottomNavigationBarActivity
import at.shockbytes.core.ui.model.AdditionalToolbarAction
import at.shockbytes.core.ui.model.BottomNavigationActivityOptions
import at.shockbytes.core.ui.model.BottomNavigationTab
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.CoreyPagerAdapter
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.data.goal.Goal
import at.shockbytes.corey.ui.fragment.MenuFragment
import at.shockbytes.corey.ui.fragment.dialog.AddGoalDialogFragment
import at.shockbytes.corey.ui.fragment.dialog.DesiredWeightDialogFragment
import at.shockbytes.corey.ui.viewmodel.MainViewModel
import at.shockbytes.corey.util.AppParams
import javax.inject.Inject

class MainActivity : BottomNavigationBarActivity<AppComponent>() {

    @Inject
    protected lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel

    override val imageLoader: ImageLoader = GlideImageLoader(R.drawable.ic_account)

    override val options: BottomNavigationActivityOptions by lazy {
        BottomNavigationActivityOptions(
                tabs = listOf(
                        BottomNavigationTab(R.id.nav_item_workout, R.drawable.navigation_item, R.drawable.ic_tab_workout, getString(R.string.tab_workout)),
                        BottomNavigationTab(R.id.nav_item_schedule, R.drawable.navigation_item, R.drawable.ic_tab_schedule, getString(R.string.tab_schedule)),
                        BottomNavigationTab(R.id.nav_item_my_body, R.drawable.navigation_item, R.drawable.ic_tab_my_body, getString(R.string.tab_my_body)),
                        BottomNavigationTab(R.id.nav_item_goals, R.drawable.navigation_item, R.drawable.ic_tab_goals, getString(R.string.tab_goals))
                ),
                defaultTab = R.id.nav_item_schedule,
                appName = getString(R.string.app_name),
                viewPagerOffscreenLimit = 3,
                appTheme = R.style.AppTheme_NoActionBar,
                fabMenuId = R.menu.menu_fab,
                fabMenuColorList = listOf(R.color.colorAccent, R.color.material_red),
                fabVisiblePageIndices = listOf(0, 3),
                overflowIcon = R.drawable.ic_overflow_white,
                additionalToolbarAction = AdditionalToolbarAction(R.drawable.ic_body_card_weight_history),
                toolbarColor = R.color.colorPrimary,
                toolbarItemColor = R.color.toolbar_item_color,
                fabClosedIcon = R.drawable.ic_add,
                fabOpenedIcon = R.drawable.ic_cancel,
                navigationBarColor = R.color.navigation_bar_color,
                navigationItemTextColor = R.color.navigation_bar_item_text,
                navigationItemTintColor = R.color.navigation_bar_item_text
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]
    }

    override fun bindViewModel() {
        viewModel.getUserEvent().observe(this, Observer {
            it?.let { event ->
                onUserEvent(event)
            }
        })
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun onAdditionalToolbarActionClicked() {
        askForDesiredWeight()
    }

    override fun onFabMenuItemClicked(id: Int): Boolean {

        return when (id) {

            R.id.menu_fab_create_workout -> {
                activityTransition(CreateWorkoutActivity.newIntent(applicationContext),
                        AppParams.REQUEST_CODE_CREATE_WORKOUT)
                false
            }
            R.id.menu_fab_new_goal -> {
                AddGoalDialogFragment.newInstance()
                        .setOnGoalMessageAddedListener { msg ->
                            viewModel.storeBodyGoal(Goal(msg, false, ""))
                        }
                        .show(supportFragmentManager, "dialog-fragment-add-goal")
                false
            }
            else -> false
        }
    }

    override fun setupDarkMode() = Unit

    override fun setupPagerAdapter(tabs: List<BottomNavigationTab>): PagerAdapter {
        return CoreyPagerAdapter(supportFragmentManager, tabs)
    }

    override fun showLoginScreen() = Unit

    override fun showMenuFragment() {
        MenuFragment.newInstance().show(supportFragmentManager, "menu-fragment")
    }

    override fun showWelcomeScreen(user: ShockbytesUser) = Unit

    override fun unbindViewModel() = Unit

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == AppParams.REQUEST_CODE_CREATE_WORKOUT && resultCode == Activity.RESULT_OK) {

            val isUpdated = data?.getBooleanExtra(AppParams.INTENT_EXTRA_WORKOUT_UPDATED, false)
            data?.getParcelableExtra<Workout>(AppParams.INTENT_EXTRA_NEW_WORKOUT)?.let { w ->
                if (isUpdated == false) {
                    viewModel.storeWorkout(w)
                } else {
                    viewModel.updateWorkout(w)
                }
            }
        }
    }

    private fun activityTransition(intent: Intent, reqCodeForResult: Int) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        if (reqCodeForResult > 0) {
            startActivityForResult(intent, reqCodeForResult, options.toBundle())
        } else {
            startActivity(intent, options.toBundle())
        }
    }

    private fun askForDesiredWeight() {
        DesiredWeightDialogFragment.newInstance().show(supportFragmentManager, "frag-desired-weight")
    }


    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

    }
}
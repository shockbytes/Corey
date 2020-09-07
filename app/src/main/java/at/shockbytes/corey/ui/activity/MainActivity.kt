package at.shockbytes.corey.ui.activity

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import at.shockbytes.core.image.GlideImageLoader
import at.shockbytes.core.image.ImageLoader
import at.shockbytes.core.model.ShockbytesUser
import at.shockbytes.core.ui.activity.BottomNavigationBarActivity
import at.shockbytes.core.ui.model.AdditionalToolbarAction
import at.shockbytes.core.ui.model.BottomNavigationActivityOptions
import at.shockbytes.core.ui.model.BottomNavigationTab
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.navigation.CoreyPageFragmentResolver
import at.shockbytes.corey.ui.fragment.MenuFragment
import at.shockbytes.corey.ui.fragment.dialog.AddGoalDialogFragment
import at.shockbytes.corey.ui.fragment.dialog.DesiredWeightDialogFragment
import at.shockbytes.corey.ui.viewmodel.MainViewModel
import at.shockbytes.corey.util.AppParams
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BottomNavigationBarActivity<AppComponent>() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel

    override val imageLoader: ImageLoader = GlideImageLoader(R.drawable.ic_account)

    private val additionalToolbarActionItems = listOf(
        AdditionalToolbarAction(R.drawable.ic_add_colored, R.string.add_nutrition_entry, changeWithAnimation = true) {
            showToast("Create new nutrition entry")
        },
        AdditionalToolbarAction(R.drawable.ic_add_colored, R.string.create_workout, true) {
            activityTransition(CreateWorkoutActivity.newIntent(applicationContext), AppParams.REQUEST_CODE_CREATE_WORKOUT)
        },
        AdditionalToolbarAction(R.drawable.ic_cancel_red, R.string.reset_schedule, true) {
            showScheduleDeletionApprovalDialog()
        },
        AdditionalToolbarAction(R.drawable.ic_body_card_weight_history_colored, R.string.change_dreamweight, true) {
            askForDesiredWeight()
        },
        AdditionalToolbarAction(R.drawable.ic_add_colored, R.string.add_goal, true) {
            AddGoalDialogFragment.newInstance()
                .setOnGoalCreatedListener { goal ->
                    viewModel.storeBodyGoal(goal)
                }
                .show(supportFragmentManager, "dialog-fragment-add-goal")
        }
    )

    private val tabs by lazy {
        listOf(
            BottomNavigationTab(R.id.nav_item_nutrition, R.drawable.navigation_item, R.drawable.ic_tab_nutrition, getString(R.string.tab_nutrition)),
            BottomNavigationTab(R.id.nav_item_workout, R.drawable.navigation_item, R.drawable.ic_tab_workout, getString(R.string.tab_workout)),
            BottomNavigationTab(R.id.nav_item_schedule, R.drawable.navigation_item, R.drawable.ic_tab_schedule, getString(R.string.tab_schedule)),
            BottomNavigationTab(R.id.nav_item_my_body, R.drawable.navigation_item, R.drawable.ic_tab_my_body, getString(R.string.tab_my_body)),
            BottomNavigationTab(R.id.nav_item_goals, R.drawable.navigation_item, R.drawable.ic_tab_goals, getString(R.string.tab_goals))
        )
    }

    override val options: BottomNavigationActivityOptions by lazy {
        BottomNavigationActivityOptions(
            tabs = tabs,
            defaultTab = R.id.nav_item_schedule,
            appName = getString(R.string.app_name),
            viewPagerOffscreenLimit = 3,
            appTheme = R.style.AppTheme_NoActionBar,
            fabMenuOptions = null,
            overflowIcon = R.drawable.ic_overflow,
            initialAdditionalToolbarAction = additionalToolbarActionItems[1],
            toolbarColor = R.color.white,
            toolbarItemColor = R.color.controls,
            titleColor = R.color.colorPrimary,
            navigationBarColor = R.color.navigation_bar_color,
            navigationItemTextColor = R.color.navigation_bar_item_text,
            navigationItemTintColor = R.color.navigation_bar_item_text
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]
        viewModel.pokeReminderManager(this)
    }

    override fun bindViewModel() {
        viewModel.getUserEvent().observe(this, Observer {
            it?.let { event ->
                onUserEvent(event)
            }
        })

        viewModel.getToastMessages()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ message ->
                showToast(message)
            }, { throwable ->
                Timber.e(throwable)
            })
            .addTo(compositeDisposable)
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun onBottomBarPageChanged(newPageIndex: Int) {
        additionalToolbarActionItem = additionalToolbarActionItems[newPageIndex]
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
                    .setOnGoalCreatedListener { goal ->
                        viewModel.storeBodyGoal(goal)
                    }
                    .show(supportFragmentManager, "dialog-fragment-add-goal")
                false
            }
            else -> false
        }
    }

    override fun setupDarkMode() = Unit

    override fun createFragmentForIndex(index: Int): Fragment {
        return CoreyPageFragmentResolver.createFragmentForPosition(index)
    }

    override fun showLoginScreen() = Unit

    override fun showMenuFragment() {
        MenuFragment.newInstance().show(supportFragmentManager, "menu-fragment")
    }

    override fun showWelcomeScreen(user: ShockbytesUser) = Unit

    override fun unbindViewModel() = Unit

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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

    private fun showScheduleDeletionApprovalDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_schedule)
            .setMessage(R.string.delete_schedule_message)
            .setIcon(R.drawable.ic_cancel_red)
            .setNegativeButton(R.string.cancel) { _, _ -> Unit }
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.resetSchedule()
            }
            .create()
            .show()
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

    fun shouldCastActionBarShadow(castsActionBarShadow: Boolean) {
        castActionBarShadow(castsActionBarShadow)
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
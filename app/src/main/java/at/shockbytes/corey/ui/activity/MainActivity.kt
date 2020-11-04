package at.shockbytes.corey.ui.activity

import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import at.shockbytes.core.image.GlideImageLoader
import at.shockbytes.core.image.ImageLoader
import at.shockbytes.core.model.ShockbytesUser
import at.shockbytes.core.ui.activity.BottomNavigationBarActivity
import at.shockbytes.core.ui.model.AdditionalToolbarAction
import at.shockbytes.core.ui.model.BottomNavigationActivityOptions
import at.shockbytes.core.ui.model.BottomNavigationTab
import at.shockbytes.core.util.CoreUtils.colored
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.navigation.CoreyPageFragmentResolver
import at.shockbytes.corey.ui.fragment.AddNutritionEntryFragment
import at.shockbytes.corey.ui.fragment.MenuFragment
import at.shockbytes.corey.ui.fragment.dialog.DesiredWeightDialogFragment
import at.shockbytes.corey.ui.viewmodel.MainViewModel
import at.shockbytes.corey.util.ShockColors
import at.shockbytes.corey.util.showBaseFragment
import at.shockbytes.corey.util.viewModelOfActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BottomNavigationBarActivity<AppComponent>() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel

    override val imageLoader: ImageLoader = GlideImageLoader(R.drawable.ic_account)

    private val additionalToolbarActionItems = listOf(
        AdditionalToolbarAction(
                R.drawable.ic_add_colored,
                R.string.add_nutrition_entry,
                changeWithAnimation = true) {
            AddNutritionEntryFragment.newInstance().let(supportFragmentManager::showBaseFragment)
        },
        AdditionalToolbarAction(
                R.drawable.ic_cancel_red,
                R.string.reset_schedule,
                changeWithAnimation = true,
                onActionClick = ::showScheduleDeletionApprovalDialog
        ),
        AdditionalToolbarAction(
                R.drawable.ic_body_card_weight_history_colored,
                R.string.change_dreamweight,
                changeWithAnimation = true,
                onActionClick = ::askForDesiredWeight
        )
    )

    private val tabs by lazy {
        listOf(
            BottomNavigationTab(R.id.nav_item_nutrition, R.drawable.navigation_item, R.drawable.ic_tab_nutrition, getString(R.string.tab_nutrition)),
            BottomNavigationTab(R.id.nav_item_schedule, R.drawable.navigation_item, R.drawable.ic_tab_schedule, getString(R.string.tab_schedule)),
            BottomNavigationTab(R.id.nav_item_my_body, R.drawable.navigation_item, R.drawable.ic_tab_my_body, getString(R.string.tab_my_body))
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
            toolbarColor = R.color.navigation_bar_color,
            toolbarItemColor = R.color.controls,
            titleColor = R.color.title_color,
            navigationBarColor = R.color.navigation_bar_color,
            navigationItemTextColor = R.color.navigation_bar_item_text,
            navigationItemTintColor = R.color.navigation_bar_item_text
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModelOfActivity(vmFactory)
        viewModel.pokeReminderManager(this)
        viewModel.prefetch()
    }

    override fun bindViewModel() {
        viewModel.getUserEvent().observe(this, Observer(::onUserEvent))

        viewModel.getToastMessages()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::showToast, Timber::e)
            .addTo(compositeDisposable)
    }

    override fun onDestroy() {
        viewModel.cleanUp()
        super.onDestroy()
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun onBottomBarPageChanged(newPageIndex: Int) {
        additionalToolbarActionItem = additionalToolbarActionItems[newPageIndex]
    }

    override fun onFabMenuItemClicked(id: Int): Boolean = false

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

    private fun showScheduleDeletionApprovalDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_schedule)
            .setMessage(R.string.delete_schedule_message)
            .setIcon(R.drawable.ic_cancel_red)
            .setNegativeButton(getString(R.string.cancel).colored(ContextCompat.getColor(this, R.color.colorPrimaryText))) { _, _ -> Unit }
            .setPositiveButton(getString(R.string.delete).colored(ShockColors.ERROR)) { _, _ ->
                viewModel.resetSchedule()
            }
            .create()
            .show()
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
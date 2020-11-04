package at.shockbytes.corey.ui.fragment

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import at.shockbytes.core.image.ImageLoader
import at.shockbytes.core.model.LoginUserEvent
import at.shockbytes.corey.R
import at.shockbytes.corey.common.setVisible
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.ui.activity.SettingsActivity
import at.shockbytes.corey.ui.custom.CheckableMenuEntryItemView
import at.shockbytes.corey.ui.custom.MenuEntryItemView
import at.shockbytes.corey.ui.viewmodel.MainViewModel
import at.shockbytes.corey.util.showBaseFragment
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    06.06.2018
 */
class MenuFragment : BottomSheetDialogFragment() {

    private val bottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @Inject
    protected lateinit var vmFactory: ViewModelProvider.Factory

    @Inject
    protected lateinit var imageLoader: ImageLoader

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as CoreyApp).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.bottom_sheet_menu, null)
        dialog.setContentView(contentView)
        (contentView.parent as View)
                .setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))

        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.addBottomSheetCallback(bottomSheetBehaviorCallback)
        }

        setupViews(contentView)
    }

    private fun setupViews(view: View) {

        view.findViewById<View>(R.id.btnMenuLogin)?.setOnClickListener {
            viewModel.logout()
        }

        view.findViewById<View>(R.id.menu_item_settings)?.setOnClickListener {
            startSettingsActivity()
            dismiss()
        }

        view.findViewById<View>(R.id.menu_item_notifications)?.setOnClickListener {
            openNotificationSettingsFragment()
            dismiss()
        }

        view.findViewById<CheckableMenuEntryItemView>(R.id.checkable_menu_item_forecast).setOnCheckedChangeListener { isChecked ->
            viewModel.enableWeatherForecast(isChecked)
        }

        viewModel.isWeatherForecastEnabled().observe(this, { isEnabled ->
            view.findViewById<CheckableMenuEntryItemView>(R.id.checkable_menu_item_forecast).isChecked = (isEnabled == true)
        })

        viewModel.getUserEvent().observe(this, { event ->

            when (event) {

                is LoginUserEvent.SuccessEvent -> {

                    view.findViewById<TextView>(R.id.txtMenuUserName)?.text = event.user?.displayName
                    view.findViewById<TextView>(R.id.txtMenuUserMail)?.text = event.user?.email
                    view.findViewById<Button>(R.id.btnMenuLogin)?.text = getString(R.string.logout)

                    event.user?.photoUrl?.let { photoUrl ->
                        imageLoader.loadImageUri(
                                requireContext(),
                                photoUrl,
                                view.findViewById(R.id.imageViewMenuUser),
                                circular = true)
                    }
                }
            }
        })

        viewModel.getWatchInfo().observe(this, { (title, isConnected) ->
            view.findViewById<MenuEntryItemView>(R.id.menu_item_watch).apply {
                setTitle(title ?: getString(R.string.unknown_smartwatch))
                setVisible(isConnected)
            }
        })
    }

    private fun openNotificationSettingsFragment() {
        parentFragmentManager.showBaseFragment(ReminderFragment.newInstance())
    }

    private fun startSettingsActivity() {
        activity?.let { act ->
            startActivity(SettingsActivity.newIntent(act),
                ActivityOptionsCompat.makeSceneTransitionAnimation(act).toBundle())
        }
    }

    companion object {
        fun newInstance(): MenuFragment {
            val fragment = MenuFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
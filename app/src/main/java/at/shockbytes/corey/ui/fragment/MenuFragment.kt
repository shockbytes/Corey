package at.shockbytes.corey.ui.fragment

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import at.shockbytes.core.image.ImageLoader
import at.shockbytes.core.model.LoginUserEvent
import at.shockbytes.corey.R
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.ui.activity.SettingsActivity
import at.shockbytes.corey.ui.viewmodel.MainViewModel
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
                .setBackgroundColor(ContextCompat.getColor(context!!, android.R.color.transparent))

        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(bottomSheetBehaviorCallback)
        }

        setupViews(contentView)
    }

    private fun setupViews(view: View) {

        view.findViewById<View>(R.id.btnMenuLogin)?.setOnClickListener {
            viewModel.logout()
        }

        view.findViewById<View>(R.id.btnMenuSettings)?.setOnClickListener {
            activity?.let { act ->
                startActivity(SettingsActivity.newIntent(act),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(act).toBundle())
            }
            dismiss()
        }

        viewModel.getUserEvent().observe(this, Observer { event ->

            when (event) {

                is LoginUserEvent.SuccessEvent -> {

                    view.findViewById<TextView>(R.id.txtMenuUserName)?.text = event.user?.displayName
                    view.findViewById<TextView>(R.id.txtMenuUserMail)?.text = event.user?.email
                    view.findViewById<Button>(R.id.btnMenuLogin)?.text = getString(R.string.logout)

                    context?.let { ctx ->
                        event.user?.photoUrl?.let { photoUrl ->
                            imageLoader.loadImageUri(
                                    ctx,
                                    photoUrl,
                                    view.findViewById(R.id.imageViewMenuUser),
                                    circular = true)
                        }
                    }
                }
            }
        })
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
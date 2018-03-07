package at.shockbytes.corey.ui.fragment


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.WearAppComponent
import butterknife.ButterKnife
import butterknife.Unbinder

class RunningFragment : WearableBaseFragment() {

    override val layoutId = R.layout.fragment_running

    override fun setupViews() {
        // Setup views
    }

    override fun injectToGraph(appComponent: WearAppComponent) {
        appComponent.inject(this)
    }

    companion object {

        fun newInstance(): RunningFragment {
            val fragment = RunningFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor

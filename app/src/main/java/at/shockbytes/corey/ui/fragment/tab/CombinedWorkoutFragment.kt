package at.shockbytes.corey.ui.fragment.tab

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import at.shockbytes.corey.R
import at.shockbytes.corey.dagger.AppComponent
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_combined_workout.*

class CombinedWorkoutFragment : TabBaseFragment<AppComponent>() {

    override val layoutId: Int = R.layout.fragment_combined_workout

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_background

    override fun bindViewModel() = Unit
    override fun injectToGraph(appComponent: AppComponent?) = Unit

    override val castsActionBarShadow: Boolean = false

    override fun setupViews() {
        vp2_combined_workout.adapter = CombinedWorkoutAdapter(this)

        TabLayoutMediator(tablayout_combined_workout, vp2_combined_workout) { tab, position ->

            val text = when (position) {
                0 -> getString(R.string.tab_workout)
                1 -> getString(R.string.tab_running)
                else -> throw IllegalStateException("Illegal position for mediator: $position")
            }

            tab.text = text
        }.attach()
    }

    override fun unbindViewModel() = Unit

    private class CombinedWorkoutAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            // Return a NEW fragment instance in createFragment(int)
            return when (position) {
                0 -> WorkoutOverviewFragment.newInstance()
                1 -> RunningFragment.newInstance()
                else -> throw IllegalStateException("Illegal position $position")
            }
        }
    }

    companion object {

        fun newInstance(): CombinedWorkoutFragment = CombinedWorkoutFragment()
    }
}
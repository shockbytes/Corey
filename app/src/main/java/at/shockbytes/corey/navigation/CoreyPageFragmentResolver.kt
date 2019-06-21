package at.shockbytes.corey.navigation

import androidx.fragment.app.Fragment
import at.shockbytes.corey.ui.fragment.pager.BodyFragment
import at.shockbytes.corey.ui.fragment.pager.GoalsFragment
import at.shockbytes.corey.ui.fragment.pager.ScheduleFragment
import at.shockbytes.corey.ui.fragment.pager.WorkoutOverviewFragment

object CoreyPageFragmentResolver {

    /*
    private val workoutOverviewFragment by lazy {
        WorkoutOverviewFragment.newInstance()
    }

    private val scheduleFragment by lazy {
        ScheduleFragment.newInstance()
    }

    private val bodyFragment by lazy {
        BodyFragment.newInstance()
    }

    private val goalsFragment by lazy {
        GoalsFragment.newInstance()
    }
    */

    fun createFragmentForPosition(position: Int): Fragment {
        return when (position) {
            0 -> WorkoutOverviewFragment.newInstance()
            1 -> ScheduleFragment.newInstance()
            2 -> BodyFragment.newInstance()
            3 -> GoalsFragment.newInstance()
            else -> throw IllegalStateException("Cannot resolve fragment in CoreyPagerAdapter for position $position") // Never the case
        }
    }
}
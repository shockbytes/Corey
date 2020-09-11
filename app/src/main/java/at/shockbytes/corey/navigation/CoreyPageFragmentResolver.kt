package at.shockbytes.corey.navigation

import androidx.fragment.app.Fragment
import at.shockbytes.corey.ui.fragment.tab.*

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

    // TODO Improve caching of Fragments
    fun createFragmentForPosition(position: Int): Fragment {
        return when (position) {
            0 -> NutritionFragment.newInstance()
            1 -> ScheduleFragment.newInstance()
            2 -> BodyFragment.newInstance()
            else -> throw IllegalStateException("Cannot resolve fragment in CoreyPagerAdapter for position $position") // Never the case
        }
    }
}
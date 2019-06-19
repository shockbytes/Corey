package at.shockbytes.corey.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import at.shockbytes.core.ui.model.BottomNavigationTab
import at.shockbytes.corey.ui.fragment.pager.BodyFragment
import at.shockbytes.corey.ui.fragment.pager.GoalsFragment
import at.shockbytes.corey.ui.fragment.pager.ScheduleFragment
import at.shockbytes.corey.ui.fragment.pager.WorkoutOverviewFragment

/**
 * Author:  Martin Macheiner
 * Date:    30.08.2016
 */
class CoreyPagerAdapter(
    fm: FragmentManager,
    private val tabs: List<BottomNavigationTab>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WorkoutOverviewFragment.newInstance()
            1 -> ScheduleFragment.newInstance()
            2 -> BodyFragment.newInstance()
            3 -> GoalsFragment.newInstance()
            else -> throw IllegalStateException("Cannot resolve fragment in CoreyPagerAdapter for position $position") // Never the case
        }
    }

    override fun getCount(): Int = tabs.size

    override fun getPageTitle(position: Int): CharSequence? {
        return tabs[position].title
    }
}

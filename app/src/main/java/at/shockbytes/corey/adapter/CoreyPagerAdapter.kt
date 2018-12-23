package at.shockbytes.corey.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import at.shockbytes.core.ui.model.BottomNavigationTab
import at.shockbytes.corey.ui.fragment.pager.BodyFragment
import at.shockbytes.corey.ui.fragment.pager.ScheduleFragment
import at.shockbytes.corey.ui.fragment.pager.WorkoutOverviewFragment


/**
 * Author:  Martin Macheiner
 * Date:    30.08.2016
 */
class CoreyPagerAdapter(fm: FragmentManager,
                        private val tabs: List<BottomNavigationTab>) : FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> WorkoutOverviewFragment.newInstance()
            1 -> ScheduleFragment.newInstance()
            2 -> BodyFragment.newInstance()
            else -> null // Never the case
        }
    }

    override fun getCount(): Int = tabs.size

    override fun getPageTitle(position: Int): CharSequence? {
        return tabs[position].title
    }
}

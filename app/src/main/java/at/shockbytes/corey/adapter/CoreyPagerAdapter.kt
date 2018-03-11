package at.shockbytes.corey.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.fragment.pager.BodyFragment
import at.shockbytes.corey.ui.fragment.pager.ScheduleFragment
import at.shockbytes.corey.ui.fragment.pager.WorkoutOverviewFragment


/**
 * @author Martin Macheiner
 * Date: 30.08.2016.
 */
class CoreyPagerAdapter(private val context: Context,
                        fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount() = 3

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> WorkoutOverviewFragment.newInstance()
            1 -> ScheduleFragment.newInstance()
            2 -> BodyFragment.newInstance()
            else -> null // Never the case
        }
    }

    override fun getPageTitle(position: Int): CharSequence {

        return when (position) {

            0 -> context.getString(R.string.tab_workout)
            1 -> context.getString(R.string.tab_schedule)
            2 -> context.getString(R.string.tab_my_body)
            else -> "" // Never the case
        }
    }

    fun getPageIcon(position: Int): Int {

        return when (position) {

            0 -> R.drawable.ic_tab_workout
            1 -> R.drawable.ic_tab_schedule
            2 -> R.drawable.ic_tab_my_body
            else -> 0 // Never the case
        }
    }


}

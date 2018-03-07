package at.shockbytes.corey.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.Toast
import at.shockbytes.corey.R
import at.shockbytes.corey.adapter.DaysScheduleAdapter
import at.shockbytes.corey.adapter.ScheduleAdapter
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.schedule.ScheduleItem
import at.shockbytes.corey.schedule.ScheduleManager
import at.shockbytes.corey.storage.live.LiveScheduleUpdateListener
import at.shockbytes.corey.ui.fragment.dialog.InsertScheduleDialogFragment
import at.shockbytes.util.AppUtils
import at.shockbytes.util.adapter.BaseAdapter
import at.shockbytes.util.adapter.BaseItemTouchHelper
import at.shockbytes.util.view.EqualSpaceItemDecoration
import kotterknife.bindView
import javax.inject.Inject

/**
 * @author  Martin Macheiner
 * Date:    26.10.2015.
 */
class ScheduleFragment : BaseFragment(), LiveScheduleUpdateListener,
        BaseAdapter.OnItemMoveListener<ScheduleItem> {

    @Inject
    protected lateinit var scheduleManager: ScheduleManager

    private lateinit var touchHelper: ItemTouchHelper
    private lateinit var adapter: ScheduleAdapter

    private val recyclerView: RecyclerView by bindView(R.id.fragment_schedule_rv)
    private val recyclerViewDays: RecyclerView by bindView(R.id.fragment_schedule_rv_days)

    private val recyclerViewLayoutManager: RecyclerView.LayoutManager
        get() = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        } else {
            GridLayoutManager(context, ScheduleAdapter.MAX_SCHEDULES)
        }

    override val layoutId = R.layout.fragment_schedule

    override fun onStart() {
        super.onStart()
        scheduleManager.registerLiveForScheduleUpdates(this)
    }

    override fun onStop() {
        super.onStop()
        scheduleManager.unregisterLiveForScheduleUpdates()
    }

    override fun onScheduleItemAdded(item: ScheduleItem) {
        adapter.insertScheduleItem(item)
    }

    override fun onScheduleItemDeleted(item: ScheduleItem) {
        adapter.resetEntity(item)
    }

    override fun onScheduleItemChanged(item: ScheduleItem) {
        adapter.updateScheduleItem(item)
    }

    override fun onItemMove(t: ScheduleItem, from: Int, to: Int) {
    }

    override fun onItemMoveFinished() {
        adapter.reorderAfterMove().forEach { scheduleManager.updateScheduleItem(it) }
    }

    override fun onItemDismissed(t: ScheduleItem, position: Int) {
        if (!t.isEmpty) {
            scheduleManager.deleteScheduleItem(t)
        }
    }

    override fun setupViews() {

        recyclerViewDays.layoutManager = recyclerViewLayoutManager
        recyclerViewDays.adapter = DaysScheduleAdapter(context!!, resources.getStringArray(R.array.days).toList())
        recyclerViewDays.addItemDecoration(EqualSpaceItemDecoration(AppUtils.convertDpInPixel(4, context!!)))

        recyclerView.layoutManager = recyclerViewLayoutManager
        adapter = ScheduleAdapter(context!!, listOf())
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(EqualSpaceItemDecoration(AppUtils.convertDpInPixel(4, context!!)))
        val callback = BaseItemTouchHelper(adapter, true, BaseItemTouchHelper.DragAccess.ALL)
        adapter.onItemMoveListener = this
        adapter.setOnScheduleItemSelectedListener { item, _, position ->
            onScheduleItemClicked(item, position)
        }

        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)

        scheduleManager.schedule.subscribe({ scheduleItems ->
            adapter.data = scheduleItems.toMutableList()
        }, { throwable ->
            throwable.printStackTrace()
            Toast.makeText(context, throwable.toString(), Toast.LENGTH_LONG).show()
        })

    }

    override fun injectToGraph(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    private fun onScheduleItemClicked(item: ScheduleItem, position: Int) {
        if (item.isEmpty) {
            InsertScheduleDialogFragment.newInstance()
                    .setOnScheduleItemSelectedListener { i ->
                        scheduleManager.insertScheduleItem(ScheduleItem(i, position))
                    }
                    .show(fragmentManager, "dialogfragment-insert-schedule")
        }
    }

    companion object {

        fun newInstance(): ScheduleFragment {
            val fragment = ScheduleFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}

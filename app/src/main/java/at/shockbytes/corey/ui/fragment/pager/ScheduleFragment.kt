package at.shockbytes.corey.ui.fragment.pager

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.Toast
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.DaysScheduleAdapter
import at.shockbytes.corey.ui.adapter.ScheduleAdapter
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.data.schedule.ScheduleItem
import at.shockbytes.corey.data.schedule.ScheduleRepository
import at.shockbytes.corey.ui.fragment.dialog.InsertScheduleDialogFragment
import at.shockbytes.util.AppUtils
import at.shockbytes.util.adapter.BaseAdapter
import at.shockbytes.util.adapter.BaseItemTouchHelper
import at.shockbytes.util.view.EqualSpaceItemDecoration
import kotterknife.bindView
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    26.10.2015
 */
class ScheduleFragment : BaseFragment<AppComponent>(), BaseAdapter.OnItemMoveListener<ScheduleItem> {

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    @Inject
    lateinit var scheduleManager: ScheduleRepository

    private lateinit var touchHelper: ItemTouchHelper
    private val adapter: ScheduleAdapter by lazy {
        ScheduleAdapter(
            context!!,
            { item, _, position -> onScheduleItemClicked(item, position) },
            { item, position -> onItemDismissed(item, position) }
        )
    }

    private val recyclerView: RecyclerView by bindView(R.id.fragment_schedule_rv)
    private val recyclerViewDays: RecyclerView by bindView(R.id.fragment_schedule_rv_days)

    private val recyclerViewLayoutManager: RecyclerView.LayoutManager
        get() = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        } else {
            GridLayoutManager(context, ScheduleAdapter.MAX_SCHEDULES)
        }

    override val layoutId = R.layout.fragment_schedule

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

    override fun bindViewModel() {

        scheduleManager.schedule
                .subscribe({ scheduleItems ->
                    adapter.updateData(scheduleItems)
                }, { throwable ->
                    throwable.printStackTrace()
                    Toast.makeText(context, throwable.toString(), Toast.LENGTH_LONG).show()
                })
                .addTo(compositeDisposable)
    }

    override fun unbindViewModel() = Unit

    override fun setupViews() {

        context?.let { ctx ->

            recyclerViewDays.layoutManager = recyclerViewLayoutManager
            recyclerViewDays.adapter = DaysScheduleAdapter(ctx, resources.getStringArray(R.array.days).toList())
            recyclerViewDays.addItemDecoration(EqualSpaceItemDecoration(AppUtils.convertDpInPixel(4, ctx)))

            recyclerView.layoutManager = recyclerViewLayoutManager
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.addItemDecoration(EqualSpaceItemDecoration(AppUtils.convertDpInPixel(4, ctx)))
            val callback = BaseItemTouchHelper(adapter, false, BaseItemTouchHelper.DragAccess.ALL)
            adapter.onItemMoveListener = this

            touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(recyclerView)
            recyclerView.adapter = adapter
        }
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    private fun onScheduleItemClicked(item: ScheduleItem, position: Int) {
        if (item.isEmpty) {
            InsertScheduleDialogFragment.newInstance()
                    .setOnScheduleItemSelectedListener { i ->
                        scheduleManager.insertScheduleItem(ScheduleItem(i.item.title, position, locationType = i.item.locationType))
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

package at.shockbytes.corey.ui.fragment.tab

import android.content.res.Configuration
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.*
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.DaysScheduleAdapter
import at.shockbytes.corey.ui.adapter.ScheduleAdapter
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.data.schedule.ScheduleItem
import at.shockbytes.corey.data.schedule.ScheduleRepository
import at.shockbytes.corey.data.schedule.weather.ScheduleWeatherResolver
import at.shockbytes.corey.ui.fragment.dialog.InsertScheduleDialogFragment
import at.shockbytes.util.AppUtils
import at.shockbytes.util.adapter.BaseAdapter
import at.shockbytes.util.adapter.BaseItemTouchHelper
import at.shockbytes.util.view.EqualSpaceItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotterknife.bindView
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    26.10.2015
 */
class ScheduleFragment : TabBaseFragment<AppComponent>(),
        BaseAdapter.OnItemMoveListener<ScheduleItem>,
        BaseAdapter.OnItemClickListener<ScheduleItem>
{

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    @Inject
    lateinit var scheduleRepository: ScheduleRepository

    @Inject
    lateinit var schedulers: SchedulerFacade

    @Inject
    lateinit var userSettings: UserSettings

    @Inject
    lateinit var weatherResolver: ScheduleWeatherResolver

    private lateinit var touchHelper: ItemTouchHelper
    private val scheduleAdapter: ScheduleAdapter by lazy {
        ScheduleAdapter(
                requireContext(),
                onItemClickListener = this,
                onItemMoveListener = this,
                weatherResolver,
                schedulers,
                userSettings
        )
    }

    private val recyclerViewDays: RecyclerView by bindView(R.id.fragment_schedule_rv_days)

    private val recyclerViewLayoutManager: RecyclerView.LayoutManager
        get() = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        } else {
            GridLayoutManager(context, ScheduleAdapter.MAX_SCHEDULES)
        }

    override val layoutId = R.layout.fragment_schedule

    override fun onItemMove(t: ScheduleItem, from: Int, to: Int) = Unit

    override fun onItemMoveFinished() {
        scheduleAdapter.reorderAfterMove()
                .forEach { item ->
                    scheduleRepository.updateScheduleItem(item)
                }
    }

    override fun onItemDismissed(t: ScheduleItem, position: Int) = Unit

    override fun bindViewModel() {

        scheduleRepository.schedule
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ scheduleItems ->
                    scheduleAdapter.updateData(scheduleItems)
                }, { throwable ->
                    throwable.printStackTrace()
                    Toast.makeText(context, throwable.toString(), Toast.LENGTH_LONG).show()
                })
                .addTo(compositeDisposable)
    }

    override fun unbindViewModel() = Unit

    override fun setupViews() {

        recyclerViewDays.apply {
            layoutManager = recyclerViewLayoutManager
            adapter = DaysScheduleAdapter(
                    requireContext(),
                    resources.getStringArray(R.array.days).toList(),
                    object: BaseAdapter.OnItemLongClickListener<String> {
                        override fun onItemLongClick(content: String, position: Int, v: View) {
                            v.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            scheduleAdapter.getItemAt(position)?.let { item ->
                                if (!item.isEmpty) {
                                    scheduleRepository.deleteScheduleItem(item)
                                }
                            }
                        }
                    }
            )
            addItemDecoration(EqualSpaceItemDecoration(AppUtils.convertDpInPixel(4, requireContext())))
        }

        fragment_schedule_rv.apply {
            layoutManager = recyclerViewLayoutManager
            isNestedScrollingEnabled = false
            addItemDecoration(EqualSpaceItemDecoration(AppUtils.convertDpInPixel(4, requireContext())))
            val callback = BaseItemTouchHelper(scheduleAdapter, false, BaseItemTouchHelper.DragAccess.ALL)

            touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(this)
            adapter = scheduleAdapter
        }
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun onItemClick(content: ScheduleItem, position: Int, v: View) {
        if (content.isEmpty) {
            InsertScheduleDialogFragment.newInstance()
                    .setOnScheduleItemSelectedListener { i ->
                        scheduleRepository.insertScheduleItem(
                                ScheduleItem(
                                        i.item.title,
                                        position,
                                        locationType = i.item.locationType,
                                        workoutIconType = i.item.workoutType
                                )
                        )
                    }
                    .show(childFragmentManager, "dialog-fragment-insert-schedule")
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

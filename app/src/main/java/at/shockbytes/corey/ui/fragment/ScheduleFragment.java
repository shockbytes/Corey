package at.shockbytes.corey.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.DaysScheduleAdapter;
import at.shockbytes.corey.adapter.ScheduleAdapter;
import at.shockbytes.corey.dagger.AppComponent;
import at.shockbytes.corey.storage.live.LiveScheduleUpdateListener;
import at.shockbytes.corey.ui.fragment.dialogs.InsertScheduleDialogFragment;
import at.shockbytes.corey.util.schedule.ScheduleItem;
import at.shockbytes.corey.util.schedule.ScheduleManager;
import at.shockbytes.util.AppUtils;
import at.shockbytes.util.adapter.BaseItemTouchHelper;
import at.shockbytes.util.view.EqualSpaceItemDecoration;
import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * @author Martin Macheiner
 *         Date: 26.10.2015.
 */
public class ScheduleFragment extends BaseFragment implements LiveScheduleUpdateListener,
        ScheduleAdapter.OnItemMoveListener, ScheduleAdapter.OnItemClickListener,
        InsertScheduleDialogFragment.OnScheduleItemSelectedListener {


    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private ItemTouchHelper touchHelper;

    private ScheduleAdapter adapter;

    @Inject
    protected ScheduleManager scheduleManager;

    @BindView(R.id.fragment_schedule_rv)
    protected RecyclerView recyclerView;

    @BindView(R.id.fragment_schedule_rv_days)
    protected RecyclerView recyclerViewDays;

    @Override
    public void onStop() {
        super.onStop();
        scheduleManager.unregisterLiveForScheduleUpdates();
    }

    @Override
    public void onScheduleItemAdded(ScheduleItem item) {
        adapter.insertEntity(item);
    }

    @Override
    public void onScheduleItemDeleted(ScheduleItem item) {
        adapter.resetEntity(item);
    }

    @Override
    public void onScheduleItemChanged(ScheduleItem item) {
        adapter.updateEntity(item);
    }

    @Override
    public void onItemMove(ScheduleItem item, int from, int to) {

    }

    @Override
    public void onItemMoveFinished() {

        List<ScheduleItem> items = adapter.getScheduleData();
        for (ScheduleItem si : items) {
            scheduleManager.updateScheduleItem(si);
        }
    }

    @Override
    public void onItemDismissed(ScheduleItem item, int position) {

        if (!item.isEmpty()) {
            scheduleManager.deleteScheduleItem(item);
        }
        //adapter.addEntity(position, new ScheduleItem("", position));
        //recyclerView.invalidate();
    }

    @Override
    public void onItemClick(ScheduleItem item, View v, int position) {

        if (item.isEmpty()) {
            InsertScheduleDialogFragment fragment = InsertScheduleDialogFragment
                    .newInstance(position);
            fragment.setOnScheduleItemSelectedListener(this);
            fragment.show(getFragmentManager(), fragment.getTag());
        }
    }

    @Override
    public void onScheduleItemSelected(String item, int day) {
        scheduleManager.insertScheduleItem(new ScheduleItem(item, day));
    }

    @Override
    public void setupViews() {

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getColsForOrientation()));
        recyclerViewDays.setLayoutManager(new GridLayoutManager(getContext(), getColsForOrientation()));
        recyclerViewDays.setAdapter(new DaysScheduleAdapter(getContext(),
                Arrays.asList(getResources().getStringArray(R.array.days))));
        recyclerViewDays.addItemDecoration(new EqualSpaceItemDecoration(
                AppUtils.INSTANCE.convertDpInPixel(4, getContext())));

        adapter = new ScheduleAdapter(getContext(), new ArrayList<ScheduleItem>());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new EqualSpaceItemDecoration(
                AppUtils.INSTANCE.convertDpInPixel(4, getContext())));
        ItemTouchHelper.Callback callback = new BaseItemTouchHelper(adapter, true, BaseItemTouchHelper.DragAccess.VERTICAL);
        adapter.setOnItemMoveListener(this);
        adapter.setOnItemClickListener(this);

        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        scheduleManager.getSchedule().subscribe(new Consumer<List<ScheduleItem>>() {

            @Override
            public void accept(List<ScheduleItem> scheduleItems) {
                adapter.setData(scheduleItems);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });

        scheduleManager.registerLiveForScheduleUpdates(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_schedule;
    }

    @Override
    protected void injectToGraph(@NotNull AppComponent appComponent) {
        appComponent.inject(this);
    }

    private int getColsForOrientation() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? 1
                : 7;
    }

}

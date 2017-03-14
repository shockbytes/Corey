package at.shockbytes.corey.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.DaysScheduleAdapter;
import at.shockbytes.corey.adapter.ScheduleAdapter;
import at.shockbytes.corey.adapter.helper.CoreyItemTouchHelper;
import at.shockbytes.corey.core.CoreyApp;
import at.shockbytes.corey.fragment.dialogs.InsertScheduleDialogFragment;
import at.shockbytes.corey.storage.live.LiveScheduleUpdateListener;
import at.shockbytes.corey.common.core.util.ResourceManager;
import at.shockbytes.corey.util.schedule.ScheduleItem;
import at.shockbytes.corey.util.schedule.ScheduleManager;
import at.shockbytes.corey.common.core.util.view.EqualSpaceItemDecoration;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * @author Martin Macheiner
 *         Date: 26.10.2015.
 */
public class ScheduleFragment extends Fragment implements LiveScheduleUpdateListener,
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

    @Bind(R.id.fragment_schedule_rv)
    protected RecyclerView recyclerView;

    @Bind(R.id.fragment_schedule_rv_days)
    protected RecyclerView recyclerViewDays;

    public ScheduleFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CoreyApp) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeViews();
    }

    @Override
    public void onStop() {
        super.onStop();
        scheduleManager.unregisterLiveForScheduleUpdates(this);
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

    private void initializeViews() {

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getColsForOrientation()));
        recyclerViewDays.setLayoutManager(new GridLayoutManager(getContext(), getColsForOrientation()));
        recyclerViewDays.setAdapter(new DaysScheduleAdapter(getContext(),
                Arrays.asList(getResources().getStringArray(R.array.days))));
        recyclerViewDays.addItemDecoration(new EqualSpaceItemDecoration(
                ResourceManager.convertDpInPixel(4, getContext())));

        adapter = new ScheduleAdapter(getContext(), new ArrayList<ScheduleItem>());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new EqualSpaceItemDecoration(
                ResourceManager.convertDpInPixel(4, getContext())));
        ItemTouchHelper.Callback callback = new CoreyItemTouchHelper(adapter, true, false);
        adapter.setOnItemMoveListener(this);
        adapter.setOnItemClickListener(this);

        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        scheduleManager.getSchedule().subscribe(new Action1<List<ScheduleItem>>() {

            @Override
            public void call(List<ScheduleItem> scheduleItems) {
                adapter.setData(scheduleItems);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });

        scheduleManager.registerLiveForScheduleUpdates(this);
    }

    private int getColsForOrientation() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? 1
                : 7;
    }

}

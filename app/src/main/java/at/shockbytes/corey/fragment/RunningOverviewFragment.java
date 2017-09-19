package at.shockbytes.corey.fragment;


import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.shockbytes.corey.R;
import at.shockbytes.corey.core.RunningActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RunningOverviewFragment extends Fragment {

    public static RunningOverviewFragment newInstance() {
        return new RunningOverviewFragment();
    }

    public RunningOverviewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_running_overview, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.fragment_running_overview_card_free_run)
    protected void onClickStartFreeRun() {
        startRunningActivity(RunningActivity.RunningMode.FREE_RUN);
    }

    @OnClick(R.id.fragment_running_overview_card_distance)
    protected void onClickStartDistanceRun() {
        startRunningActivity(RunningActivity.RunningMode.DISTANCE);
    }

    @OnClick(R.id.fragment_running_overview_card_time)
    protected void onClickStartTimeRun() {
        startRunningActivity(RunningActivity.RunningMode.TIME);
    }

    @OnClick(R.id.fragment_running_overview_card_calories)
    protected void onClickStartCaloriesRun() {
        startRunningActivity(RunningActivity.RunningMode.CALORIES);
    }

    private void startRunningActivity(RunningActivity.RunningMode mode) {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(getActivity());
        startActivity(RunningActivity.newIntent(getContext(), mode), options.toBundle());
    }

}

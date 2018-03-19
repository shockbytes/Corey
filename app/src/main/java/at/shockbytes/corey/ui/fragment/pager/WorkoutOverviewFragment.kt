package at.shockbytes.corey.ui.fragment.pager

import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import at.shockbytes.corey.R
import at.shockbytes.corey.adapter.WorkoutAdapter
import at.shockbytes.corey.common.core.util.WorkoutNameComparator
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.activity.CreateWorkoutActivity
import at.shockbytes.corey.ui.activity.WorkoutDetailActivity
import at.shockbytes.corey.util.AppParams
import at.shockbytes.corey.workout.LiveWorkoutUpdateListener
import at.shockbytes.corey.workout.WorkoutManager
import at.shockbytes.util.adapter.BaseAdapter
import at.shockbytes.util.view.RecyclerViewWithEmptyView
import kotterknife.bindView
import javax.inject.Inject

/**
 * @author Martin Macheiner
 * Date: 26.10.2015.
 */
class WorkoutOverviewFragment : BasePagerFragment(), BaseAdapter.OnItemClickListener<Workout>,
        WorkoutAdapter.OnWorkoutPopupItemSelectedListener, LiveWorkoutUpdateListener {

    @Inject
    protected lateinit var workoutManager: WorkoutManager

    private lateinit var adapter: WorkoutAdapter

    private val recyclerView: RecyclerViewWithEmptyView by bindView(R.id.fragment_training_rv)

    private val layoutManagerForOrientation: RecyclerView.LayoutManager
        get() = if (isPortrait()) {
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        } else {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

    override val layoutId = R.layout.fragment_workout_overview


    override fun registerForLiveEvents() {
        workoutManager.registerLiveWorkoutUpdates(this)
    }

    override fun unregisterForLiveEvents() {
        workoutManager.unregisterLiveWorkoutUpdates()
    }

    override fun injectToGraph(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onItemClick(t: Workout, v: View) {

        val intent = WorkoutDetailActivity.newIntent(context!!, t)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity!!,
                Pair(v.findViewById(R.id.item_training_imgview_body_region),
                        getString(R.string.transition_workout_body_region)),
                Pair(v.findViewById(R.id.item_training_container_duration),
                        getString(R.string.transition_workout_duration)),
                Pair(v.findViewById(R.id.item_training_cardview),
                        getString(R.string.transition_workout_card)),
                Pair(v.findViewById(R.id.item_training_txt_title),
                        getString(R.string.transition_workout_name)),
                Pair(v.findViewById(R.id.item_training_container_exercises),
                        getString(R.string.transition_workout_exercise_count)),
                Pair(v.findViewById(R.id.item_training_imgview_equipment),
                        getString(R.string.transition_workout_equipment)))
        activity?.startActivity(intent, options.toBundle())
    }

    override fun onDelete(w: Workout?) {
        if (w != null) {
            adapter.deleteEntity(w)
            workoutManager.deleteWorkout(w)
        }
    }

    override fun onEdit(w: Workout?) {
        val intent = CreateWorkoutActivity.newIntent(context!!, w)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!)
        activity?.startActivityForResult(intent, AppParams.REQUEST_CODE_CREATE_WORKOUT, options.toBundle())
    }

    override fun setupViews() {

        recyclerView.layoutManager = layoutManagerForOrientation
        adapter = WorkoutAdapter(activity!!, listOf(), this)
        adapter.onItemClickListener = this
        // recyclerView.setEmptyView(emptyView);
        recyclerView.adapter = adapter

        workoutManager.workouts.subscribe({ workouts ->
            adapter.data = workouts.sortedWith(WorkoutNameComparator()).toMutableList()
        }) { throwable ->
            throwable.printStackTrace()
            showSnackbar(getString(R.string.snackbar_cannot_load_workouts))
        }
    }

    override fun onWorkoutAdded(workout: Workout) {
        adapter.addEntityAtLast(workout)
    }

    override fun onWorkoutDeleted(workout: Workout) {
        adapter.deleteEntity(workout)
    }

    override fun onWorkoutChanged(workout: Workout) {
        adapter.updateEntity(workout)
    }

    companion object {

        fun newInstance(): WorkoutOverviewFragment {
            val fragment = WorkoutOverviewFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}

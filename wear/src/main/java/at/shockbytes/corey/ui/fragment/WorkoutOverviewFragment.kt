package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import android.view.View
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.WearWorkoutOverviewAdapter
import at.shockbytes.corey.common.core.util.WorkoutNameComparator
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.WearAppComponent
import at.shockbytes.corey.ui.activity.WearMainActivity
import at.shockbytes.corey.ui.activity.WorkoutActivity
import at.shockbytes.util.adapter.BaseAdapter
import kotterknife.bindView

class WorkoutOverviewFragment : WearableBaseFragment(),
        BaseAdapter.OnItemClickListener<Workout>, WearMainActivity.OnWorkoutsLoadedListener {

    private lateinit var workouts: List<Workout>

    private val recyclerView: WearableRecyclerView by bindView(R.id.fragment_workout_overview_rv)

    override val layoutId = R.layout.fragment_workout_overview

    override fun setupViews() {
        val adapter = WearWorkoutOverviewAdapter(requireContext(), this).apply {
            data = workouts.sortedWith(WorkoutNameComparator()).toMutableList()
        }

        recyclerView.isEdgeItemsCenteringEnabled = true
        recyclerView.layoutManager = WearableLinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    override fun injectToGraph(appComponent: WearAppComponent) = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workouts = arguments?.getParcelableArrayList(ARG_WORKOUTS)
                ?: throw NullPointerException("Workout must be set!")
    }

    override fun bindViewModel() {
    }

    override fun onItemClick(t: Workout, position: Int, v: View) {
        activity?.let {
            startActivity(WorkoutActivity.newIntent(it, t),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(it).toBundle())
        }
    }

    override fun onWorkoutLoaded(workouts: List<Workout>) {
        this.workouts = workouts
        setupViews()
    }

    companion object {

        private const val ARG_WORKOUTS = "arg_workouts"

        fun newInstance(workouts: ArrayList<Workout>): WorkoutOverviewFragment {
            val fragment = WorkoutOverviewFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_WORKOUTS, workouts)
            fragment.arguments = args
            return fragment
        }
    }
}

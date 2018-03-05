package at.shockbytes.corey.ui.fragment


import android.app.Fragment
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.wear.widget.WearableLinearLayoutManager
import android.support.wear.widget.WearableRecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.shockbytes.corey.R
import at.shockbytes.corey.adapter.WearWorkoutOverviewAdapter
import at.shockbytes.corey.common.core.util.WorkoutNameComparator
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.ui.activity.MainActivity
import at.shockbytes.corey.ui.activity.WorkoutActivity
import at.shockbytes.util.adapter.BaseAdapter
import kotterknife.bindView
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class WorkoutOverviewFragment : Fragment(),
        BaseAdapter.OnItemClickListener<Workout>, MainActivity.OnWorkoutsLoadedListener {


    private val recyclerView: WearableRecyclerView by bindView(R.id.fragment_workout_overview_rv)

    private lateinit var workouts: List<Workout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workouts = arguments.getParcelableArrayList(ARG_WORKOUTS)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onItemClick(t: Workout, v: View) {
        startActivity(WorkoutActivity.newIntent(context, t),
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle())
    }

    private fun setupRecyclerView() {

        val adapter = WearWorkoutOverviewAdapter(context, workouts.sortedWith(WorkoutNameComparator()))

        recyclerView.isEdgeItemsCenteringEnabled = true
        recyclerView.layoutManager = WearableLinearLayoutManager(context)
        recyclerView.adapter = adapter
        adapter.onItemClickListener = this
    }

    override fun onWorkoutLoaded(workouts: List<Workout>) {
        this.workouts = workouts
        setupRecyclerView()
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

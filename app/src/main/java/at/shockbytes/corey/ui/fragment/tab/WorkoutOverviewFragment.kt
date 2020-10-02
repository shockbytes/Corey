package at.shockbytes.corey.ui.fragment.tab

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.WorkoutAdapter
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.activity.CreateWorkoutActivity
import at.shockbytes.corey.ui.activity.WorkoutDetailActivity
import at.shockbytes.corey.util.AppParams
import at.shockbytes.corey.ui.viewmodel.WorkoutOverviewViewModel
import at.shockbytes.util.adapter.BaseAdapter
import at.shockbytes.corey.util.isPortrait
import kotlinx.android.synthetic.main.fragment_workout_overview.*
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    26.10.2015
 */
class WorkoutOverviewFragment : TabBaseFragment<AppComponent>(),
        BaseAdapter.OnItemClickListener<Workout>,
        WorkoutAdapter.OnWorkoutPopupItemSelectedListener {

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    private lateinit var workoutOverviewAdapter: WorkoutAdapter
    private lateinit var viewModel: WorkoutOverviewViewModel

    override val castsActionBarShadow: Boolean = false

    @Inject
    protected lateinit var vmFactory: ViewModelProvider.Factory

    private val layoutManagerForOrientation: RecyclerView.LayoutManager
        get() = if (isPortrait()) {
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        } else {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

    override val layoutId = R.layout.fragment_workout_overview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[WorkoutOverviewViewModel::class.java]
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun onItemClick(t: Workout, position: Int, v: View) {

        val intent = WorkoutDetailActivity.newIntent(requireContext(), t)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
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
            // workoutOverviewAdapter.deleteEntity(w)
            viewModel.deleteWorkout(w)
        }
    }

    override fun onEdit(w: Workout?) {
        val intent = CreateWorkoutActivity.newIntent(requireContext(), w)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity())
        activity?.startActivityForResult(intent, AppParams.REQUEST_CODE_CREATE_WORKOUT, options.toBundle())
    }

    override fun setupViews() {

        workoutOverviewAdapter = WorkoutAdapter(
                requireContext(),
                onWorkoutPopupItemSelectedListener = this,
                onItemClickListener = this
        )

        fragment_training_rv.apply {
            layoutManager = layoutManagerForOrientation
            adapter = workoutOverviewAdapter
        }
    }

    override fun bindViewModel() {

        viewModel.getWorkouts().observe(this, { state ->

            when (state) {

                is WorkoutOverviewViewModel.RetrieveWorkoutState.Success -> {
                    workoutOverviewAdapter.data = state.workouts.toMutableList()
                }
                is WorkoutOverviewViewModel.RetrieveWorkoutState.Error -> {
                    showSnackbar(getString(R.string.snackbar_cannot_load_workouts))
                }
            }
        })
    }

    override fun unbindViewModel() {
        viewModel.getWorkouts().removeObservers(this)
    }

    companion object {

        fun newInstance(): WorkoutOverviewFragment = WorkoutOverviewFragment()
    }
}

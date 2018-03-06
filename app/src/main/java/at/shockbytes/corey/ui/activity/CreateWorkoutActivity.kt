package at.shockbytes.corey.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.activity.core.TintableBackNavigableActivity
import at.shockbytes.corey.ui.fragment.CreateWorkoutFragment

/**
 * @author Martin Macheiner
 * Date: 27.10.2015.
 */
class CreateWorkoutActivity : TintableBackNavigableActivity(),
        TintableBackNavigableActivity.OnTintSystemBarListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_workout)
        setResult(Activity.RESULT_CANCELED, Intent())

        val newWorkout = intent.getBooleanExtra(ARG_NEW_WORKOUT, true)
        val workout = if (!newWorkout) {
            intent.getParcelableExtra(ARG_EDIT)
        } else { null }
        supportFragmentManager.beginTransaction()
                .replace(R.id.activity_create_workout, CreateWorkoutFragment.newInstance(workout))
                .commit()
    }

    override fun injectToGraph(appComponent: AppComponent) {
        // Do nothing
    }

    override fun tint(to: Int, toDark: Int) {
        tintSystemBarsWithText(to, toDark, animated = true)
    }

    companion object {

        private const val ARG_EDIT = "arg_edit_workout"
        private const val ARG_NEW_WORKOUT = "arg_new_workout"

        fun newIntent(context: Context, editWorkout: Workout? = null): Intent {
            return Intent(context, CreateWorkoutActivity::class.java)
                    .putExtra(ARG_EDIT, editWorkout)
                    .putExtra(ARG_NEW_WORKOUT, editWorkout == null)
        }
    }


}


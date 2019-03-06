package at.shockbytes.corey.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import at.shockbytes.core.ui.activity.base.TintableBackNavigableActivity
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.CreateWorkoutFragment

/**
 * Author:  Martin Macheiner
 * Date:    27.10.2015
 */
class CreateWorkoutActivity : TintableBackNavigableActivity<AppComponent>() {

    override val abDefColor: Int = R.color.colorPrimary
    override val abTextDefColor: Int = R.color.white
    override val colorPrimary: Int = R.color.colorPrimary
    override val colorPrimaryDark: Int = R.color.colorPrimaryDark
    override val colorPrimaryText: Int = R.color.colorPrimaryText
    override val sbDefColor: Int = colorPrimaryDark
    override val upIndicator: Int = R.drawable.ic_back_arrow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_workout)
        setResult(Activity.RESULT_CANCELED, Intent())

        val newWorkout = intent.getBooleanExtra(ARG_NEW_WORKOUT, true)
        val workout: Workout? = if (!newWorkout) {
            intent.getParcelableExtra(ARG_EDIT)
        } else null

        supportFragmentManager.beginTransaction()
                .replace(R.id.activity_create_workout, CreateWorkoutFragment.newInstance(workout))
                .commit()
    }

    override fun injectToGraph(appComponent: AppComponent?) = Unit

    override fun bindViewModel() = Unit

    override fun unbindViewModel() = Unit

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
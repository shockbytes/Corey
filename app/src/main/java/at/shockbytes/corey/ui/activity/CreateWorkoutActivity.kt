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
 * Date:    27.10.2015.
 */
class CreateWorkoutActivity : TintableBackNavigableActivity<AppComponent>() {

    override val abDefColor: Int
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
    override val abTextDefColor: Int
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
    override val colorPrimary: Int
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
    override val colorPrimaryDark: Int
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
    override val colorPrimaryText: Int
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
    override val sbDefColor: Int
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
    override val upIndicator: Int
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.

    override fun bindViewModel() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun unbindViewModel() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

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

    override fun injectToGraph(appComponent: AppComponent?) {
        // Do nothing
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
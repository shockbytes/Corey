package at.shockbytes.corey.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.KeyEvent

import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.ui.fragment.WorkoutFragment

class WorkoutActivity : WearableActivity() {

    interface OnWorkoutNavigationListener {

        fun moveToNext()

        fun moveToPrevious()

        fun onEnterAmbient()

        fun onUpdateAmbient()

        fun onExitAmbient()
    }

    private var navigationListener: OnWorkoutNavigationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        setAmbientEnabled()

        val w = intent.getParcelableExtra<Workout>(ARG_WORKOUT)
        val workoutFragment = WorkoutFragment.newInstance(w)
        navigationListener = workoutFragment
        fragmentManager.beginTransaction()
                .replace(R.id.activity_workout_container, workoutFragment)
                .commit()
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        navigationListener?.onEnterAmbient()
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
        navigationListener?.onUpdateAmbient()
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        navigationListener?.onExitAmbient()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        when (keyCode) {
            KeyEvent.KEYCODE_NAVIGATE_NEXT -> navigationListener?.moveToNext()
            KeyEvent.KEYCODE_NAVIGATE_PREVIOUS -> navigationListener?.moveToPrevious()
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {

        private const val ARG_WORKOUT = "arg_workout"

        fun newIntent(context: Context, w: Workout): Intent {
            return Intent(context, WorkoutActivity::class.java).putExtra(ARG_WORKOUT, w)
        }
    }

}

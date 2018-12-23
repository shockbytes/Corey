package at.shockbytes.corey.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import at.shockbytes.core.ui.activity.base.BaseActivity
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.WorkoutFragment
import at.shockbytes.corey.ui.fragment.dialog.WorkoutMessageDialogFragment

class WorkoutActivity : BaseActivity<AppComponent>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val w = intent.getParcelableExtra<Workout>(ARG_WORKOUT)

        setupActionBar(w.displayableName, w.colorResForIntensity, w.darkColorResForIntensity)
        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, WorkoutFragment.newInstance(w))
                .commit()
    }

    override fun bindViewModel() {
    }

    override fun injectToGraph(appComponent: AppComponent?) {
    }

    override fun unbindViewModel() {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            showQuitDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar(workoutName: String, actionbarColor: Int, statusColor: Int) {
        supportActionBar?.let { ab ->
            ab.title = workoutName
            ab.setHomeButtonEnabled(true)
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setHomeAsUpIndicator(R.drawable.ic_cancel)
            ab.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, actionbarColor)))
            window.statusBarColor = ContextCompat.getColor(this, statusColor)
        }
    }

    private fun showQuitDialog() {
        WorkoutMessageDialogFragment.newInstance(WorkoutMessageDialogFragment.MessageType.QUIT)
                .setOnMessageAgreeClickedListener {
                    supportFinishAfterTransition()
                }.show(supportFragmentManager, "dialogfragment-show-quit")
    }

    companion object {

        private const val ARG_WORKOUT = "arg_workout"

        fun newIntent(context: Context, workout: Workout): Intent {
            return Intent(context, WorkoutActivity::class.java)
                    .putExtra(ARG_WORKOUT, workout)
        }
    }

}

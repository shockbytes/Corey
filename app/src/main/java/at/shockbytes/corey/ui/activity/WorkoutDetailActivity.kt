package at.shockbytes.corey.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.transition.Fade
import at.shockbytes.core.ui.activity.base.TintableBackNavigableActivity
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.ExerciseAdapter
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.util.AppUtils
import kotlinx.android.synthetic.main.activity_workout_detail.*

/**
 * Author:  Martin Macheiner
 * Date:    01.11.2015
 */
class WorkoutDetailActivity : TintableBackNavigableActivity<AppComponent>() {

    override val abDefColor: Int = R.color.colorPrimary
    override val abTextDefColor: Int = R.color.white
    override val colorPrimary: Int = R.color.colorPrimary
    override val colorPrimaryDark: Int = R.color.colorPrimaryDark
    override val colorPrimaryText: Int = R.color.colorPrimaryText
    override val sbDefColor: Int = colorPrimaryDark
    override val upIndicator: Int = R.drawable.ic_back_arrow
    override val enableActivityTransition = false

    private lateinit var workout: Workout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.exitTransition = Fade(Fade.OUT)
        setContentView(R.layout.activity_workout_detail)

        workout = intent.getParcelableExtra(ARG_WORKOUT)!!
        setupViews()
    }

    override fun injectToGraph(appComponent: AppComponent?) = Unit

    override fun bindViewModel() = Unit

    override fun unbindViewModel() = Unit

    override fun onStart() {
        super.onStart()
        supportActionBar?.elevation = 0f
    }

    override fun backwardAnimation() {
        super.backwardAnimation()
        activity_training_btn_start.hide()
    }

    override fun onStop() {
        super.onStop()
        supportActionBar?.elevation = AppUtils.convertDpInPixel(8, this).toFloat()
    }

    private fun setupViews() {

        tintSystemBarsWithText(
                actionBarColor = ContextCompat.getColor(this, workout.colorResForIntensity),
                statusBarColor = ContextCompat.getColor(this, workout.darkColorResForIntensity),
                animated = false,
                title = "",
                actionBarTextColor = Color.WHITE,
                useSameColorsForBoth = false
        )

        activity_training_detail_imgview_ext_toolbar.setBackgroundResource(workout.colorResForIntensity)
        activity_training_detail_imgview_body_region.setImageDrawable(AppUtils.createRoundedBitmapFromResource(this,
                workout.imageResForBodyRegion, workout.colorResForIntensity))

        activity_training_detail_txt_title.text = workout.displayableName
        activity_training_detail_txt_duration.text = getString(R.string.duration_with_minutes, workout.duration)
        activity_training_detail_txt_exercise_count.text = getString(R.string.exercises_with_count, workout.exerciseCount)

        activity_training_recyclerview.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@WorkoutDetailActivity)
            adapter = ExerciseAdapter(this@WorkoutDetailActivity).apply {
                data = workout.exercises
            }
        }

        activity_training_btn_start.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            startActivity(WorkoutActivity.newIntent(applicationContext, workout), options.toBundle())
        }

        activity_training_detail_imgview_equipment
                .setImageDrawable(AppUtils.createRoundedBitmapFromResource(this,
                        CoreyUtils.getImageByEquipment(workout.equipment), R.color.equipmentBackground)
                )
    }

    companion object {

        private const val ARG_WORKOUT = "arg_workout"

        fun newIntent(context: Context, workout: Workout): Intent {
            return Intent(context, WorkoutDetailActivity::class.java)
                    .putExtra(ARG_WORKOUT, workout)
        }
    }
}

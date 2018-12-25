package at.shockbytes.corey.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Fade
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import at.shockbytes.core.ui.activity.base.TintableBackNavigableActivity
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.ExerciseAdapter
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.util.AppUtils
import kotterknife.bindView

/**
 * Author:  Martin Macheiner
 * Date:    01.11.2015
 */
class WorkoutDetailActivity : TintableBackNavigableActivity<AppComponent>() {

    override val abDefColor: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val abTextDefColor: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val colorPrimary: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val colorPrimaryDark: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val colorPrimaryText: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val sbDefColor: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val upIndicator: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun bindViewModel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unbindViewModel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val imgViewExtToolbar: ImageView by bindView(R.id.activity_training_detail_imgview_ext_toolbar)
    private val imgViewMuscles: ImageView by bindView(R.id.activity_training_detail_imgview_body_region)
    private val imgViewEquipment: ImageView by bindView(R.id.activity_training_detail_imgview_equipment)
    private val txtDuration: TextView by bindView(R.id.activity_training_detail_txt_duration)
    private val txtExerciseCount: TextView by bindView(R.id.activity_training_detail_txt_exercise_count)
    private val recyclerViewExercises: RecyclerView by bindView(R.id.activity_training_recyclerview)
    private val btnStart: Button by bindView(R.id.activity_training_btn_start)
    private val txtName: TextView by bindView(R.id.activity_training_detail_txt_title)

    override val enableActivityTransition = false

    private lateinit var workout: Workout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.exitTransition = Fade(Fade.OUT)

        setContentView(R.layout.activity_workout_detail)

        workout = intent.getParcelableExtra(ARG_WORKOUT)
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        // Do nothing
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.elevation = 0f
    }

    override fun onStop() {
        super.onStop()
        supportActionBar?.elevation = AppUtils.convertDpInPixel(8, this).toFloat()
    }

    private fun setupViews() {

        btnStart.text = getString(R.string.activity_workout_detail_btn_start, workout.displayableName)

        imgViewExtToolbar.setBackgroundResource(workout.colorResForIntensity)
        imgViewMuscles.setImageDrawable(AppUtils.createRoundedBitmapFromResource(this,
                workout.imageResForBodyRegion, workout.colorResForIntensity))

        txtName.text = workout.displayableName
        txtDuration.text = getString(R.string.duration_with_minutes, workout.duration)
        txtExerciseCount.text = getString(R.string.exercises_with_count, workout.exerciseCount)

        val exerciseAdapter = ExerciseAdapter(this, listOf())
        recyclerViewExercises.layoutManager = LinearLayoutManager(this)
        recyclerViewExercises.adapter = exerciseAdapter
        exerciseAdapter.data = workout.exercises

        btnStart.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            startActivity(WorkoutActivity.newIntent(applicationContext, workout), options.toBundle())
        }

        imgViewEquipment.setImageDrawable(AppUtils.createRoundedBitmapFromResource(this,
                CoreyUtils.getImageByEquipment(workout.equipment), R.color.equipmentBackground))
    }

    companion object {

        private const val ARG_WORKOUT = "arg_workout"

        fun newIntent(context: Context, workout: Workout): Intent {
            return Intent(context, WorkoutDetailActivity::class.java)
                    .putExtra(ARG_WORKOUT, workout)
        }
    }

}

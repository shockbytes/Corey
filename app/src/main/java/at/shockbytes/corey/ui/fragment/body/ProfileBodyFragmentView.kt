package at.shockbytes.corey.ui.fragment.body

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.body.info.BodyInfo
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.common.core.util.view.CoreyViewManager
import at.shockbytes.corey.ui.fragment.BaseFragment
import at.shockbytes.corey.user.CoreyUser
import at.shockbytes.util.AppUtils
import butterknife.BindView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * @author Martin Macheiner
 * Date: 05-Mar-18.
 */

class ProfileBodyFragmentView(fragment: BaseFragment,
                              bodyInfo: BodyInfo,
                              bodyManager: BodyManager,
                              private val user: CoreyUser)
    : BodyFragmentView(fragment, bodyInfo, bodyManager), Palette.PaletteAsyncListener, Callback {

    @BindView(R.id.fragment_body_pb_weight)
    protected lateinit var progressBarWeight: ProgressBar

    @BindView(R.id.fragment_body_img_avatar)
    protected lateinit var imgAvatar: ImageView

    @BindView(R.id.fragment_body_txt_weight)
    protected lateinit var txtWeight: TextView

    @BindView(R.id.fragment_body_txt_bmi)
    protected lateinit var txtBMI: TextView

    @BindView(R.id.fragment_body_txt_name)
    protected lateinit var txtName: TextView

    @BindView(R.id.fragment_body_header)
    protected lateinit var headerLayout: RelativeLayout

    @BindView(R.id.fragment_body_txt_dream_weight)
    protected lateinit var txtDreamWeight: TextView

    override val layoutId = R.layout.fragment_body_view_profile

    override fun onDesiredWeightChanged(changed: Int) {
        bodyInfo.dreamWeight = changed
        setupView()
    }

    override fun onBodyGoalAdded(g: Goal) {
        // Not interesting...
    }

    override fun onBodyGoalDeleted(g: Goal) {
        // Not interesting...
    }

    override fun onBodyGoalChanged(g: Goal) {
        // Not interesting...
    }

    override fun setupView() {

        val weight = "${bodyInfo.latestWeightPoint.weight} $weightUnit"
        txtWeight.text = weight
        val bmi = "BMI: ${bodyInfo.latestBmi}"
        txtBMI.text = bmi
        val dreamWeight = "${bodyInfo.dreamWeight}$weightUnit"
        txtDreamWeight.text = dreamWeight
        txtName.text = user.name

        val uri = user.photoUrl
        if (uri != null) {
            Picasso.with(fragment.context!!).load(uri).into(imgAvatar, this)
        }
    }

    override fun animateView(startDelay: Long) {
        animateContent()
    }

    override fun onGenerated(palette: Palette?) {
        val defaultColor = ContextCompat.getColor(fragment.context!!, R.color.colorPrimary)
        palette?.getDarkMutedColor(defaultColor)?.let { headerColor ->
            CoreyViewManager.backgroundColorTransition(headerLayout, defaultColor, headerColor)
        }
    }

    override fun onSuccess() {

        val bm = (imgAvatar.drawable as? BitmapDrawable)?.bitmap
        if (bm != null) {
            imgAvatar.setImageDrawable(AppUtils.createRoundedBitmap(fragment.context!!, bm))
            Palette.from(bm).generate(this)
        }
    }

    override fun onError() {
        imgAvatar.setImageResource(R.drawable.ic_user_default)
    }

    private fun animateContent() {

        val weightProgress = CoreyUtils.calculateDreamWeightProgress(
                bodyInfo.highestWeight,
                bodyInfo.latestWeightPoint.weight,
                bodyInfo.dreamWeight.toDouble())

        // Animate image
        val imgAnimAlpha = ObjectAnimator.ofFloat(imgAvatar, "alpha", 0f, 1f)
        val imgAnimScaleX = ObjectAnimator.ofFloat(imgAvatar, "scaleX", 0.7f, 1f)
        val imgAnimScaleY = ObjectAnimator.ofFloat(imgAvatar, "scaleY", 0.7f, 1f)
        val imageSet = AnimatorSet()
        imageSet.play(imgAnimAlpha).with(imgAnimScaleX).with(imgAnimScaleY)
        imageSet.duration = 500
        imageSet.interpolator = DecelerateInterpolator()
        imageSet.startDelay = 200
        imageSet.start()

        // Animate secondary weight progress
        val secondaryWeightAnimation = ObjectAnimator.ofInt(progressBarWeight, "secondaryProgress", 100)
        secondaryWeightAnimation.startDelay = 500
        secondaryWeightAnimation.duration = 750
        secondaryWeightAnimation.interpolator = AccelerateDecelerateInterpolator()
        secondaryWeightAnimation.start()

        // Animate weight progress
        val weightAnimation = ObjectAnimator.ofInt(progressBarWeight, "progress", weightProgress)
        weightAnimation.startDelay = 200
        weightAnimation.duration = 1500
        weightAnimation.interpolator = AnticipateOvershootInterpolator(1.0f, 4.0f)
        weightAnimation.start()

        // Add all TextViews to ArrayList, we're going to animate them later
        val animatedTextViews = listOf(txtName, txtWeight, txtBMI, txtDreamWeight)

        // Animate all ListViews
        val textStartDelay = 150
        for (i in animatedTextViews.indices) {

            //Animate weight txtExercise
            val txtAnimAlpha = ObjectAnimator.ofFloat(animatedTextViews[i], "alpha", 0.3f, 1f)
            val txtAnimScaleX = ObjectAnimator.ofFloat(animatedTextViews[i], "scaleX", 0.3f, 1f)
            val txtAnimScaleY = ObjectAnimator.ofFloat(animatedTextViews[i], "scaleY", 0.3f, 1f)
            val txtSet = AnimatorSet()
            txtSet.play(txtAnimAlpha).with(txtAnimScaleX).with(txtAnimScaleY)
            txtSet.duration = 150
            txtSet.startDelay = (textStartDelay * (i + 1)).toLong()
            txtSet.interpolator = DecelerateInterpolator()
            txtSet.start()
        }


    }


}
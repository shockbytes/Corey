package at.shockbytes.corey.ui.fragment.body

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import at.shockbytes.core.image.ImageLoader
import at.shockbytes.core.image.ImageLoadingCallback
import at.shockbytes.core.model.ShockbytesUser
import at.shockbytes.corey.R
import at.shockbytes.corey.data.body.info.BodyInfo
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.common.core.util.view.CoreyViewManager
import at.shockbytes.corey.dagger.AppComponent
import kotlinx.android.synthetic.main.fragment_body_view_profile.*
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    05.02.2018
 */
class ProfileBodyFragmentView : BodySubFragment(), Palette.PaletteAsyncListener, ImageLoadingCallback {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun unbindViewModel() = Unit
    override fun bindViewModel() = Unit
    override fun setupViews() = Unit

    override val layoutId = R.layout.fragment_body_view_profile

    fun setProfileData(bodyInfo: BodyInfo, user: ShockbytesUser, weightUnit: String) {
        val weight = "${bodyInfo.latestWeightPoint.weight} $weightUnit"
        fragment_body_txt_weight.text = weight
        val bmi = "BMI: ${bodyInfo.latestBmi}"
        fragment_body_txt_bmi.text = bmi
        val dreamWeight = "${bodyInfo.dreamWeight}$weightUnit"
        fragment_body_txt_dream_weight.text = dreamWeight
        fragment_body_txt_name.text = user.displayName

        user.photoUrl?.let { uri ->
            context?.let { context ->
                imageLoader.loadImageUri(context, uri, fragment_body_img_avatar, R.drawable.ic_user_default, true, this, Pair(false, true))
            }
        }

        animateContent(bodyInfo)
    }

    override fun animateView(startDelay: Long) {
    }

    override fun onGenerated(palette: Palette?) {
        val defaultColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
        palette?.getDarkMutedColor(defaultColor)?.let { headerColor ->
            CoreyViewManager.backgroundColorTransition(fragment_body_header, defaultColor, headerColor)
        }
    }

    override fun onImageResourceReady(resource: Drawable?) {
        (resource as? BitmapDrawable)?.bitmap?.let { bitmap ->
            Palette.from(bitmap).generate(this)
        }
    }

    override fun onImageLoadingFailed(e: Exception?) {
        fragment_body_img_avatar.setImageResource(R.drawable.ic_user_default)
    }

    private fun animateContent(bodyInfo: BodyInfo) {

        val weightProgress = CoreyUtils.calculateDreamWeightProgress(
                bodyInfo.highestWeight,
                bodyInfo.latestWeightPoint.weight,
                bodyInfo.dreamWeight.toDouble())

        // Animate image
        val imgAnimAlpha = ObjectAnimator.ofFloat(fragment_body_img_avatar, "alpha", 0f, 1f)
        val imgAnimScaleX = ObjectAnimator.ofFloat(fragment_body_img_avatar, "scaleX", 0.7f, 1f)
        val imgAnimScaleY = ObjectAnimator.ofFloat(fragment_body_img_avatar, "scaleY", 0.7f, 1f)
        val imageSet = AnimatorSet()
        imageSet.play(imgAnimAlpha).with(imgAnimScaleX).with(imgAnimScaleY)
        imageSet.duration = 500
        imageSet.interpolator = DecelerateInterpolator()
        imageSet.startDelay = 200
        imageSet.start()

        // Animate secondary weight progress
        val secondaryWeightAnimation = ObjectAnimator.ofInt(fragment_body_pb_weight, "secondaryProgress", 100)
        secondaryWeightAnimation.startDelay = 500
        secondaryWeightAnimation.duration = 750
        secondaryWeightAnimation.interpolator = AccelerateDecelerateInterpolator()
        secondaryWeightAnimation.start()

        // Animate weight progress
        val weightAnimation = ObjectAnimator.ofInt(fragment_body_pb_weight, "progress", weightProgress)
        weightAnimation.startDelay = 200
        weightAnimation.duration = 1500
        weightAnimation.interpolator = AnticipateOvershootInterpolator(1.0f, 4.0f)
        weightAnimation.start()

        // Add all TextViews to ArrayList, we're going to animate them later
        val animatedTextViews = listOf(
                fragment_body_txt_name,
                fragment_body_txt_weight,
                fragment_body_txt_bmi,
                fragment_body_txt_dream_weight
        )

        // Animate all ListViews
        val textStartDelay = 150
        for (i in animatedTextViews.indices) {

            // Animate weight txtExercise
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
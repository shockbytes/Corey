package at.shockbytes.corey.util

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.ui.viewmodel.NutritionViewModel

fun Fragment.isPortrait(): Boolean {
    return this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

fun Drawable.toBitmap(): Bitmap {

    if (this is BitmapDrawable) {
        return this.bitmap
    }

    val bitmap = Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)

    return bitmap
}

inline fun <reified T: BaseViewModel> FragmentActivity.viewModelOfActivity(vmFactory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, vmFactory)[T::class.java]
}

inline fun <reified T: BaseViewModel> Fragment.viewModelOf(vmFactory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, vmFactory)[T::class.java]
}
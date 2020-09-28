package at.shockbytes.corey.common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.math.BigDecimal
import java.math.RoundingMode

fun Activity.hideKeyboard() {
    val view = this.currentFocus
    view?.let { v ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun View.setVisible(isVisible: Boolean) {
    val visibility = if (isVisible) View.VISIBLE else View.GONE
    this.visibility = visibility
}

fun Double.roundDouble(digits: Int): Double {

    if (this == 0.0 || digits < 0 || this == Double.POSITIVE_INFINITY || this == Double.NaN || this == Double.NaN || this == Double.NEGATIVE_INFINITY) {
        return 0.00
    }

    return BigDecimal(this).setScale(digits, RoundingMode.HALF_UP).toDouble()
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

fun isListNotEmpty(data: List<*>): Boolean = data.isNotEmpty()
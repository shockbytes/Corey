package at.shockbytes.corey.common.core.util.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import at.shockbytes.corey.common.core.R

/**
 * This is an own implementation of the SliderPreference, which allows the user
 * to choose between an interval from 0 - x seconds. This interval is the
 * location update interval in the TrackingService class.
 *
 * Author:  Martin Macheiner
 */
class SliderPreference : DialogPreference, OnSeekBarChangeListener, OnClickListener {

    private var seekbar: SeekBar? = null
    private var valueText: TextView? = null

    private var unit: String? = null

    private var defaultVal: Int = 0
    private var maxVal: Int = 0
    private var value: Int = 0

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet) {

        // Get string value for suffix
        val unitId = attrs.getAttributeResourceValue(androidns, "text", 0)
        unit = if (unitId == 0)
            attrs.getAttributeValue(androidns, "text")
        else
            context.getString(unitId)

        defaultVal = attrs.getAttributeIntValue(androidns, "defaultValue", 5)
        maxVal = attrs.getAttributeIntValue(androidns, "max", 5)
    }

    override fun onCreateDialogView(): View {
        val v = LayoutInflater.from(context).inflate(R.layout.sl_slider_pref, null, false)

        valueText = v.findViewById(R.id.sl_slider_pref_txt_value) as TextView
        seekbar = v.findViewById(R.id.sl_slider_pref_seekbar) as SeekBar

        if (shouldPersist()) {
            value = getPersistedInt(defaultVal)
        }

        seekbar?.max = maxVal
        seekbar?.progress = value
        seekbar?.setOnSeekBarChangeListener(this)
        return v
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        seekbar?.max = maxVal
        seekbar?.progress = value
    }

    override fun onSetInitialValue(
        restorePersistedValue: Boolean,
        defaultValue: Any?
    ) {
        super.onSetInitialValue(restorePersistedValue, defaultValue)

        value = if (restorePersistedValue) {
            if (shouldPersist()) getPersistedInt(defaultVal) else 0
        } else {
            defaultValue as? Int ?: defaultVal
        }
    }

    override fun showDialog(state: Bundle?) {
        super.showDialog(state)

        val positiveButton = (dialog as AlertDialog)
                .getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        if (shouldPersist()) {
            value = seekbar?.progress ?: 0
            persistInt(value)
            callChangeListener(value)
            dialog.dismiss()
        }
    }

    // --------------------------- OnSeekBarChangedListener ---------------------------
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val t = progress.toString()
        valueText?.text = t.plus(" ").plus(unit)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    companion object {

        private val androidns = "http://schemas.android.com/apk/res/android"
    }
    // --------------------------------------------------------------------------------
}

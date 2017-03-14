package at.shockbytes.corey.common.core.util.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import at.shockbytes.corey.common.core.R;


/**
 * This is an own implementation of the SliderPreference, which allows the user
 * to choose between an interval from 0 - x seconds. This interval is the
 * location update interval in the TrackingService class.
 * 
 * @author Martin Macheiner
 *
 */
public class SliderPreference extends DialogPreference implements
		OnSeekBarChangeListener, OnClickListener {

	private static final String androidns = "http://schemas.android.com/apk/res/android";

	private SeekBar mSeekBar;
	private TextView mValueText;

	private String mUnit;

	private int mDefault;
	private int mMax;
	private int mValue;

	/**
	 * @param context
	 *            Application context
	 * @param attrs
	 *            AttributeSet from xml definition
	 */
	public SliderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(attrs);
	}

	/**
	 * @param context
	 *            Application context
	 * @param attrs
	 *            AttributeSet from xml definition
	 * @param defStyleAttr
	 *            Integer with Styling
	 */
	public SliderPreference(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(attrs);
	}

	/**
	 * Initializes all values from AttributeSet
	 *
	 * @param _attrs
	 *            AttributeSet from constructor
	 */
	private void initialize(AttributeSet _attrs) {

		//Get string value for suffix
		int unitId = _attrs.getAttributeResourceValue(androidns, "txtExercise", 0);
		mUnit = (unitId == 0) ? _attrs.getAttributeValue(androidns, "txtExercise")
				: getContext().getString(unitId);

		mDefault = _attrs.getAttributeIntValue(androidns, "defaultValue", 0);
		mMax = _attrs.getAttributeIntValue(androidns, "max", 5);
	}

	@Override
	protected View onCreateDialogView() {

		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.sl_slider_pref, null);

		mValueText = (TextView) v.findViewById(R.id.sl_slider_pref_txt_value);

		mSeekBar = (SeekBar) v.findViewById(R.id.sl_slider_pref_seekbar);
		mSeekBar.setOnSeekBarChangeListener(this);

		if (shouldPersist()) {
			mValue = getPersistedInt(mDefault);
		}

		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);

		return v;
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		super.onSetInitialValue(restorePersistedValue, defaultValue);

		if (restorePersistedValue) {
			mValue = (shouldPersist()) ? getPersistedInt(mDefault) : 0;
		} else {
			mValue = (int) defaultValue;
		}
	}

	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);

		Button positiveButton = ((AlertDialog) getDialog())
				.getButton(AlertDialog.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (shouldPersist()) {

			mValue = mSeekBar.getProgress();
			persistInt(mValue);
			callChangeListener(mValue);
			getDialog().dismiss();
		}

	}

	//--------------------------- OnSeekBarChangedListener ---------------------------
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		String t = String.valueOf(progress);
		mValueText.setText((mUnit == null) ? t : t.concat(" " + mUnit));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
	//--------------------------------------------------------------------------------

}

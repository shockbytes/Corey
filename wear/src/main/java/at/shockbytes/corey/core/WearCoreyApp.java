package at.shockbytes.corey.core;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

import at.shockbytes.corey.dagger.DaggerWearAppComponent;
import at.shockbytes.corey.dagger.WearAppComponent;
import at.shockbytes.corey.dagger.WearAppModule;

/**
 * @author Martin Macheiner
 *         Date: 21.02.2017.
 */
public class WearCoreyApp extends Application {

	private WearAppComponent appComponent;

	@Override
	public void onCreate() {
		super.onCreate();

        //Realm.init(this);
		JodaTimeAndroid.init(this);


		appComponent = DaggerWearAppComponent.builder()
			.wearAppModule(new WearAppModule(this))
			.build();
	}

	public WearAppComponent getAppComponent() {
		return appComponent;
	}
}

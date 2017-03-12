package at.shockbytes.corey.core;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

import at.shockbytes.corey.dagger.AppComponent;
import at.shockbytes.corey.dagger.AppModule;
import at.shockbytes.corey.dagger.DaggerAppComponent;
import io.realm.Realm;

/**
 * @author Martin Macheiner
 *         Date: 21.02.2017.
 */
public class CoreyApp extends Application {

	private AppComponent appComponent;

	@Override
	public void onCreate() {
		super.onCreate();

        Realm.init(this);
		JodaTimeAndroid.init(this);

		appComponent = DaggerAppComponent.builder()
			.appModule(new AppModule(this))
			.build();
	}

	public AppComponent getAppComponent() {
		return appComponent;
	}
}

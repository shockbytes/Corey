package at.shockbytes.corey.dagger

import android.app.Application
import android.content.SharedPreferences
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.GoogleFitBodyRepository
import at.shockbytes.corey.data.body.bmr.BmrComputation
import at.shockbytes.corey.data.body.bmr.RevisedHarrisBenedictBmrComputation
import at.shockbytes.corey.data.firebase.FirebaseDatabaseAccess
import at.shockbytes.corey.data.goal.FirebaseGoalsRepository
import at.shockbytes.corey.data.goal.GoalsRepository
import at.shockbytes.corey.data.google.CoreyGoogleApi
import at.shockbytes.corey.ui.fragment.body.weight.filter.RawWeightLineFilter
import at.shockbytes.corey.ui.fragment.body.weight.filter.RunningAverageWeightLineFilter
import at.shockbytes.corey.ui.fragment.body.weight.filter.WeightLineFilter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BodyModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideCoreyGoogleApiClient(): CoreyGoogleApi {
        return CoreyGoogleApi(app.applicationContext)
    }

    @Provides
    fun provideBmrComputation(): BmrComputation {
        return RevisedHarrisBenedictBmrComputation()
    }

    @Provides
    @Singleton
    fun provideBodyRepository(
        coreyGoogleApi: CoreyGoogleApi,
        preferences: SharedPreferences,
        firebase: FirebaseDatabaseAccess,
        userSettings: UserSettings
    ): BodyRepository {
        return GoogleFitBodyRepository(coreyGoogleApi, preferences, firebase, userSettings)
    }

    @Provides
    @Singleton
    fun provideGoalsRepository(firebase: FirebaseDatabaseAccess): GoalsRepository {
        return FirebaseGoalsRepository(firebase)
    }

    @Provides
    fun provideWeightLineFilters(): Array<WeightLineFilter> {
        return arrayOf(RawWeightLineFilter(), RunningAverageWeightLineFilter())
    }
}
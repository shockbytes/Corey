package at.shockbytes.corey.dagger

import android.app.Application
import android.content.SharedPreferences
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.GoogleFitBodyRepository
import at.shockbytes.corey.data.body.bmr.BmrComputation
import at.shockbytes.corey.data.body.bmr.RevisedHarrisBenedictBmrComputation
import at.shockbytes.corey.data.goal.FirebaseGoalsRepository
import at.shockbytes.corey.data.goal.GoalsRepository
import at.shockbytes.corey.data.nutrition.FirebaseNutritionRepository
import at.shockbytes.corey.data.nutrition.NutritionRepository
import at.shockbytes.corey.ui.fragment.body.weight.filter.RawWeightLineFilter
import at.shockbytes.corey.ui.fragment.body.weight.filter.RunningAverageWeightLineFilter
import at.shockbytes.corey.ui.fragment.body.weight.filter.WeightLineFilter
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

@Module
class BodyModule(private val app: Application) {

    @Provides
    fun provideBmrComputation(): BmrComputation {
        return RevisedHarrisBenedictBmrComputation()
    }

    @Provides
    @Singleton
    fun provideBodyManager(
            preferences: SharedPreferences,
            firebase: FirebaseDatabase,
            bmrComputation: BmrComputation
    ): BodyRepository {
        return GoogleFitBodyRepository(app.applicationContext, preferences, firebase, bmrComputation)
    }

    @Provides
    @Singleton
    fun provideGoalsRepository(firebase: FirebaseDatabase): GoalsRepository {
        return FirebaseGoalsRepository(firebase)
    }

    @Provides
    fun provideWeightLineFilters(): Array<WeightLineFilter> {
        return arrayOf(RawWeightLineFilter(), RunningAverageWeightLineFilter())
    }

    @Provides
    fun provideNutritionRepository(
            firebase: FirebaseDatabase,
            schedulers: SchedulerFacade
    ): NutritionRepository {
        return FirebaseNutritionRepository(firebase, schedulers)
    }

}
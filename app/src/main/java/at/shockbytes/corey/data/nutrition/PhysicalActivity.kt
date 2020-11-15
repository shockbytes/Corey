package at.shockbytes.corey.data.nutrition

import android.content.Context
import at.shockbytes.corey.R
import at.shockbytes.corey.data.body.bmr.Bmr

sealed class PhysicalActivity {

    abstract fun activityName(context: Context): String
    abstract val kcal: Int

    data class BasalMetabolicRate(private val bmr: Bmr) : PhysicalActivity() {

        override val kcal: Int
            get() = bmr.kcal

        override fun activityName(context: Context): String {
            return context.getString(R.string.bmr_full)
        }
    }

    data class Activity(
        private val externalName: String,
        override val kcal: Int
    ) : PhysicalActivity() {
        override fun activityName(context: Context): String = externalName
    }
}

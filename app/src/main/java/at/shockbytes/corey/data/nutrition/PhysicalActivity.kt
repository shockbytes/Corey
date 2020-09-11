package at.shockbytes.corey.data.nutrition

sealed class PhysicalActivity {

    abstract val activityName: String
    abstract val kcal: Int

    class Activity(
            override val activityName: String,
            override val kcal: Int
    ): PhysicalActivity()

    class BasalMetabolicRate(
            override val activityName: String,
            override val kcal: Int
    ): PhysicalActivity()
}

package at.shockbytes.corey.data.body.bmr

import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.Gender

class RevisedHarrisBenedictBmrComputation : BmrComputation {

    override val name: String = "Revised Harris-Benedict"

    override fun compute(
        gender: Gender,
        weight: Double,
        height: Int,
        age: Int,
        activityLevel: ActivityLevel
    ): Bmr {

        val kcal = when (gender) {
            Gender.MALE -> computeMaleBmr(weight, height, age)
            Gender.FEMALE -> computeFemaleBmr(weight, height, age)
        }
        val kcalWithActivity = kcal.times(activityLevel.factor).toInt()

        return Bmr(kcal, kcalWithActivityFactor = kcalWithActivity, computationAlgorithm = name)
    }

    private fun computeMaleBmr(weight: Double, heightInCm: Int, age: Int): Int {
        return ((88.4 + 13.4 * weight) + (4.8 * heightInCm) - (5.68 * age)).toInt()
    }

    private fun computeFemaleBmr(weight: Double, heightInCm: Int, age: Int): Int {
        return ((447.6 + 9.25 * weight) + (3.10 * heightInCm) - (4.33 * age)).toInt()
    }
}
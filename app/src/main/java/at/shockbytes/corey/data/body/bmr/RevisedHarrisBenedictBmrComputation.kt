package at.shockbytes.corey.data.body.bmr

import at.shockbytes.corey.common.core.Gender
import at.shockbytes.corey.data.body.CoreyUser
import io.reactivex.Single

class RevisedHarrisBenedictBmrComputation : BmrComputation {

    override val name: String = "Revised Harris-Benedict Formula"

    override fun compute(user: CoreyUser): Single<Bmr> {
        return Single
                .fromCallable {
                    when (user.gender) {
                        Gender.MALE -> computeMaleBmr(user.weight, user.heightInCm, user.age)
                        Gender.FEMALE -> computeFemaleBmr(user.weight, user.heightInCm, user.age)
                    }
                }
                .map { kcal ->
                    Bmr(kcal, computationAlgorithm = name)
                }
    }

    private fun computeMaleBmr(weight: Double, heightInCm: Int, age: Int): Int {
        return ((88.4 + 13.4 * weight) + (4.8 * heightInCm) - (5.68 * age)).toInt()
    }

    private fun computeFemaleBmr(weight: Double, heightInCm: Int, age: Int): Int {
        return ((447.6 + 9.25 * weight) + (3.10 * heightInCm) - (4.33 * age)).toInt()
    }
}
package at.shockbytes.corey.data.nutrition.lookup

import io.reactivex.Single

interface KcalLookup {

    fun lookup(foodName: String): Single<KcalLookupResult>
}
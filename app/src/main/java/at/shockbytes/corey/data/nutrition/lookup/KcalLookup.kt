package at.shockbytes.corey.data.nutrition.lookup

import io.reactivex.Single

interface KcalLookup {

    val dataSource: LookupDataSource

    fun lookup(foodName: String): Single<KcalLookupResult>
}
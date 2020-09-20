package at.shockbytes.corey.data.nutrition.lookup.usda

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.data.nutrition.lookup.KcalLookup
import at.shockbytes.corey.data.nutrition.lookup.KcalLookupResult
import io.reactivex.Single

class UsdaKcalLookup(
        private val usdaApi: UsdaApi,
        private val schedulers: SchedulerFacade
) : KcalLookup {

    override fun lookup(foodName: String): Single<KcalLookupResult> {
        TODO("Not yet implemented")
    }
}
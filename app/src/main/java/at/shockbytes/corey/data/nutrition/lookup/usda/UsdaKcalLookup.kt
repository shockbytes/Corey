package at.shockbytes.corey.data.nutrition.lookup.usda

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.R
import at.shockbytes.corey.data.nutrition.lookup.KcalLookup
import at.shockbytes.corey.data.nutrition.lookup.KcalLookupItem
import at.shockbytes.corey.data.nutrition.lookup.KcalLookupResult
import at.shockbytes.corey.data.nutrition.lookup.LookupDataSource
import io.reactivex.Single

class UsdaKcalLookup(
    private val usdaApi: UsdaApi,
    private val schedulers: SchedulerFacade
) : KcalLookup {

    private val requestedNutrients = listOf(
        UsdaApiNutrients.ENERGY
    ).map { it.code }

    override val dataSource = LookupDataSource(
        name = "USDA kcal lookup",
        url = DATA_SOURCE_URL,
        icon = R.drawable.ic_usda
    )

    override fun lookup(foodName: String): Single<KcalLookupResult> {
        return usdaApi
            .search(query = foodName)
            .flatMap { response ->
                usdaApi.lookup(
                    fdcIds = response.fdcIds,
                    nutrients = requestedNutrients
                )
            }
            .map { lookupResponses ->

                val items = lookupResponses
                    .filter { it.kcal != null }
                    .map { response ->
                        KcalLookupItem.Standard(
                            dishName = response.description,
                            kcal = response.kcal!!,
                            portionSize = STANDARD_PORTION_SIZE
                        )
                    }

                KcalLookupResult(dataSource, items)
            }
            .subscribeOn(schedulers.io)
    }

    companion object {

        private const val DATA_SOURCE_URL = "https://ndb.nal.usda.gov/"
        private const val STANDARD_PORTION_SIZE = "100g"
    }
}
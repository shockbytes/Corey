package at.shockbytes.corey.data.nutrition.lookup.edamam

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.R
import at.shockbytes.corey.data.nutrition.lookup.KcalLookup
import at.shockbytes.corey.data.nutrition.lookup.KcalLookupItem
import at.shockbytes.corey.data.nutrition.lookup.KcalLookupResult
import at.shockbytes.corey.data.nutrition.lookup.LookupDataSource
import io.reactivex.Single

class EdamamKcalLookup(
    private val edamamApi: EdamamApi,
    private val schedulers: SchedulerFacade
) : KcalLookup {

    override val dataSource = LookupDataSource(
        name = "Edamam Food Database API",
        url = "https://developer.edamam.com/",
        icon = R.drawable.ic_edamam
    )

    override fun lookup(foodName: String): Single<KcalLookupResult> {
        return edamamApi.textSearch(keyword = foodName)
            .map { responses ->
                val items = responses
                    .results
                    .filter { it.kcal != null }
                    .map { response ->
                        KcalLookupItem.WithImage(
                            dishName = foodName,
                            portionSize = DEFAULT_PORTION_SIZE,
                            kcal = response.kcal!!,
                            imageUrl = response.image
                        )
                    }

                KcalLookupResult(dataSource, items)
            }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
    }

    companion object {

        private const val DEFAULT_PORTION_SIZE = "100g"
    }
}
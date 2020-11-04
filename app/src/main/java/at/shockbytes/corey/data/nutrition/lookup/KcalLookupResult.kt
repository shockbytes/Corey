package at.shockbytes.corey.data.nutrition.lookup

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KcalLookupResult(
    val dataSource: LookupDataSource,
    val items: List<KcalLookupItem>
) : Parcelable

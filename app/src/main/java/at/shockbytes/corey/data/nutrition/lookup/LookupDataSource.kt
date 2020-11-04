package at.shockbytes.corey.data.nutrition.lookup

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LookupDataSource(
    val name: String,
    val url: String,
    val icon: Int
) : Parcelable
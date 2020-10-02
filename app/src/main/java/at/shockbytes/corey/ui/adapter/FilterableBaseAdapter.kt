package at.shockbytes.corey.ui.adapter

import android.content.Context
import at.shockbytes.corey.common.core.Sortable
import at.shockbytes.util.adapter.BaseAdapter

/**
 * Author:  Martin Macheiner
 * Date:    06.03.2018
 */
abstract class FilterableBaseAdapter<T : Sortable>(
    context: Context,
    data: List<T>,
    onItemClickListener: OnItemClickListener<T>,
    private val filterPredicate: (T, String) -> Boolean
) : BaseAdapter<T>(context, onItemClickListener) {

    private var originalData = ArrayList(data)

    fun filter(query: String) {

        if (query.isNotEmpty()) {
            val filtered = filterList(data, query)
            setData(filtered, true)
        } else {
            // Restore original data
            setData(originalData.toList(), true)
        }
    }

    private fun filterList(data: List<T>, query: String): List<T> {
        val lowerCaseQuery = query.toLowerCase()

        val filteredModelList = java.util.ArrayList<T>()
        for (s in data) {
            if (filterPredicate.invoke(s, lowerCaseQuery)) {
                filteredModelList.add(s)
            }
        }
        return filteredModelList
    }

    fun setData(data: List<T>, filtering: Boolean) {

        if (!filtering) {
            originalData = ArrayList(data)
        }

        // Remove all deleted items
        for (i in this.data.indices.reversed()) {
            // Remove all deleted items
            if (getLocation(this.data[i]) < 0) {
                deleteEntity(i)
            }
        }

        // Add and move items
        for (i in data.indices) {
            val entity = data[i]
            val location = getLocation(entity)
            if (location < 0) {
                addEntity(i, entity)
            } else if (location != i) {
                moveEntity(i, location)
            }
        }
    }
}

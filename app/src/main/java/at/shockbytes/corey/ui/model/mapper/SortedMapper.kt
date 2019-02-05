package at.shockbytes.corey.ui.model.mapper

abstract class SortedMapper<In, Out> : Mapper<In, Out>() {

    abstract val mapToSortFunction: Comparator<Out>
    abstract val mapFromSortFunction: Comparator<In>

    override fun mapTo(data: List<In>): List<Out> {
        return super.mapTo(data).sortedWith(mapToSortFunction)
    }

    override fun mapFrom(data: List<Out>): List<In> {
        return super.mapFrom(data).sortedWith(mapFromSortFunction)
    }
}
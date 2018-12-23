package at.shockbytes.corey.ui.fragment.body

import android.graphics.Color
import at.shockbytes.corey.R
import at.shockbytes.corey.body.info.BodyInfo
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.dagger.AppComponent
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_body_view_weight_history.*

/**
 * Author:  Martin Macheiner
 * Date:    05.03.2018.
 */
class WeightHistoryBodyFragmentView : BodySubFragment() {

    override fun bindViewModel() = Unit
    override fun injectToGraph(appComponent: AppComponent?) = Unit
    override fun unbindViewModel() = Unit

    override val layoutId = R.layout.fragment_body_view_weight_history

    override fun setupViews() {

        // Style chart
        fragment_body_card_weight_graph_linechart.setDrawGridBackground(false)
        fragment_body_card_weight_graph_linechart.axisRight.isEnabled = false
        fragment_body_card_weight_graph_linechart.legend.isEnabled = false
        fragment_body_card_weight_graph_linechart.description = null
        fragment_body_card_weight_graph_linechart.isClickable = false
        fragment_body_card_weight_graph_linechart.axisLeft.setDrawAxisLine(false)
        fragment_body_card_weight_graph_linechart.axisLeft.setDrawGridLines(false)
        fragment_body_card_weight_graph_linechart.xAxis.setDrawGridLines(false)
        fragment_body_card_weight_graph_linechart.xAxis.setDrawAxisLine(false)
        fragment_body_card_weight_graph_linechart.axisLeft.textColor = Color.WHITE
        fragment_body_card_weight_graph_linechart.xAxis.textColor = Color.WHITE
    }

    fun setWeightData(bodyInfo: BodyInfo, weightUnit: String) {

        val entries = ArrayList<Entry>()
        val weightPoints = bodyInfo.weightPoints
        val labels = mutableListOf<String>()

        weightPoints.forEachIndexed { idx, p ->
            labels.add(CoreyUtils.formatDate(p.timeStamp, true))
            entries.add(Entry(idx.toFloat(), p.weight.toFloat()))
        }

        val dataSet = LineDataSet(entries, getString(R.string.weight))
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.color = Color.WHITE
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.isHighlightEnabled = false

        fragment_body_card_weight_graph_linechart.xAxis.valueFormatter = IAxisValueFormatter { value, _ -> labels[value.toInt()] }
        fragment_body_card_weight_graph_linechart.axisLeft.valueFormatter = IAxisValueFormatter { value, _ -> "${value.toInt()} $weightUnit" }
        val lineData = LineData(dataSet)
        fragment_body_card_weight_graph_linechart.data = lineData
        fragment_body_card_weight_graph_linechart.invalidate()

        animateCard(fragment_body_card_weight_graph, 0)
    }

    override fun animateView(startDelay: Long) {
    }

}
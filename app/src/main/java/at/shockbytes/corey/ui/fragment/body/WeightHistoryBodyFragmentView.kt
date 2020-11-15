package at.shockbytes.corey.ui.fragment.body

import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.body.weight.WeightHistoryLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_body_view_weight_history.*
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.LimitLine

/**
 * Author:  Martin Macheiner
 * Date:    05.03.2018
 */
class WeightHistoryBodyFragmentView : BodySubFragment() {

    override fun bindViewModel() = Unit
    override fun injectToGraph(appComponent: AppComponent?) = Unit
    override fun unbindViewModel() = Unit

    override val layoutId = R.layout.fragment_body_view_weight_history

    override fun setupViews() {

        // Style chart
        fragment_body_card_weight_graph_linechart.apply {
            setDrawGridBackground(false)
            axisRight.isEnabled = false
            legend.isEnabled = false
            description = null
            isClickable = false
            axisLeft.apply {
                typeface = ResourcesCompat.getFont(context, R.font.montserrat)
                setDrawAxisLine(false)
                setDrawGridLines(false)
                textColor = ContextCompat.getColor(requireContext(), R.color.body_card_weight_history)
            }
            xAxis.apply {
                typeface = ResourcesCompat.getFont(context, R.font.montserrat)
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textColor = ContextCompat.getColor(requireContext(), R.color.body_card_weight_history)
            }
        }
    }

    fun setWeightData(
        weightLines: List<WeightHistoryLine>,
        dreamWeight: Int,
        weightUnit: String
    ) {

        if (weightLines.isEmpty()) {
            return
        }

        val labels = weightLines
            .maxByOrNull { it.points.size }
            ?.points
            ?.map { p ->
                CoreyUtils.formatDate(p.timeStamp, true)
            }
            ?: return

        val dataSets = weightLines
            .filter { it.points.isNotEmpty() }
            .map { weightHistoryLine ->

                val entries = weightHistoryLine.points.mapIndexed { idx, p ->
                    Entry(idx.toFloat(), p.weight.toFloat())
                }

                LineDataSet(entries, getString(weightHistoryLine.name)).apply {
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawCircles(false)
                    setDrawValues(false)
                    isHighlightEnabled = false
                    color = ContextCompat.getColor(requireContext(), weightHistoryLine.lineColor)
                    lineWidth = weightHistoryLine.lineThickness
                }
            }

        // Add dream weight line
        val dreamWeightLine = getDreamWeightLine(
            dreamWeight.toFloat(),
            getString(R.string.dreamweight),
            ContextCompat.getColor(requireContext(), R.color.body_card_weight_history)
        )

        fragment_body_card_weight_graph_linechart.apply {
            xAxis.valueFormatter = IAxisValueFormatter { value, _ -> labels[value.toInt()] }
            axisLeft.valueFormatter = IAxisValueFormatter { value, _ -> "${value.toInt()} $weightUnit" }
            axisLeft.addLimitLine(dreamWeightLine)

            data = LineData(dataSets)
            invalidate()
        }

        animateCard(fragment_body_card_weight_graph, 0)
    }

    private fun getDreamWeightLine(
        dreamWeight: Float,
        title: String,
        @ColorInt dreamWeightLineColor: Int
    ): LimitLine {

        return LimitLine(dreamWeight, title).apply {
            lineWidth = 1f
            lineColor = dreamWeightLineColor
            enableDashedLine(7f, 7f, 0f)
            labelPosition = LimitLabelPosition.LEFT_BOTTOM
            textSize = 10f
            textColor = dreamWeightLineColor
            typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat)
        }
    }
}
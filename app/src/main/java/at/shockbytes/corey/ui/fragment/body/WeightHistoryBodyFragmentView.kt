package at.shockbytes.corey.ui.fragment.body

import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import at.shockbytes.corey.R
import at.shockbytes.corey.data.body.info.BodyInfo
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.dagger.AppComponent
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
            axisLeft.setDrawAxisLine(false)
            axisLeft.setDrawGridLines(false)
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
        }

        context?.let { ctx ->
            fragment_body_card_weight_graph_linechart.axisLeft.textColor = ContextCompat.getColor(ctx, R.color.body_card_weight_history)
            fragment_body_card_weight_graph_linechart.xAxis.textColor = ContextCompat.getColor(ctx, R.color.body_card_weight_history)
        }
    }

    fun setWeightData(bodyInfo: BodyInfo, weightUnit: String) {

        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        bodyInfo.weightPoints.forEachIndexed { idx, p ->
            labels.add(CoreyUtils.formatDate(p.timeStamp, true))
            entries.add(Entry(idx.toFloat(), p.weight.toFloat()))
        }

        val dataSet = LineDataSet(entries, getString(R.string.weight)).apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawCircles(false)
            setDrawValues(false)
            isHighlightEnabled = false
        }

        context?.let { ctx ->
            dataSet.color = ContextCompat.getColor(ctx, R.color.body_card_weight_history)

            // Add dream weight line
            val dreamWeightLine = getDreamWeightLine(
                    bodyInfo.dreamWeight.toFloat(),
                    getString(R.string.dreamweight),
                    ContextCompat.getColor(ctx, R.color.body_card_weight_history)
            )
            fragment_body_card_weight_graph_linechart.axisLeft.addLimitLine(dreamWeightLine)
        }

        fragment_body_card_weight_graph_linechart.apply {
            xAxis.valueFormatter = IAxisValueFormatter { value, _ -> labels[value.toInt()] }
            axisLeft.valueFormatter = IAxisValueFormatter { value, _ -> "${value.toInt()} $weightUnit" }

            data = LineData(dataSet)
            invalidate()
        }

        animateCard(fragment_body_card_weight_graph, 0)
    }

    override fun animateView(startDelay: Long) = Unit

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
        }
    }

    /*
    private fun requestCustomFont() {

        val request = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Montserrat",
                R.array.com_google_android_gms_fonts_certs)

        val callback = object : FontsContract.FontRequestCallback() {

            override fun onTypefaceRetrieved(typeface: Typeface) {
                // Your code to use the font goes here
                ...
            }

            override fun onTypefaceRequestFailed(reason: Int) {
                // Your code to deal with the failure goes here
                ...
            }
        }
        FontsContract.requestFonts(context, request, handler, null, callback)
    }
    */
}
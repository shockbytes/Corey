package at.shockbytes.corey.ui.fragment.body

import android.graphics.Color
import android.support.v7.widget.CardView
import at.shockbytes.corey.R
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.body.info.BodyInfo
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.ui.fragment.BaseFragment
import at.shockbytes.corey.user.CoreyUser
import butterknife.BindView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.util.*

/**
 * @author  Martin Macheiner
 * Date:    05.03.2018.
 */

class WeightHistoryBodyFragmentView(fragment: BaseFragment,
                                  bodyInfo: BodyInfo,
                                  bodyManager: BodyManager,
                                  user: CoreyUser) : BodyFragmentView(fragment, bodyInfo, bodyManager, user) {

    @BindView(R.id.fragment_body_card_weight_graph)
    protected lateinit var cardView: CardView

    @BindView(R.id.fragment_body_card_weight_graph_linechart)
    protected lateinit var lineChartWeight: LineChart

    override val layoutId = R.layout.fragment_body_view_weight_history

    override fun onDesiredWeightChanged(changed: Int) {
        // Not interesting...
    }

    override fun onBodyGoalAdded(g: Goal) {
        // Not interesting...
    }

    override fun onBodyGoalDeleted(g: Goal) {
        // Not interesting...
    }

    override fun onBodyGoalChanged(g: Goal) {
        // Not interesting...
    }

    override fun setupView() {

        // Style chart
        lineChartWeight.setDrawGridBackground(false)
        lineChartWeight.axisRight.isEnabled = false
        lineChartWeight.legend.isEnabled = false
        lineChartWeight.description = null
        lineChartWeight.isClickable = false
        lineChartWeight.axisLeft.setDrawAxisLine(false)
        lineChartWeight.axisLeft.setDrawGridLines(false)
        lineChartWeight.xAxis.setDrawGridLines(false)
        lineChartWeight.xAxis.setDrawAxisLine(false)
        lineChartWeight.axisLeft.textColor = Color.WHITE
        lineChartWeight.xAxis.textColor = Color.WHITE

        // Set data
        val entries = ArrayList<Entry>()
        val weightPoints = bodyInfo.weightPoints
        val labels = mutableListOf<String>()

        weightPoints.forEachIndexed { idx, p ->
            labels.add(CoreyUtils.formatDate(p.timeStamp, true))
            entries.add(Entry(idx.toFloat(), p.weight.toFloat()))
        }

        val dataSet = LineDataSet(entries, fragment.getString(R.string.weight))
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.color = Color.WHITE
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.isHighlightEnabled = false

        lineChartWeight.xAxis.valueFormatter = IAxisValueFormatter { value, _ -> labels[value.toInt()] }
        lineChartWeight.axisLeft.valueFormatter = IAxisValueFormatter { value, _ -> "${value.toInt()} $weightUnit" }
        val lineData = LineData(dataSet)
        lineChartWeight.data = lineData
        lineChartWeight.invalidate()

    }

    override fun animateView(startDelay: Long) {
        animateCard(cardView, startDelay)
    }

}
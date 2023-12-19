package com.elementary.mualijpro.ui.fragments.statistics

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import com.elementary.mualijpro.MualijProApplication.Companion.isPhone
import com.elementary.mualijpro.R
import com.elementary.mualijpro.interfaces.ApiResponseEvent
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.models.local.statistics.MonthsArray
import com.elementary.mualijpro.models.local.statistics.YearArray
import com.elementary.mualijpro.models.posts.statistics.Statistics
import com.elementary.mualijpro.networks.ApiController
import com.elementary.mualijpro.ui.activities.base.BaseFragment
import com.elementary.mualijpro.utils.*
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.util.*


@Suppress("DEPRECATION")
class StatisticsFragment : BaseFragment(), View.OnClickListener, ApiResponseEvent {

    var monthFilterValue = Constants.all
    var yearFilterValue = Constants.all
    var monthsArray = ArrayList<MonthsArray>()
    var yearArray = ArrayList<YearArray>()
    var startingYear = 2019

    private var monthsStringArray = ArrayList<String>()
    private var yearStringArray = ArrayList<String>()

    lateinit var monthsDropdownView: ListPopupWindow
    lateinit var yearDropdownView: ListPopupWindow

    var revenueProgressDataSet = ArrayList<IBarDataSet>()
    var apptProgressDataSet = ArrayList<IBarDataSet>()
    var defaultBarWidth = -1f
    var xAxisValues: List<String>? = null
    var chartSize = 12
    var statisticsObj: Statistics? = null
    var apiResponse = 0
    var isRecursionEnable = true

    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setListeners()
        validateAndGetStatistics()
    }

    private fun initData() {

        xAxisValues = ArrayList(
            listOf(
                resources.getString(R.string.txt_jan),
                resources.getString(R.string.txt_feb),
                resources.getString(R.string.txt_mar),
                resources.getString(R.string.txt_apr),
                resources.getString(R.string.txt_may),
                resources.getString(R.string.txt_jun),
                resources.getString(R.string.txt_jul),
                resources.getString(R.string.txt_aug),
                resources.getString(R.string.txt_sep),
                resources.getString(R.string.txt_oct),
                resources.getString(R.string.txt_nov),
                resources.getString(R.string.txt_dec)
            )
        )

        monthsArray.clear()
        var monthsObjAll = MonthsArray()
        monthsObjAll.monthName = resources.getString(R.string.txt_all)
        monthsObjAll.monthId = Constants.all
        monthsArray.add(monthsObjAll)

        var monthsObj1 = MonthsArray()
        monthsObj1.monthName = resources.getString(R.string.txt_jan)
        monthsObj1.monthId = "1"
        monthsArray.add(monthsObj1)

        var monthsObj2 = MonthsArray()
        monthsObj2.monthName = resources.getString(R.string.txt_feb)
        monthsObj2.monthId = "2"
        monthsArray.add(monthsObj2)

        var monthsObj3 = MonthsArray()
        monthsObj3.monthName = resources.getString(R.string.txt_mar)
        monthsObj3.monthId = "3"
        monthsArray.add(monthsObj3)

        var monthsObj4 = MonthsArray()
        monthsObj4.monthName = resources.getString(R.string.txt_apr)
        monthsObj4.monthId = "4"
        monthsArray.add(monthsObj4)

        var monthsObj5 = MonthsArray()
        monthsObj5.monthName = resources.getString(R.string.txt_may)
        monthsObj5.monthId = "5"
        monthsArray.add(monthsObj5)

        var monthsObj6 = MonthsArray()
        monthsObj6.monthName = resources.getString(R.string.txt_jun)
        monthsObj6.monthId = "6"
        monthsArray.add(monthsObj6)

        var monthsObj7 = MonthsArray()
        monthsObj7.monthName = resources.getString(R.string.txt_jul)
        monthsObj7.monthId = "7"
        monthsArray.add(monthsObj7)

        var monthsObj8 = MonthsArray()
        monthsObj8.monthName = resources.getString(R.string.txt_aug)
        monthsObj8.monthId = "8"
        monthsArray.add(monthsObj8)

        var monthsObj9 = MonthsArray()
        monthsObj9.monthName = resources.getString(R.string.txt_sep)
        monthsObj9.monthId = "9"
        monthsArray.add(monthsObj9)

        var monthsObj10 = MonthsArray()
        monthsObj10.monthName = resources.getString(R.string.txt_oct)
        monthsObj10.monthId = "10"
        monthsArray.add(monthsObj10)

        var monthsObj11 = MonthsArray()
        monthsObj11.monthName = resources.getString(R.string.txt_nov)
        monthsObj11.monthId = "11"
        monthsArray.add(monthsObj11)

        var monthsObj12 = MonthsArray()
        monthsObj12.monthName = resources.getString(R.string.txt_dec)
        monthsObj12.monthId = "12"
        monthsArray.add(monthsObj12)


        monthsStringArray.clear()
        for (i in 0 until monthsArray.size)
            monthsStringArray.add(monthsArray[i].monthName!!)


        monthsDropdownView = ListPopupWindow(activity!!)
        yearDropdownView = ListPopupWindow(activity!!)
        revenueBarChart.setTouchEnabled(false)
        appointmentBarChart.setTouchEnabled(false)
    }

    private fun setRevenueRecursively() {

        if (!isRecursionEnable)
            return

        Thread(Runnable {
            if (apiResponse == 1) {
                activity!!.runOnUiThread {
                    // update UI
                    try {
                        revenueBarChart.visibility = View.VISIBLE
                        appointmentBarChart.visibility = View.VISIBLE
                        setRevenueProgressChart(chartSize)
                        setAppointmentProgressChart(chartSize)
                        setRevenueRecursively()
                        isRecursionEnable = false
                    } catch (e: Exception) {

                    }
                }
            } else if (apiResponse == 0) {
                try {
                    setRevenueRecursively()
                } catch (e: Exception) {

                }
            }

        }).start()

    }

    private fun setListeners() {
        // Do something on dropdown view item
        monthsDropdownView.setOnItemClickListener { _, _, position, _ ->
            monthFilterValue = monthsArray[position].monthId!!
            tvMonth.text = monthsArray[position].monthName!!
            monthsDropdownView.dismiss()
            validateAndGetStatistics()
        }

        // Do something on dropdown view item
        yearDropdownView.setOnItemClickListener { _, _, position, _ ->
            yearFilterValue = yearArray[position].yearId!!
            tvYear.text = yearArray[position].yearName!!
            yearDropdownView.dismiss()
            validateAndGetStatistics()
        }

        rlMonth.setOnClickListener(this)
        rlYear.setOnClickListener(this)
    }

    private fun setData() {
        tvTotalAppointments.text = statisticsObj?.getTotalAppointments().toString()
        tvEarningMonth.text = statisticsObj?.getCurrentMonthAmount().toString()
        tvTotalEarning.text = statisticsObj?.getTotalAmount().toString()
        tvTotalRating.text =
            statisticsObj?.getTotalReviews()
                .toString() + " " + resources.getString(R.string.txt_total).toUpperCase()
        avgRating.rating = statisticsObj?.getAverageRating()?.toFloat()!!

        tvAvgRating.text = statisticsObj?.getAverageRating()

        if (statisticsObj?.getClinicAppointments() == 0 && statisticsObj?.getOnlineAppointments() == 0) {
            rlPieChartType.visibility = View.GONE
            tvNoDataFoundPieType.visibility = View.VISIBLE
        } else {
            rlPieChartType.visibility = View.VISIBLE
            tvNoDataFoundPieType.visibility = View.GONE
            pieChartByType.setDataPoints(
                floatArrayOf(
                    statisticsObj?.getClinicAppointments()?.toFloat()!!,
                    statisticsObj?.getOnlineAppointments()?.toFloat()!!
                )
            )
            pieChartByType.setCenterColor(R.color.white)
            pieChartByType.setSliceColor(
                intArrayOf(
                    R.color.gray_light,
                    R.color.colorPrimary
                )
            )
        }

        if (statisticsObj?.getPastAppointments() == 0 && statisticsObj?.getUpcomingsAppointments() == 0) {
            rlPieChartTime.visibility = View.GONE
            tvNoDataFoundPieTime.visibility = View.VISIBLE
        } else {
            rlPieChartTime.visibility = View.VISIBLE
            tvNoDataFoundPieTime.visibility = View.GONE
            pieChartByTime.setDataPoints(
                floatArrayOf(
                    statisticsObj?.getPastAppointments()?.toFloat()!!,
                    statisticsObj?.getUpcomingsAppointments()?.toFloat()!!
                )
            )
            pieChartByTime.setCenterColor(R.color.white)
            pieChartByTime.setSliceColor(
                intArrayOf(
                    R.color.gray_light,
                    R.color.colorPrimary
                )
            )
        }

        var nextYear = startingYear
        yearArray.clear()
        var yearObjAll = YearArray()
        yearObjAll.yearName = resources.getString(R.string.txt_all)
        yearObjAll.yearId = Constants.all
        yearArray.add(yearObjAll)

        var yearObj1 = YearArray()
        yearObj1.yearName = startingYear.toString()
        yearObj1.yearId = startingYear.toString()
        yearArray.add(yearObj1)

        if (statisticsObj?.getCurrentYear()?.toInt()!! > startingYear) {

            var loopValue = statisticsObj?.getCurrentYear()?.toInt()!! - startingYear
            for (i in 0 until loopValue) {
                var yearObj = YearArray()
                nextYear += 1
                yearObj.yearName = nextYear.toString()
                yearObj.yearId = nextYear.toString()
                yearArray.add(yearObj)
            }
        }

        yearStringArray.clear()
        for (i in 0 until yearArray.size)
            yearStringArray.add(yearArray[i].yearName!!)
    }

    private fun setRevenueProgressChart(size: Int) {

        val revenueProgressEntries = setRevenueProgressEntries(size)
        revenueProgressDataSet = ArrayList()
        val set1: BarDataSet

        set1 =
            BarDataSet(revenueProgressEntries, resources.getString(R.string.txt_revenue_progress))
        set1.color = resources.getColor(R.color.colorPrimary)
        set1.valueTextColor = resources.getColor(R.color.colorPrimary)
        set1.valueTextSize = 10f

        revenueProgressDataSet.add(set1)

        val data = BarData(revenueProgressDataSet)
        if (isPhone) {
            data.barWidth = 0.5f
            data.setValueTextSize(8f)
        }

        revenueBarChart.data = data
        revenueBarChart.axisLeft.axisMinimum = 0f
        revenueBarChart.description.isEnabled = false
        revenueBarChart.axisRight.axisMinimum = 0f
        revenueBarChart.setDrawBarShadow(false)
        revenueBarChart.setDrawValueAboveBar(true)
        revenueBarChart.setPinchZoom(true)
        revenueBarChart.setDrawGridBackground(false)

        val l = revenueBarChart.legend
        l.isWordWrapEnabled = true
        l.textSize = if (isPhone) 8f else 0f
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.form = Legend.LegendForm.CIRCLE

        val xAxis = revenueBarChart.xAxis
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -45f
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        revenueBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)

        val leftAxis = revenueBarChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.typeface = Typeface.DEFAULT
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawGridLines(false)
        revenueBarChart.axisRight.isEnabled = false
        revenueBarChart.setVisibleXRange(1f, 12f)
        revenueBarChart.xAxis.labelCount = 12
        setRevenueBarWidth(data)
        revenueBarChart.invalidate()

    }

    private fun setRevenueBarWidth(barData: BarData) {
        if (revenueProgressDataSet.size > 1) {
            val barSpace = 50.0f
            val groupSpace = 50.0f
            defaultBarWidth = 0.01f
            if (defaultBarWidth >= 0) {
                barData.barWidth = defaultBarWidth
            }
            revenueBarChart.groupBars(0f, groupSpace, barSpace) // perform the "explicit" grouping
            revenueBarChart.invalidate()
        }
    }

    private fun setRevenueProgressEntries(size: Int): List<BarEntry> {
        val reveueProgressEntries = ArrayList<BarEntry>()

        var jan = statisticsObj?.getRevenueProgress()?.getJan().toString()
        var feb = statisticsObj?.getRevenueProgress()?.getFeb().toString()
        var mar = statisticsObj?.getRevenueProgress()?.getMar().toString()
        var apr = statisticsObj?.getRevenueProgress()?.getApr().toString()
        var may = statisticsObj?.getRevenueProgress()?.getMay().toString()
        var jun = statisticsObj?.getRevenueProgress()?.getJun().toString()
        var jul = statisticsObj?.getRevenueProgress()?.getJul().toString()
        var aug = statisticsObj?.getRevenueProgress()?.getAug().toString()
        var sep = statisticsObj?.getRevenueProgress()?.getSep().toString()
        var oct = statisticsObj?.getRevenueProgress()?.getOct().toString()
        var nov = statisticsObj?.getRevenueProgress()?.getNov().toString()
        var dec = statisticsObj?.getRevenueProgress()?.getDec().toString()

        if (jan?.contains(",")!!)
            jan = jan?.replace(",", "")

        if (feb?.contains(",")!!)
            feb = feb?.replace(",", "")

        if (mar?.contains(",")!!)
            mar = mar?.replace(",", "")

        if (apr?.contains(",")!!)
            apr = apr?.replace(",", "")

        if (may?.contains(",")!!)
            may = may?.replace(",", "")

        if (jun?.contains(",")!!)
            jun = jun?.replace(",", "")

        if (jul?.contains(",")!!)
            jul = jul?.replace(",", "")

        if (aug?.contains(",")!!)
            aug = aug?.replace(",", "")

        if (sep?.contains(",")!!)
            sep = sep?.replace(",", "")

        if (oct?.contains(",")!!)
            oct = oct?.replace(",", "")

        if (nov?.contains(",")!!)
            nov = nov?.replace(",", "")

        if (dec?.contains(",")!!)
            dec = dec?.replace(",", "")

        reveueProgressEntries.add(BarEntry(1f, jan.toFloat()))
        reveueProgressEntries.add(BarEntry(2f, feb.toFloat()))
        reveueProgressEntries.add(BarEntry(3f, mar.toFloat()))
        reveueProgressEntries.add(BarEntry(4f, apr.toFloat()))
        reveueProgressEntries.add(BarEntry(5f, may.toFloat()))
        reveueProgressEntries.add(BarEntry(6f, jun.toFloat()))
        reveueProgressEntries.add(BarEntry(7f, jul.toFloat()))
        reveueProgressEntries.add(BarEntry(8f, aug.toFloat()))
        reveueProgressEntries.add(BarEntry(9f, sep.toFloat()))
        reveueProgressEntries.add(BarEntry(10f, oct.toFloat()))
        reveueProgressEntries.add(BarEntry(11f, nov.toFloat()))
        reveueProgressEntries.add(BarEntry(12f, dec.toFloat()))

        return reveueProgressEntries.subList(0, size)
    }

    private fun setAppointmentProgressChart(size: Int) {

        val appntProgressEntries = setAppntProgressEntries(size)
        apptProgressDataSet = ArrayList()
        val set1: BarDataSet

        set1 =
            BarDataSet(
                appntProgressEntries,
                resources.getString(R.string.txt_appointments_progress)
            )
        set1.color = resources.getColor(R.color.colorPrimary)
        set1.valueTextColor = resources.getColor(R.color.colorPrimary)
        set1.valueTextSize = 10f

        apptProgressDataSet.add(set1)

        val data = BarData(apptProgressDataSet)
        if (isPhone) {
            data.barWidth = 0.5f
            data.setValueTextSize(8f)
        }
        appointmentBarChart.data = data
        appointmentBarChart.axisLeft.axisMinimum = 0f
        appointmentBarChart.description.isEnabled = false
        appointmentBarChart.axisRight.axisMinimum = 0f
        appointmentBarChart.setDrawBarShadow(false)
        appointmentBarChart.setDrawValueAboveBar(true)
        appointmentBarChart.setPinchZoom(true)
        appointmentBarChart.setDrawGridBackground(false)

        val l = appointmentBarChart.legend
        l.isWordWrapEnabled = true
        l.textSize = if (isPhone) 8f else 0f

        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.form = Legend.LegendForm.CIRCLE


        val xAxis = appointmentBarChart.xAxis
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -45f
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        appointmentBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)

        val leftAxis = appointmentBarChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.typeface = Typeface.DEFAULT
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawGridLines(false)
        appointmentBarChart.axisRight.isEnabled = false
        appointmentBarChart.setVisibleXRange(1f, 12f)
        appointmentBarChart.xAxis.labelCount = 12
        setAppntBarWidth(data)
        appointmentBarChart.invalidate()

    }

    private fun setAppntBarWidth(barData: BarData) {
        if (apptProgressDataSet.size > 1) {
            val barSpace = 50.0f
            val groupSpace = 50.0f
            defaultBarWidth = 0.01f
            if (defaultBarWidth >= 0) {
                barData.barWidth = defaultBarWidth
            }
            appointmentBarChart.groupBars(
                0f,
                groupSpace,
                barSpace
            ) // perform the "explicit" grouping
            appointmentBarChart.invalidate()
        }
    }

    private fun setAppntProgressEntries(size: Int): List<BarEntry> {
        val appntProgressEntries = ArrayList<BarEntry>()

        var jan = statisticsObj?.getAppointmentProgress()?.getJan().toString()
        var feb = statisticsObj?.getAppointmentProgress()?.getFeb().toString()
        var mar = statisticsObj?.getAppointmentProgress()?.getMar().toString()
        var apr = statisticsObj?.getAppointmentProgress()?.getApr().toString()
        var may = statisticsObj?.getAppointmentProgress()?.getMay().toString()
        var jun = statisticsObj?.getAppointmentProgress()?.getJun().toString()
        var jul = statisticsObj?.getAppointmentProgress()?.getJul().toString()
        var aug = statisticsObj?.getAppointmentProgress()?.getAug().toString()
        var sep = statisticsObj?.getAppointmentProgress()?.getSep().toString()
        var oct = statisticsObj?.getAppointmentProgress()?.getOct().toString()
        var nov = statisticsObj?.getAppointmentProgress()?.getNov().toString()
        var dec = statisticsObj?.getAppointmentProgress()?.getDec().toString()

        if (jan?.contains(",")!!)
            jan = jan?.replace(",", "")

        if (feb?.contains(",")!!)
            feb = feb?.replace(",", "")

        if (mar?.contains(",")!!)
            mar = mar?.replace(",", "")

        if (apr?.contains(",")!!)
            apr = apr?.replace(",", "")

        if (may?.contains(",")!!)
            may = may?.replace(",", "")

        if (jun?.contains(",")!!)
            jun = jun?.replace(",", "")

        if (jul?.contains(",")!!)
            jul = jul?.replace(",", "")

        if (aug?.contains(",")!!)
            aug = aug?.replace(",", "")

        if (sep?.contains(",")!!)
            sep = sep?.replace(",", "")

        if (oct?.contains(",")!!)
            oct = oct?.replace(",", "")

        if (nov?.contains(",")!!)
            nov = nov?.replace(",", "")

        if (dec?.contains(",")!!)
            dec = dec?.replace(",", "")

        appntProgressEntries.add(BarEntry(1f, jan.toFloat()))
        appntProgressEntries.add(BarEntry(2f, feb.toFloat()))
        appntProgressEntries.add(BarEntry(3f, mar.toFloat()))
        appntProgressEntries.add(BarEntry(4f, apr.toFloat()))
        appntProgressEntries.add(BarEntry(5f, may.toFloat()))
        appntProgressEntries.add(BarEntry(6f, jun.toFloat()))
        appntProgressEntries.add(BarEntry(7f, jul.toFloat()))
        appntProgressEntries.add(BarEntry(8f, aug.toFloat()))
        appntProgressEntries.add(BarEntry(9f, sep.toFloat()))
        appntProgressEntries.add(BarEntry(10f, oct.toFloat()))
        appntProgressEntries.add(BarEntry(11f, nov.toFloat()))
        appntProgressEntries.add(BarEntry(12f, dec.toFloat()))

        return appntProgressEntries.subList(0, size)
    }

    private fun validateAndGetStatistics() {

        apiResponse = 0
        isRecursionEnable = true
        setRevenueRecursively()
        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }
        val paramObject = AppUtils.getGenericParams()
        paramObject.addProperty("selected_year", yearFilterValue)
        paramObject.addProperty("selected_month", monthFilterValue)
        Loader.showLoader(activity!!)
        ApiController.getInstance(activity!!, this).getStatistics(paramObject)
    }

    // API Error Response Handling
    override fun onError(requestType: Int, errorMessage: String) {

        try {
            apiResponse = 2
            Loader.hideLoader()
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                errorMessage
            )
        } catch (e: Exception) {
            apiResponse = 2

        }

    }

    // API Success Response Handling
    override fun onSuccess(requestType: Int, webResponse: Any) {

        try {
            Loader.hideLoader()
            var objectData = webResponse as WebResponse<*>
            statisticsObj =
                CommonFunctions.convertDataType(objectData, Statistics::class.java)
            setData()
            apiResponse = 1
        } catch (e: Exception) {
            apiResponse = 2

        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.rlMonth -> {
                monthsDropdownView.setAdapter(
                    ArrayAdapter(
                        activity!!,
                        android.R.layout.simple_spinner_dropdown_item,
                        monthsStringArray
                    )
                )
                monthsDropdownView.width = rlMonth.measuredWidth!!
                monthsDropdownView.isModal = true
                monthsDropdownView.anchorView = rlMonth
                monthsDropdownView.show()
            }

            R.id.rlYear -> {
                yearDropdownView.setAdapter(
                    ArrayAdapter(
                        activity!!,
                        android.R.layout.simple_spinner_dropdown_item,
                        yearStringArray
                    )
                )
                yearDropdownView.width = rlYear.measuredWidth!!
                yearDropdownView.isModal = true
                yearDropdownView.anchorView = rlYear
                yearDropdownView.show()
            }
        }
    }
}
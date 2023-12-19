package com.elementary.mualijpro.ui.fragments.settings

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.elementary.mualijpro.R
import com.elementary.mualijpro.ui.activities.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import com.elementary.mualijpro.dialogs.DialogStartEndTime
import com.elementary.mualijpro.interfaces.ApiResponseEvent
import com.elementary.mualijpro.interfaces.WeeklyScheduleStartEndTime
import com.elementary.mualijpro.models.local.settings.SettingsStartEndTime
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.models.posts.settings.WeeklyDataResponse
import com.elementary.mualijpro.models.posts.settings.WeeklyScheduleResponse
import com.elementary.mualijpro.networks.APIsTag
import com.elementary.mualijpro.networks.ApiController
import com.elementary.mualijpro.utils.*
import com.google.gson.JsonObject
import java.util.ArrayList
import org.json.JSONObject
import org.json.JSONArray
import com.elementary.mualijpro.utils.InputFilterMinMax
import android.text.InputFilter
import android.text.TextWatcher
import com.elementary.mualijpro.interfaces.AlertDialogCallBack
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_settings.btnUpdate
import kotlinx.android.synthetic.main.fragment_settings.llMain
import kotlinx.android.synthetic.main.fragment_settings.llSub
import kotlinx.android.synthetic.main.fragment_settings.svMain


@Suppress("DEPRECATION")
class SettingsFragment : BaseFragment(), WeeklyScheduleStartEndTime, ApiResponseEvent,
    View.OnClickListener,
    CompoundButton.OnCheckedChangeListener, AlertDialogCallBack {

    private var dialogStartEndTime: DialogStartEndTime? = null
    private var startTimeArrayList = ArrayList<SettingsStartEndTime>()
    private var endTimeArrayList = ArrayList<SettingsStartEndTime>()
    private var weeklyScheduleArray = ArrayList<WeeklyScheduleResponse>()
    private val startTimeHoursArray =
        arrayOf("12", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11")
    private val endTimeHoursArray =
        arrayOf("06", "07", "08", "09", "10", "11", "12", "01", "02", "03", "04", "05")
    private val minutesArray =
        arrayOf("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")

    ///    private val startTimeHoursArray = arrayOf("10", "11", "12", "01", "02", "03", "04", "05", "06", "07", "08", "09")

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setChangeListeners()
        setListeners()
        setStartEndTimeArray()
        callGetSchedule()
    }

    private fun initData() {
        etTimePerSection.filters = arrayOf<InputFilter>(InputFilterMinMax("1", "60"))
        etLapPerSection.filters = arrayOf<InputFilter>(InputFilterMinMax("1", "60"))
    }

    private fun setListeners() {


        llMain.setOnClickListener { AppUtils.hideKeyboard(activity!!) }
        svMain.setOnClickListener { AppUtils.hideKeyboard(activity!!) }
        llSub.setOnClickListener { AppUtils.hideKeyboard(activity!!) }

        rlMondayStartTime.setOnClickListener(this)
        rlMondayEndTime.setOnClickListener(this)
        rlTuesdayStartTime.setOnClickListener(this)
        rlTuesdayEndTime.setOnClickListener(this)
        rlWednesdayStartTime.setOnClickListener(this)
        rlWednesdayEndTime.setOnClickListener(this)
        rlThursdayStartTime.setOnClickListener(this)
        rlThursdayEndTime.setOnClickListener(this)
        rlFridayStartTime.setOnClickListener(this)
        rlFridayEndTime.setOnClickListener(this)
        rlSaturdayStartTime.setOnClickListener(this)
        rlSaturdayEndTime.setOnClickListener(this)
        rlSundayStartTime.setOnClickListener(this)
        rlSundayEndTime.setOnClickListener(this)

        toggleBtnMonday.setOnCheckedChangeListener(this)
        toggleBtnTuesday.setOnCheckedChangeListener(this)
        toggleBtnWednesday.setOnCheckedChangeListener(this)
        toggleBtnThursday.setOnCheckedChangeListener(this)
        toggleBtnFriday.setOnCheckedChangeListener(this)
        toggleBtnSaturday.setOnCheckedChangeListener(this)
        toggleBtnSunday.setOnCheckedChangeListener(this)
        btnUpdate.setOnClickListener(this)
        btnSetAllDays.setOnClickListener(this)

    }

    private fun setStartEndTimeArray() {

        //<---------Set Start Time Array------->
        // Set PM Values
        for (i in 0..11) {
            for (s in 0..11) {
                var objectStartTime = SettingsStartEndTime()
                objectStartTime.setTime(startTimeHoursArray[i] + ":" + minutesArray[s] + " PM")
                startTimeArrayList.add(objectStartTime)
            }
        }
        // Set AM Values
        for (i in 0..11) {
            for (s in 0..11) {
                var objectStartTime = SettingsStartEndTime()
                objectStartTime.setTime(startTimeHoursArray[i] + ":" + minutesArray[s] + " AM")
                startTimeArrayList.add(objectStartTime)
            }
        }


        //<---------Set End Time Array------->
        // Set PM Values
        for (i in 0..5) {
            for (s in 0..11) {
                var objectStartTime = SettingsStartEndTime()
                objectStartTime.setTime(endTimeHoursArray[i] + ":" + minutesArray[s] + " PM")
                endTimeArrayList.add(objectStartTime)
            }
        }
        // Set AM Values
        for (i in 0..11) {
            for (s in 0..11) {
                var objectStartTime = SettingsStartEndTime()
                objectStartTime.setTime(startTimeHoursArray[i] + ":" + minutesArray[s] + " AM")
                endTimeArrayList.add(objectStartTime)
            }
        }
        // Set PM Values
        for (i in 6..11) {
            for (s in 0..11) {
                var objectStartTime = SettingsStartEndTime()
                objectStartTime.setTime(endTimeHoursArray[i] + ":" + minutesArray[s] + " PM")
                endTimeArrayList.add(objectStartTime)
            }
        }


    }

    private fun setWeeklyData() {
        try {
            showHideBtnSetAllDaysData()
            if (weeklyScheduleArray != null && weeklyScheduleArray.size >= 7) {

                etTimePerSection.setText(weeklyScheduleArray[0].getTimePerSection())
                etLapPerSection.setText(weeklyScheduleArray[0].getLapPerSection())

                setLayout(
                    0,
                    toggleBtnMonday,
                    ivMondayArrowStart,
                    ivMondayArrowEnd,
                    tvMondayStartTime,
                    tvMondayEndTime,
                    rlMondayStartTime,
                    rlMondayEndTime,
                    weeklyScheduleArray[0].getIsOn()
                )

                setLayout(
                    1,
                    toggleBtnTuesday,
                    ivTuesdayArrowStart,
                    ivTuesdayArrowEnd,
                    tvTuesdayStartTime,
                    tvTuesdayEndTime,
                    rlTuesdayStartTime,
                    rlTuesdayEndTime,
                    weeklyScheduleArray[1].getIsOn()
                )

                setLayout(
                    2,
                    toggleBtnWednesday,
                    ivWednesdayArrowStart,
                    ivWednesdayArrowEnd,
                    tvWednesdayStartTime,
                    tvWednesdayEndTime,
                    rlWednesdayStartTime,
                    rlWednesdayEndTime,
                    weeklyScheduleArray[2].getIsOn()
                )

                setLayout(
                    3,
                    toggleBtnThursday,
                    ivThursdayArrowStart,
                    ivThursdayArrowEnd,
                    tvThursdayStartTime,
                    tvThursdayEndTime,
                    rlThursdayStartTime,
                    rlThursdayEndTime,
                    weeklyScheduleArray[3].getIsOn()
                )

                setLayout(
                    4,
                    toggleBtnFriday,
                    ivFridayArrowStart,
                    ivFridayArrowEnd,
                    tvFridayStartTime,
                    tvFridayEndTime,
                    rlFridayStartTime,
                    rlFridayEndTime,
                    weeklyScheduleArray[4].getIsOn()
                )

                setLayout(
                    5,
                    toggleBtnSaturday,
                    ivSaturdayArrowStart,
                    ivSaturdayArrowEnd,
                    tvSaturdayStartTime,
                    tvSaturdayEndTime,
                    rlSaturdayStartTime,
                    rlSaturdayEndTime,
                    weeklyScheduleArray[5].getIsOn()
                )

                setLayout(
                    6,
                    toggleBtnSunday,
                    ivSundayArrowStart,
                    ivSundayArrowEnd,
                    tvSundayStartTime,
                    tvSundayEndTime,
                    rlSundayStartTime,
                    rlSundayEndTime,
                    weeklyScheduleArray[6].getIsOn()
                )
            }
        } catch (e: Exception) {

            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_something_went_wrong)
            )
        }

    }

    private fun setLayout(
        indexValue: Int,
        toggleBtn: ToggleButton,
        ivStart: ImageView,
        ivEnd: ImageView,
        tvStart: TextView,
        tvEnd: TextView,
        viewStart: RelativeLayout,
        viewEnd: RelativeLayout,
        checked: Boolean
    ) {
        toggleBtn.isChecked = checked
        viewStart.isClickable = checked
        viewEnd.isClickable = checked
        if (checked) {
            weeklyScheduleArray[indexValue].setIsOn(1)
            if (weeklyScheduleArray[indexValue].getStartTime() != "")
                tvStart.text = weeklyScheduleArray[indexValue].getStartTime()
            if (weeklyScheduleArray[indexValue].getEndTime() != "")
                tvEnd.text = weeklyScheduleArray[indexValue].getEndTime()
            viewStart.background = resources.getDrawable(R.drawable.bg_white_with_gray_border)
            viewEnd.background = resources.getDrawable(R.drawable.bg_white_with_gray_border)
            ivStart.background = resources.getDrawable(R.drawable.ic_down_arrow)
            ivEnd.background = resources.getDrawable(R.drawable.ic_down_arrow)
        } else {
            weeklyScheduleArray[indexValue].setIsOn(0)
            weeklyScheduleArray[indexValue].setStartTime("")
            weeklyScheduleArray[indexValue].setEndTime("")
            viewStart.background = resources.getDrawable(R.drawable.bg_gray_without_border)
            viewEnd.background = resources.getDrawable(R.drawable.bg_gray_without_border)
            ivStart.background = resources.getDrawable(R.drawable.ic_down_arrow_gry)
            ivEnd.background = resources.getDrawable(R.drawable.ic_down_arrow_gry)
            tvStart.text = resources.getString(R.string.txt_setting_start_time)
            tvEnd.text = resources.getString(R.string.txt_setting_end_time)
        }
        showHideBtnSetAllDaysData()
    }

    private fun showStartEndTimeDialog(
        TimeArrayList: ArrayList<SettingsStartEndTime>,
        startTime: Boolean,
        indexValue: Int,
        view: TextView,
        rlTime: RelativeLayout
    ) {
        dialogStartEndTime =
            DialogStartEndTime(startTime, indexValue, view, rlTime, TimeArrayList, this)
        fragmentManager?.let { dialogStartEndTime!!.show(it, "") }
    }

    private fun callGetSchedule() {
        Loader.showLoader(activity!!)
        ApiController.getInstance(activity!!, this).getWeeklySchedule()
    }


    private fun callWeeklyScheduleUpdate(jsonObject: JsonObject) {
        Loader.showLoader(activity!!)
        ApiController.getInstance(activity!!, this).weeklyScheduleUpdate(jsonObject)
    }


    override fun onCheckedChanged(view: CompoundButton?, isChecked: Boolean) {
        when (view?.id) {
            R.id.toggleBtnMonday ->
                setLayout(
                    0,
                    toggleBtnMonday,
                    ivMondayArrowStart,
                    ivMondayArrowEnd,
                    tvMondayStartTime,
                    tvMondayEndTime,
                    rlMondayStartTime,
                    rlMondayEndTime,
                    isChecked
                )

            R.id.toggleBtnTuesday ->
                setLayout(
                    1,
                    toggleBtnTuesday,
                    ivTuesdayArrowStart,
                    ivTuesdayArrowEnd,
                    tvTuesdayStartTime,
                    tvTuesdayEndTime,
                    rlTuesdayStartTime,
                    rlTuesdayEndTime,
                    isChecked
                )

            R.id.toggleBtnWednesday ->
                setLayout(
                    2,
                    toggleBtnWednesday,
                    ivWednesdayArrowStart,
                    ivWednesdayArrowEnd,
                    tvWednesdayStartTime,
                    tvWednesdayEndTime,
                    rlWednesdayStartTime,
                    rlWednesdayEndTime,
                    isChecked
                )

            R.id.toggleBtnThursday ->
                setLayout(
                    3,
                    toggleBtnThursday,
                    ivThursdayArrowStart,
                    ivThursdayArrowEnd,
                    tvThursdayStartTime,
                    tvThursdayEndTime,
                    rlThursdayStartTime,
                    rlThursdayEndTime,
                    isChecked
                )

            R.id.toggleBtnFriday ->
                setLayout(
                    4,
                    toggleBtnFriday,
                    ivFridayArrowStart,
                    ivFridayArrowEnd,
                    tvFridayStartTime,
                    tvFridayEndTime,
                    rlFridayStartTime,
                    rlFridayEndTime,
                    isChecked
                )

            R.id.toggleBtnSaturday ->
                setLayout(
                    5,
                    toggleBtnSaturday,
                    ivSaturdayArrowStart,
                    ivSaturdayArrowEnd,
                    tvSaturdayStartTime,
                    tvSaturdayEndTime,
                    rlSaturdayStartTime,
                    rlSaturdayEndTime,
                    isChecked
                )

            R.id.toggleBtnSunday ->
                setLayout(
                    6,
                    toggleBtnSunday,
                    ivSundayArrowStart,
                    ivSundayArrowEnd,
                    tvSundayStartTime,
                    tvSundayEndTime,
                    rlSundayStartTime,
                    rlSundayEndTime,
                    isChecked
                )

        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.rlMondayStartTime ->
                showStartEndTimeDialog(
                    startTimeArrayList,
                    true,
                    0,
                    tvMondayStartTime,
                    rlMondayStartTime
                )

            R.id.rlMondayEndTime ->
                showStartEndTimeDialog(endTimeArrayList, false, 0, tvMondayEndTime, rlMondayEndTime)

            R.id.rlTuesdayStartTime ->
                showStartEndTimeDialog(
                    startTimeArrayList,
                    true,
                    1,
                    tvTuesdayStartTime,
                    rlTuesdayStartTime
                )

            R.id.rlTuesdayEndTime ->
                showStartEndTimeDialog(
                    endTimeArrayList,
                    false,
                    1,
                    tvTuesdayEndTime,
                    rlTuesdayEndTime
                )

            R.id.rlWednesdayStartTime ->
                showStartEndTimeDialog(
                    startTimeArrayList,
                    true,
                    2,
                    tvWednesdayStartTime,
                    rlWednesdayStartTime
                )

            R.id.rlWednesdayEndTime ->
                showStartEndTimeDialog(
                    endTimeArrayList,
                    false,
                    2,
                    tvWednesdayEndTime,
                    rlWednesdayEndTime
                )

            R.id.rlThursdayStartTime ->
                showStartEndTimeDialog(
                    startTimeArrayList,
                    true,
                    3,
                    tvThursdayStartTime,
                    rlThursdayStartTime
                )

            R.id.rlThursdayEndTime ->
                showStartEndTimeDialog(
                    endTimeArrayList,
                    false,
                    3,
                    tvThursdayEndTime,
                    rlThursdayEndTime
                )

            R.id.rlFridayStartTime ->
                showStartEndTimeDialog(
                    startTimeArrayList,
                    true,
                    4,
                    tvFridayStartTime,
                    rlFridayStartTime
                )

            R.id.rlFridayEndTime ->
                showStartEndTimeDialog(endTimeArrayList, false, 4, tvFridayEndTime, rlFridayEndTime)

            R.id.rlSaturdayStartTime ->
                showStartEndTimeDialog(
                    startTimeArrayList,
                    true,
                    5,
                    tvSaturdayStartTime,
                    rlSaturdayStartTime
                )

            R.id.rlSaturdayEndTime ->
                showStartEndTimeDialog(
                    endTimeArrayList,
                    false,
                    5,
                    tvSaturdayEndTime,
                    rlSaturdayEndTime
                )

            R.id.rlSundayStartTime ->
                showStartEndTimeDialog(
                    startTimeArrayList,
                    true,
                    6,
                    tvSundayStartTime,
                    rlSundayStartTime
                )

            R.id.rlSundayEndTime ->
                showStartEndTimeDialog(endTimeArrayList, false, 6, tvSundayEndTime, rlSundayEndTime)

            R.id.btnUpdate ->
                validateRequest()

            R.id.btnSetAllDays ->
                AppUtils.setAllDays(activity!!, this)
        }
    }

    private fun setChangeListeners() {

        etTimePerSection.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                validateEt(etTimePerSection)
            }
        })

        etLapPerSection.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                validateEt(etLapPerSection)
            }
        })

    }

    private fun validateEt(et: EditText): Boolean {
        return if (AppUtils.isSet(et.text.toString())) {
            AppUtils.setBackground(et, R.drawable.bg_white_with_gray_border)
            true
        } else {
            AppUtils.setBackground(et, R.drawable.bg_white_with_red_border)
            false
        }
    }

    private fun validateStartEndTime(
        toggleBtn: ToggleButton, startTime: TextView, endTime: TextView,
        rlStart: RelativeLayout, rlEnd: RelativeLayout
    ): Boolean {
        var resultValue = true
        if (toggleBtn.isChecked) {
            if (startTime.text == resources.getString(R.string.txt_setting_start_time)) {
                AppUtils.setBackground(rlStart, R.drawable.bg_white_with_red_border)
                resultValue = false
            }

            if (endTime.text == resources.getString(R.string.txt_setting_end_time)) {
                AppUtils.setBackground(rlEnd, R.drawable.bg_white_with_red_border)
                resultValue = false
            }
        }
        return resultValue
    }


    private fun validateRequest() {

        var checkToggleButton = false
        for (i in 0 until weeklyScheduleArray.size)
            if (weeklyScheduleArray[i].getIsOn())
                checkToggleButton = true


        if (!checkToggleButton) {
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_success_information),
                resources.getString(R.string.txt_setting_day_error)
            )
            return
        }


        var resultValue = true

        if (!validateStartEndTime(
                toggleBtnMonday,
                tvMondayStartTime,
                tvMondayEndTime,
                rlMondayStartTime,
                rlMondayEndTime
            )
        )
            resultValue = false


        if (!validateStartEndTime(
                toggleBtnTuesday,
                tvTuesdayStartTime,
                tvTuesdayEndTime,
                rlTuesdayStartTime,
                rlTuesdayEndTime
            )
        )
            resultValue = false


        if (!validateStartEndTime(
                toggleBtnWednesday,
                tvWednesdayStartTime,
                tvWednesdayEndTime,
                rlWednesdayStartTime,
                rlWednesdayEndTime
            )
        )
            resultValue = false


        if (!validateStartEndTime(
                toggleBtnThursday,
                tvThursdayStartTime,
                tvThursdayEndTime,
                rlThursdayStartTime,
                rlThursdayEndTime
            )
        )
            resultValue = false


        if (!validateStartEndTime(
                toggleBtnFriday,
                tvFridayStartTime,
                tvFridayEndTime,
                rlFridayStartTime,
                rlFridayEndTime
            )
        )
            resultValue = false


        if (!validateStartEndTime(
                toggleBtnSaturday,
                tvSaturdayStartTime,
                tvSaturdayEndTime,
                rlSaturdayStartTime,
                rlSaturdayEndTime
            )
        )
            resultValue = false


        if (!validateStartEndTime(
                toggleBtnSunday,
                tvSundayStartTime,
                tvSundayEndTime,
                rlSundayStartTime,
                rlSundayEndTime
            )
        )
            resultValue = false


        if (!resultValue)
            return


        if (!validateEt(etTimePerSection))
            return

        if (!validateEt(etLapPerSection))
            return


        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }

        var servicesJsonArray = JSONArray()
        try {
            if (weeklyScheduleArray.size != 0) {
                for (serviceModel in weeklyScheduleArray) {

                    val servicesObject = JSONObject()
                    servicesObject.put("day", serviceModel.getDay())
                    servicesObject.put("start_time", serviceModel.getStartTime())
                    servicesObject.put("end_time", serviceModel.getEndTime())
                    servicesObject.put("is_on", serviceModel.getIsOn())
                    servicesJsonArray.put(servicesObject)

                }
            }
        } catch (e: Exception) {

            e.message
        }

        val paramObject = AppUtils.getGenericParams()
        paramObject.addProperty("schedule_weekday", servicesJsonArray.toString())
        paramObject.addProperty("schedule_time_slot", etTimePerSection.text.toString())
        paramObject.addProperty("schedule_time_gap", etLapPerSection.text.toString())
        callWeeklyScheduleUpdate(paramObject)
    }

    // API Error Response Handling
    override fun onError(requestType: Int, errorMessage: String) {
        try {
            Loader.hideLoader()
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                errorMessage
            )
        } catch (e: Exception) {

        }

    }

    // API Success Response Handling
    override fun onSuccess(requestType: Int, webResponse: Any) {
        try {
            Loader.hideLoader()
            var objectData = webResponse as WebResponse<*>
            if (requestType == APIsTag.weeklyScheduleUpdate)
                CustomAlert.showDropDownNotificationSuccess(
                    activity!!,
                    resources.getString(R.string.txt_alert_success_information),
                    objectData?.message!!
                )
            else {
                var weeklySchedule: WeeklyDataResponse? =
                    CommonFunctions.convertDataType(objectData, WeeklyDataResponse::class.java)
                weeklyScheduleArray = weeklySchedule?.getWeeklyScheduleArray()!!
                setWeeklyData()
            }
        } catch (e: Exception) {

        }
    }

    override fun onItemClick(
        startTime: Boolean,
        indexValue: Int,
        textView: TextView,
        rlTime: RelativeLayout,
        time: String?
    ) {

        if (AppUtils.isKeyboardOpen(activity!!))
            AppUtils.hideKeyboardForOverlay(activity!!)

        dialogStartEndTime?.dismiss()
        textView.text = time
        rlTime.setBackgroundResource(R.drawable.bg_white_with_gray_border)
        if (startTime)
            weeklyScheduleArray[indexValue].setStartTime(time)
        else
            weeklyScheduleArray[indexValue].setEndTime(time)
        showHideBtnSetAllDaysData()
    }


    private fun showHideBtnSetAllDaysData() {
        var setValuesCount = 0
        if (weeklyScheduleArray != null) {
            for (i in 0 until weeklyScheduleArray.size)
                if (weeklyScheduleArray[i].getIsOn() && weeklyScheduleArray[i].getStartTime() != "" && weeklyScheduleArray[i].getEndTime() != "")
                    setValuesCount++

            if (setValuesCount in 1..6)
                btnSetAllDays.visibility = View.VISIBLE
            else
                btnSetAllDays.visibility = View.GONE
        }
    }

    private fun setAllDaysAsFirstActiveDay() {
        var startTime = ""
        var endTime = ""
        for (i in 0 until weeklyScheduleArray.size)
            if (weeklyScheduleArray[i].getIsOn() && weeklyScheduleArray[i].getStartTime() != "" && weeklyScheduleArray[i].getEndTime() != "") {
                startTime = weeklyScheduleArray[i].getStartTime()!!
                endTime = weeklyScheduleArray[i].getEndTime()!!
                break
            }

        for (i in 0 until weeklyScheduleArray.size) {
            weeklyScheduleArray[i].setIsOn(1)
            if (weeklyScheduleArray[i].getStartTime().equals("", true)
                && weeklyScheduleArray[i].getEndTime().equals("", true)
            ) {
                weeklyScheduleArray[i].setStartTime(startTime)
                weeklyScheduleArray[i].setEndTime(endTime)
            }
        }
        setWeeklyData()
    }

    override fun onDialogPositiveClick() {
        setAllDaysAsFirstActiveDay()
    }

}
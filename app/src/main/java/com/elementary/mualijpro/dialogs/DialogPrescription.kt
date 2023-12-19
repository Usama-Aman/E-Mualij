package com.elementary.mualijpro.dialogs

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.elementary.mualijpro.MualijProApplication.Companion.isPhone
import com.elementary.mualijpro.R
import com.elementary.mualijpro.adapters.PrescriptionDialogAdapter
import com.elementary.mualijpro.interfaces.DashboardDialogsCallBack
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientDataResponse
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientPrescriptionArray
import com.elementary.mualijpro.models.posts.dashboard.prescriptiondialogarrays.MedicineDay
import com.elementary.mualijpro.models.posts.dashboard.prescriptiondialogarrays.MedicineDose
import com.elementary.mualijpro.models.posts.dashboard.prescriptiondialogarrays.MedicineFrequency
import com.elementary.mualijpro.models.posts.dashboard.prescriptiondialogarrays.MedicineType
import com.elementary.mualijpro.utils.AppUtils
import com.elementary.mualijpro.utils.Constants
import com.elementary.mualijpro.utils.CustomDialogAlert
import com.elementary.mualijpro.utils.DownloadPrescriptionPDF
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


@Suppress("DEPRECATION")
class DialogPrescription(
    private var patientDataObj: PatientDataResponse,
    private var updatePrescription: DashboardDialogsCallBack,
    medTypeArray: ArrayList<MedicineType>,
    medFrequencyArray: ArrayList<MedicineFrequency>,
    medDoseArray: ArrayList<MedicineDose>,
    medDayArray: ArrayList<MedicineDay>
) : DialogFragment() {

    private var prescriptionArray = ArrayList<PatientPrescriptionArray>()
    private var medTypeArray = ArrayList<MedicineType>()
    private var medFrequencyArray = ArrayList<MedicineFrequency>()
    private var medDoseArray = ArrayList<MedicineDose>()
    private var medDayArray = ArrayList<MedicineDay>()
    private var rlNotification: RelativeLayout? = null
    var progressBar: ProgressBar? = null
    var btnSave: Button? = null
    var btnAdd: Button? = null
    var lvPrescription: ListView? = null
    var ivCross: ImageView? = null
    var rlMain: RelativeLayout? = null
    private var mContext: FragmentActivity? = null
    var scrollView: ScrollView? = null
    var prescriptionAdapter: PrescriptionDialogAdapter? = null
    var ivShare: ImageView? = null
    var ivPrint: ImageView? = null


    var rlBody: RelativeLayout? = null
    var rlBg: RelativeLayout? = null
    var rlHeader: RelativeLayout? = null

    init {
        this.medTypeArray = medTypeArray
        this.medFrequencyArray = medFrequencyArray
        this.medDoseArray = medDoseArray
        this.medDayArray = medDayArray
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation

        val decorView = dialog!!.window!!.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        decorView.systemUiVisibility = uiOptions

        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {

            if (!isPhone) {
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                dialog.window!!.setLayout(width, height)
                DialogFactory.dialogSettings(mContext!!, dialog, rlMain, 1f, 0.9f)
            }

            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        super.onCreateView(inflater, parent, state)
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_prescription, parent, false)
        viewInitialize(view)
        setListeners()
        setData()
        return view
    }

    private fun viewInitialize(v: View) {

        var pref = mContext?.getSharedPreferences("mualijApplicationPro", Context.MODE_PRIVATE)
        var gson = Gson()
        var json = pref?.getString("prescriptionArray", "")
        var type = object : TypeToken<ArrayList<PatientPrescriptionArray>>() {}.type
        prescriptionArray = gson.fromJson<ArrayList<PatientPrescriptionArray>>(json, type)

        if (prescriptionArray.size == 0)
            prescriptionArray.add(PatientPrescriptionArray())

        ivShare = v.findViewById(R.id.ivShare)
        ivPrint = v.findViewById(R.id.ivPrint)
        rlNotification = v.findViewById(R.id.rlNotification)
        progressBar = v.findViewById(R.id.progressBar)
//        scrollView = v.findViewById(R.id.scrollView)
        btnSave = v.findViewById(R.id.btnSave)
        btnAdd = v.findViewById(R.id.btnAdd)
        ivCross = v.findViewById(R.id.ivCross)
        rlMain = v.findViewById(R.id.rlMain)
        lvPrescription = v.findViewById(R.id.lvPrescription)

        rlBody = v.findViewById(R.id.rlBody)
        rlBg = v.findViewById(R.id.rlBg)
        rlHeader = v.findViewById(R.id.rlHeader)
    }

    private fun setListeners() {

        rlBody?.setOnClickListener {
            AppUtils.hideKeyboard(activity!!)
        }


        rlBg?.setOnClickListener {
            AppUtils.hideKeyboard(activity!!)
        }

        rlHeader?.setOnClickListener {
            AppUtils.hideKeyboard(activity!!)
        }


        ivShare?.setOnClickListener {
            checkPermissionForDownloadPDF()
        }

        ivPrint?.setOnClickListener {
            checkPermissionForDownloadPDF()
        }

        btnSave?.setOnClickListener {
            var isAllSet = true
            for (i in 0 until prescriptionArray.size) {
                if (prescriptionArray[i].getMedicine()?.isEmpty()!!
                    || prescriptionArray[i].getMedType()?.isEmpty()!!
                    || prescriptionArray[i].getMedFrequency()?.isEmpty()!!
                    || prescriptionArray[i].getMedDose()?.isEmpty()!!
                    || prescriptionArray[i].getMedDay()?.isEmpty()!!
                ) {
                    isAllSet = false
                    prescriptionArray[i].setIsAllValueSet(false)
                }
            }
            prescriptionAdapter?.notifyDataSetChanged()

            if (isAllSet) {
                this.dismiss()
                updatePrescription.onCallBack(
                    Constants.dashboardUpdatePrescription,
                    "",
                    prescriptionArray
                )
            }

        }
        btnAdd?.setOnClickListener {
            prescriptionArray.add(PatientPrescriptionArray())
            prescriptionAdapter?.notifyDataSetChanged()
            AppUtils.setListViewHeightBasedOnChildren(lvPrescription!!)
        }

        ivCross?.setOnClickListener {
            this.dismiss()
        }
    }

    private fun setData() {
        var medTypeStringArray = ArrayList<String>()
        var medFrequencyStringArray = ArrayList<String>()
        var medDoseStringArray = ArrayList<String>()
        var medDayStringArray = ArrayList<String>()

        for (i in 0 until medTypeArray.size)
            medTypeStringArray.add(medTypeArray[i].getValue()!!)

        for (i in 0 until medFrequencyArray.size)
            medFrequencyStringArray.add(medFrequencyArray[i].getValue()!!)

        for (i in 0 until medDoseArray.size)
            medDoseStringArray.add(medDoseArray[i].getValue()!!)

        for (i in 0 until medDayArray.size)
            medDayStringArray.add(medDayArray[i].getValue()!!)

        prescriptionAdapter = PrescriptionDialogAdapter(
            activity!!,
            this,
            mContext!!,
            prescriptionArray,
            medTypeStringArray,
            medFrequencyStringArray,
            medDoseStringArray,
            medDayStringArray
        )
        lvPrescription?.adapter = prescriptionAdapter
        AppUtils.setListViewHeightBasedOnChildren(lvPrescription!!)
    }

    fun onListViewItemClick(type: String, parentPosition: Int, childPosition: Int, value: String) {
        try {
            if (AppUtils.isKeyboardOpen(activity!!))
                AppUtils.hideKeyboardForOverlay(activity!!)
            when (type) {
                "type" -> {
                    prescriptionArray[parentPosition].setMedType(value)
                    prescriptionArray[parentPosition].setMedTypeId(medTypeArray[childPosition].getId()!!)
                }
                "frequency" -> {
                    prescriptionArray[parentPosition].setMedFrequency(value)
                    prescriptionArray[parentPosition].setMedFrequencyId(medFrequencyArray[childPosition].getId()!!)
                }
                "dose" -> {
                    prescriptionArray[parentPosition].setMedDose(value)
                    prescriptionArray[parentPosition].setMedDoseId(medDoseArray[childPosition].getId()!!)
                }
                "day" -> {
                    prescriptionArray[parentPosition].setMedDay(value)
                    prescriptionArray[parentPosition].setMedDayID(medDayArray[childPosition].getId()!!)
                }
            }

            if (prescriptionArray[parentPosition].getMedicine()?.isEmpty()!!
                || prescriptionArray[parentPosition].getMedType()?.isEmpty()!!
                || prescriptionArray[parentPosition].getMedFrequency()?.isEmpty()!!
                || prescriptionArray[parentPosition].getMedDose()?.isEmpty()!!
                || prescriptionArray[parentPosition].getMedDay()?.isEmpty()!!
            )
                prescriptionArray[parentPosition].setIsAllValueSet(false)
            else
                prescriptionArray[parentPosition].setIsAllValueSet(true)

            prescriptionAdapter?.notifyDataSetChanged()
        } catch (e: Exception) {

        }
    }

    fun onListViewEditTextChange(parentPosition: Int, value: String) {
        try {
            prescriptionArray[parentPosition].setMedicine(value)
//            prescriptionAdapter?.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onListViewItemRemove(parentPosition: Int) {
        try {
            if (prescriptionArray.size > 1) {
                prescriptionArray.removeAt(parentPosition)
                prescriptionAdapter?.notifyDataSetChanged()
                AppUtils.setListViewHeightBasedOnChildren(lvPrescription!!)
            } else
                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.txt_prescription_delete_error)
                )
        } catch (e: Exception) {

        }
    }

    private fun checkPermissionForDownloadPDF() {
        takeRunTimePermission()
    }

    private fun takeRunTimePermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    if (patientDataObj?.getPrescriptionPdfUrl() == "")
                        CustomDialogAlert.showDropDownNotificationError(
                            activity!!,
                            rlNotification!!,
                            resources.getColor(R.color.alert_dialog_error),
                            resources.getString(R.string.txt_alert_information),
                            resources.getString(R.string.txt_prescription_pdf_error)
                        )
                    else
                        DownloadPrescriptionPDF(activity!!).execute(patientDataObj?.getPrescriptionPdfUrl())
                else
                    AppUtils.showPermissionAlertDialog(activity!!)
                return

            }
        }
    }

}
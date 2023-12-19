package com.elementary.mualijpro.adapters

import android.app.Activity
import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.elementary.mualijpro.R
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientPrescriptionArray

import android.widget.EditText
import java.util.*
import android.text.Editable
import com.elementary.mualijpro.MualijProApplication
import com.elementary.mualijpro.MualijProApplication.Companion.isPhone
import com.elementary.mualijpro.dialogs.DialogPrescription
import com.elementary.mualijpro.utils.AppUtils

class PrescriptionDialogAdapter(
    private var activity: Activity,
    var dialogPrescription: DialogPrescription,
    var context: Context,
    data: ArrayList<PatientPrescriptionArray>,
    private var medTypeStringArray: ArrayList<String>,
    private var medFrequencyStringArray: ArrayList<String>,
    private var medDoseStringArray: ArrayList<String>,
    private var medDayStringArray: ArrayList<String>
) : BaseAdapter() {

    private var prescriptionArray = ArrayList<PatientPrescriptionArray>()
    private var mInflater: LayoutInflater

    init {
        this.prescriptionArray = data
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return prescriptionArray.size
    }

    override fun getItem(position: Int): Any {
        return prescriptionArray[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view

        val holder: ViewHolder
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_prescription_dialog, null)

            holder = ViewHolder()

            holder.rlType = convertView!!.findViewById<View>(R.id.rlType) as RelativeLayout
            holder.rlFrequency =
                convertView!!.findViewById<View>(R.id.rlFrequency) as RelativeLayout
            holder.rlDose = convertView!!.findViewById<View>(R.id.rlDose) as RelativeLayout
            holder.rlDay = convertView!!.findViewById<View>(R.id.rlDay) as RelativeLayout
            holder.rlDelete = convertView!!.findViewById<View>(R.id.rlDelete) as RelativeLayout

            holder.llDropDownParent =
                convertView!!.findViewById<View>(R.id.llDropDownParent) as LinearLayout
            holder.etMedicine = convertView!!.findViewById<View>(R.id.etMedicine) as EditText
            holder.tvType = convertView!!.findViewById<View>(R.id.tvType) as TextView
            holder.tvFrequency = convertView!!.findViewById<View>(R.id.tvFrequency) as TextView
            holder.tvDose = convertView!!.findViewById<View>(R.id.tvDose) as TextView
            holder.tvDay = convertView!!.findViewById<View>(R.id.tvDay) as TextView

            holder.medTypeDropdownView = ListPopupWindow(MualijProApplication.context!!)
            holder.medFrequencyDropdownView = ListPopupWindow(MualijProApplication.context!!)
            holder.medDoseDropdownView = ListPopupWindow(MualijProApplication.context!!)
            holder.medDayDropdownView = ListPopupWindow(MualijProApplication.context!!)

            convertView.tag = holder

        } else
            holder = convertView.tag as ViewHolder


        holder.etMedicine?.tag = position
        holder.etMedicine?.id = position
        holder.rlType?.tag = position
        holder.rlType?.id = position
        holder.rlFrequency?.tag = position
        holder.rlFrequency?.id = position
        holder.rlDose?.tag = position
        holder.rlDose?.id = position
        holder.rlDay?.tag = position
        holder.rlDay?.id = position
        holder.rlDelete?.tag = position
        holder.rlDelete?.id = position
        holder.tvType?.tag = position
        holder.tvType?.id = position
        holder.tvFrequency?.tag = position
        holder.tvFrequency?.id = position
        holder.tvDose?.tag = position
        holder.tvDose?.id = position
        holder.tvDay?.tag = position
        holder.tvDay?.id = position
        holder.llDropDownParent?.tag = position
        holder.llDropDownParent?.id = position

        if (!isPhone)
            if (prescriptionArray[position].getIsAllValueSet()!!)
                holder.llDropDownParent?.setBackgroundResource(R.drawable.bg_white_with_gray_border)
            else
                holder.llDropDownParent?.setBackgroundResource(R.drawable.bg_white_with_red_border)
        else {

            if (prescriptionArray[position].getIsAllValueSet()!!)
            {
                holder.etMedicine?.setBackgroundResource(R.drawable.bg_white_with_gray_border)
                holder.rlType?.setBackgroundResource(R.drawable.bg_white_with_gray_border)
                holder.rlFrequency?.setBackgroundResource(R.drawable.bg_white_with_gray_border)
                holder.rlDose?.setBackgroundResource(R.drawable.bg_white_with_gray_border)
                holder.rlDay?.setBackgroundResource(R.drawable.bg_white_with_gray_border)

            }
            else
                updateErrorFields(
                    holder.etMedicine,
                    holder.rlType,
                    holder.rlFrequency,
                    holder.rlDose,
                    holder.rlDay,
                    position
                )
        }

        holder.etMedicine?.setText(prescriptionArray[position].getMedicine())
        holder.etMedicine?.setSelection(holder.etMedicine?.length()!!)
        holder.tvType?.text = prescriptionArray[position].getMedType()
        holder.tvFrequency?.text = prescriptionArray[position].getMedFrequency()
        holder.tvDose?.text = prescriptionArray[position].getMedDose()
        holder.tvDay?.text = prescriptionArray[position].getMedDay()


        holder.etMedicine?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {

                if (holder.etMedicine?.tag == position)
                    dialogPrescription.onListViewEditTextChange(
                        position,
                        holder.etMedicine?.text.toString()
                    )
            }
        })

        // Do something on dropdown view item
        holder.medTypeDropdownView.setOnItemClickListener { _, _, childPosition, _ ->
            dialogPrescription.onListViewItemClick(
                "type",
                position,
                childPosition,
                medTypeStringArray[childPosition]
            )
            holder.medTypeDropdownView.dismiss()
        }

        // Do something on dropdown view item
        holder.medFrequencyDropdownView?.setOnItemClickListener { _, _, childPosition, _ ->
            dialogPrescription.onListViewItemClick(
                "frequency",
                position,
                childPosition,
                medFrequencyStringArray[childPosition]
            )
            holder.medFrequencyDropdownView?.dismiss()
        }

        // Do something on dropdown view item
        holder.medDoseDropdownView.setOnItemClickListener { _, _, childPosition, _ ->
            dialogPrescription.onListViewItemClick(
                "dose",
                position,
                childPosition,
                medDoseStringArray[childPosition]
            )
            holder.medDoseDropdownView.dismiss()
        }

        // Do something on dropdown view item
        holder.medDayDropdownView.setOnItemClickListener { _, _, childPosition, _ ->
            dialogPrescription.onListViewItemClick(
                "day",
                position,
                childPosition,
                medDayStringArray[childPosition]
            )
            holder.medDayDropdownView.dismiss()
        }

        holder.rlType?.setOnClickListener {
            if (AppUtils.isKeyboardOpen(activity!!))
                AppUtils.hideKeyboardForOverlay(activity!!)
            holder.medTypeDropdownView.setAdapter(
                ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    medTypeStringArray
                )
            )
            holder.medTypeDropdownView.width = holder.rlType?.measuredWidth!!
            holder.medTypeDropdownView.isModal = true
            holder.medTypeDropdownView.anchorView = holder.rlType
            holder.medTypeDropdownView.show()
        }

        holder.rlFrequency?.setOnClickListener {
            if (AppUtils.isKeyboardOpen(activity!!))
                AppUtils.hideKeyboardForOverlay(activity!!)
            holder.medFrequencyDropdownView.setAdapter(
                ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    medFrequencyStringArray
                )
            )
            holder.medFrequencyDropdownView.width = holder.rlFrequency?.measuredWidth!!
            holder.medFrequencyDropdownView.isModal = true
            holder.medFrequencyDropdownView.anchorView = holder.rlFrequency
            holder.medFrequencyDropdownView.show()
        }

        holder.rlDose?.setOnClickListener {
            if (AppUtils.isKeyboardOpen(activity!!))
                AppUtils.hideKeyboardForOverlay(activity!!)
            holder.medDoseDropdownView.setAdapter(
                ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    medDoseStringArray
                )
            )
            holder.medDoseDropdownView.width = holder.rlDose?.measuredWidth!!
            holder.medDoseDropdownView.isModal = true
            holder.medDoseDropdownView.anchorView = holder.rlDose
            holder.medDoseDropdownView.show()
        }

        holder.rlDay?.setOnClickListener {
            if (AppUtils.isKeyboardOpen(activity!!))
                AppUtils.hideKeyboardForOverlay(activity!!)
            holder.medDayDropdownView.setAdapter(
                ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    medDayStringArray
                )
            )
            holder.medDayDropdownView.width = holder.rlDay?.measuredWidth!!
            holder.medDayDropdownView.isModal = true
            holder.medDayDropdownView.anchorView = holder.rlDay
            holder.medDayDropdownView.show()
        }

        holder.rlDelete?.setOnClickListener {
            dialogPrescription.onListViewItemRemove(position)
        }

        return convertView
    }

    private fun updateErrorFields(
        etMedicine: EditText?,
        rlType: RelativeLayout?,
        rlFrequency: RelativeLayout?,
        rlDose: RelativeLayout?,
        rlDay: RelativeLayout?,
        position: Int
    ) {
        if (etMedicine?.tag == position)
            if (prescriptionArray[position].getMedicine() == "")
                etMedicine.setBackgroundResource(R.drawable.bg_white_with_red_border)
            else
                etMedicine.setBackgroundResource(R.drawable.bg_white_with_gray_border)

        if (rlType?.tag == position)
            if (prescriptionArray[position].getMedType() == "")
                rlType.setBackgroundResource(R.drawable.bg_white_with_red_border)
            else
                rlType.setBackgroundResource(R.drawable.bg_white_with_gray_border)

        if (rlFrequency?.tag == position)
            if (prescriptionArray[position].getMedFrequency() == "")
                rlFrequency.setBackgroundResource(R.drawable.bg_white_with_red_border)
            else
                rlFrequency.setBackgroundResource(R.drawable.bg_white_with_gray_border)

        if (rlDose?.tag == position)
            if (prescriptionArray[position].getMedDose() == "")
                rlDose.setBackgroundResource(R.drawable.bg_white_with_red_border)
            else
                rlDose.setBackgroundResource(R.drawable.bg_white_with_gray_border)

        if (rlDay?.tag == position)
            if (prescriptionArray[position].getMedDay() == "")
                rlDay.setBackgroundResource(R.drawable.bg_white_with_red_border)
            else
                rlDay.setBackgroundResource(R.drawable.bg_white_with_gray_border)
    }

    internal class ViewHolder {

        var rlType: RelativeLayout? = null
        var rlFrequency: RelativeLayout? = null
        var rlDose: RelativeLayout? = null
        var rlDay: RelativeLayout? = null
        var rlDelete: RelativeLayout? = null
        var etMedicine: EditText? = null
        var tvType: TextView? = null
        var tvFrequency: TextView? = null
        var tvDose: TextView? = null
        var tvDay: TextView? = null
        var llDropDownParent: LinearLayout? = null

        lateinit var medTypeDropdownView: ListPopupWindow
        lateinit var medFrequencyDropdownView: ListPopupWindow
        lateinit var medDoseDropdownView: ListPopupWindow
        lateinit var medDayDropdownView: ListPopupWindow
    }

}
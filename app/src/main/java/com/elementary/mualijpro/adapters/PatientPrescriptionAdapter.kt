package com.elementary.mualijpro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.elementary.mualijpro.R
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientPrescriptionArray

import java.util.ArrayList

class PatientPrescriptionAdapter(var context: Context, data: ArrayList<PatientPrescriptionArray>) : BaseAdapter() {

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
            convertView = mInflater.inflate(R.layout.item_patient_prescription, null)
            holder = ViewHolder()
            holder.tvTitle = convertView!!.findViewById<View>(R.id.tvTitle) as TextView
            holder.tvDesc = convertView!!.findViewById(R.id.tvDesc) as TextView

            convertView.tag = holder
        } else
            holder = convertView.tag as ViewHolder

        holder.tvTitle!!.text = prescriptionArray[position].getMedicine()
        if (prescriptionArray[position].getMedDose().equals("N/A", true))
            holder.tvDesc!!.text =
                prescriptionArray[position].getMedType() + ", (" + prescriptionArray[position].getMedFrequency() + ") " + context.getString(
                    R.string.txt_for
                ) + " " + prescriptionArray[position].getMedDay()
        else
            holder.tvDesc!!.text =
                prescriptionArray[position].getMedType() + ", " + prescriptionArray[position].getMedDose() + ", (" + prescriptionArray[position].getMedFrequency() + ") " + context.getString(
                    R.string.txt_for
                ) + " " + prescriptionArray[position].getMedDay()

        return convertView
    }

    internal class ViewHolder {
        var tvTitle: TextView? = null
        var tvDesc: TextView? = null
    }

}
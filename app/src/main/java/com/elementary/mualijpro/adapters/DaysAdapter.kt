package com.elementary.mualijpro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.elementary.mualijpro.R
import com.elementary.mualijpro.interfaces.DashboardDialogsCallBack
import com.elementary.mualijpro.interfaces.PrescriptionSideMenuDays
import com.elementary.mualijpro.utils.Constants
import java.util.ArrayList

class DaysAdapter(private var days: DashboardDialogsCallBack, var context: Context, data: ArrayList<String>) :
    BaseAdapter() {

    private var daysArrayList = ArrayList<String>()
    private var mInflater: LayoutInflater

    init {
        this.daysArrayList = data
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return daysArrayList.size
    }

    override fun getItem(position: Int): Any {
        return daysArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view

        val holder: ViewHolder
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_start_end_time, null)
            holder = ViewHolder()
            holder.tvTime = convertView!!.findViewById<View>(R.id.tvTime) as TextView
            holder.llParent = convertView.findViewById<View>(R.id.llParent) as LinearLayout

            convertView.tag = holder
        } else
            holder = convertView.tag as ViewHolder

        holder.tvTime!!.text = daysArrayList[position]
        holder.llParent!!.setOnClickListener {
            days.onCallBack(Constants.dashboardDays, daysArrayList[position], ArrayList())
        }

        return convertView
    }

    internal class ViewHolder {
        var tvTime: TextView? = null
        var llParent: LinearLayout? = null
    }

}
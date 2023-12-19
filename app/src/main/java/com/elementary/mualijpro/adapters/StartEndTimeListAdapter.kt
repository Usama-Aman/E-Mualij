package com.elementary.mualijpro.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.elementary.mualijpro.R
import com.elementary.mualijpro.interfaces.WeeklyScheduleStartEndTime
import com.elementary.mualijpro.models.local.settings.SettingsStartEndTime
import com.elementary.mualijpro.utils.AppUtils

import java.util.ArrayList

class StartEndTimeListAdapter(
    startTime: Boolean,
    indexValue: Int,
    textView: TextView,
    rlTime: RelativeLayout,
    private var startEndTime: WeeklyScheduleStartEndTime,
    var context: Context,
    data: ArrayList<SettingsStartEndTime>
) : BaseAdapter(), Filterable {

    var originalData = ArrayList<SettingsStartEndTime>()
    var filteredData = ArrayList<SettingsStartEndTime>()
    private var mInflater: LayoutInflater
    private var mFilter = ItemFilter()
    var textView: TextView = textView
    var rlTime: RelativeLayout = rlTime
    private var startTime: Boolean = false
    private var indexValue: Int = 0

    init {
        this.startTime = startTime
        this.indexValue = indexValue
        this.filteredData = data
        this.originalData = data
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return filteredData.size
    }

    override fun getItem(position: Int): Any {
        return filteredData[position]
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

        holder.tvTime!!.text = filteredData[position].getTime()
        holder.llParent!!.setOnClickListener {
            AppUtils.hideKeyboard(context as Activity)
            startEndTime.onItemClick(startTime, indexValue, textView, rlTime, filteredData[position].getTime())
        }

        return convertView
    }

    internal class ViewHolder {
        var tvTime: TextView? = null
        var llParent: LinearLayout? = null
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {

            val filterString = constraint.toString().toLowerCase()
            val results = Filter.FilterResults()
            val list = originalData
            val count = list.size
            val nlist = ArrayList<SettingsStartEndTime>(count)
            var filterableString: String?

            for (i in 0 until count) {
                filterableString = list[i].getTime()
                if (filterableString!!.toLowerCase().contains(filterString.toLowerCase())) {
                    nlist.add(list[i])
                }
            }

            results.values = nlist
            results.count = nlist.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filteredData = results.values as ArrayList<SettingsStartEndTime>
            notifyDataSetChanged()
        }

    }


}
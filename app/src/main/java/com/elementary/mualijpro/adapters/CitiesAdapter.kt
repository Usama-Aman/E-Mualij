package com.elementary.mualijpro.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.elementary.mualijpro.R
import com.elementary.mualijpro.interfaces.CityCallBack
import com.elementary.mualijpro.models.posts.cities.Cities
import com.elementary.mualijpro.utils.AppUtils

import java.util.ArrayList

class CitiesAdapter(
    private var cityCallBack: CityCallBack,
    var context: Context,
    data: ArrayList<Cities>
) : BaseAdapter(), Filterable {

    var originalData = ArrayList<Cities>()
    var filteredData = ArrayList<Cities>()
    private var mInflater: LayoutInflater
    private var mFilter = ItemFilter()

    init {
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
            convertView = mInflater.inflate(R.layout.item_city_dialog, null)
            holder = ViewHolder()
            holder.tvCity = convertView!!.findViewById<View>(R.id.tvCity) as TextView
            holder.llParent = convertView!!.findViewById<View>(R.id.llParent) as LinearLayout
            holder.ivSelection = convertView!!.findViewById<View>(R.id.ivSelection) as ImageView

            convertView.tag = holder
        } else
            holder = convertView.tag as ViewHolder

        holder.ivSelection?.visibility = View.GONE

        if (filteredData[position].isSelected)
            holder.ivSelection?.visibility = View.VISIBLE

        if (AppUtils.getDefaultLang().equals("en", true))
            holder.tvCity!!.text = filteredData[position].getNameEn()
        else
            holder.tvCity!!.text = filteredData[position].getNameAr()

        holder.llParent!!.setOnClickListener {
            AppUtils.hideKeyboard(context as Activity)
            cityCallBack.onCallBack(filteredData[position], position)
        }

        return convertView
    }

    internal class ViewHolder {
        var tvCity: TextView? = null
        var llParent: LinearLayout? = null
        var ivSelection: ImageView? = null
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
            val nlist = ArrayList<Cities>(count)
            var filterableString: String?

            for (i in 0 until count) {
                filterableString = if (AppUtils.getDefaultLang().equals("en", true))
                    list[i].getNameEn()
                else
                    list[i].getNameAr()

                if (filterableString!!.toLowerCase().contains(filterString.toLowerCase())) {
                    nlist.add(list[i])
                }
            }

            results.values = nlist
            results.count = nlist.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filteredData = results.values as ArrayList<Cities>
            notifyDataSetChanged()
        }

    }


}
package com.elementary.mualijpro.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elementary.mualijpro.R
import com.elementary.mualijpro.models.local.dashboard.FinalWeeklyAppointments
import com.elementary.mualijpro.ui.fragments.dashboard.DashboardFragment
import com.github.florent37.viewtooltip.ViewTooltip
import java.util.ArrayList
import androidx.core.content.res.ResourcesCompat
import com.elementary.mualijpro.MualijProApplication.Companion.isPhone
import com.elementary.mualijpro.utils.AppUtils


@Suppress("DEPRECATION")
class AppointmentTimingAdapter(
    var fragment: DashboardFragment,
    ctx: Context,
    finalWeeklyAppointments: ArrayList<FinalWeeklyAppointments>
) : RecyclerView.Adapter<AppointmentTimingAdapter.MyViewHolder>() {

    var typeface: Typeface? = null
    var context: Context = ctx
    private val inflater: LayoutInflater = LayoutInflater.from(ctx)
    private var finalWeeklyAppointments = ArrayList<FinalWeeklyAppointments>()

    init {
        this.typeface = ResourcesCompat.getFont(context, R.font.sharp_sans_medium)
        this.finalWeeklyAppointments = finalWeeklyAppointments
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater.inflate(R.layout.item_appointment_time, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        try {
            holder.rlParentTimeOne.visibility = View.VISIBLE
            holder.rlParentTimeTwo.visibility = View.VISIBLE
            holder.rlParentTimeThree.visibility = View.VISIBLE
            holder.rlParentTimeFour.visibility = View.VISIBLE
            holder.rlParentTimeFive.visibility = View.VISIBLE

            holder.linearAppointment.weightSum = if (isPhone) 3f else 5f

            when (finalWeeklyAppointments[position].getTimeArray().size) {
                5 -> {
                    // Set Time and patient name
                    holder.tvNameOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getAppointmentTime()
                    holder.tvNameTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getAppointmentTime()
                    holder.tvNameThree.text =
                        finalWeeklyAppointments[position].getTimeArray()[2].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeThree.text =
                        finalWeeklyAppointments[position].getTimeArray()[2].getAppointmentTime()
                    holder.tvNameFour.text =
                        finalWeeklyAppointments[position].getTimeArray()[3].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeFour.text =
                        finalWeeklyAppointments[position].getTimeArray()[3].getAppointmentTime()
                    holder.tvNameFive.text =
                        finalWeeklyAppointments[position].getTimeArray()[4].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeFive.text =
                        finalWeeklyAppointments[position].getTimeArray()[4].getAppointmentTime()
                    // Set Views Colors
                    holder.rlParentTimeOne.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[0].getBgColor()!!)
                    holder.tvTimeOne.setTextColor(finalWeeklyAppointments[position].getTimeArray()[0].getTimeColor()!!)
                    holder.rlParentTimeTwo.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[1].getBgColor()!!)
                    holder.tvTimeTwo.setTextColor(finalWeeklyAppointments[position].getTimeArray()[1].getTimeColor()!!)
                    holder.rlParentTimeThree.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[2].getBgColor()!!)
                    holder.tvTimeThree.setTextColor(finalWeeklyAppointments[position].getTimeArray()[2].getTimeColor()!!)
                    holder.rlParentTimeFour.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[3].getBgColor()!!)
                    holder.tvTimeFour.setTextColor(finalWeeklyAppointments[position].getTimeArray()[3].getTimeColor()!!)
                    holder.rlParentTimeFive.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[4].getBgColor()!!)
                    holder.tvTimeFive.setTextColor(finalWeeklyAppointments[position].getTimeArray()[4].getTimeColor()!!)
                }
                4 -> {
                    // Set Time and patient name
                    holder.tvNameOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getAppointmentTime()
                    holder.tvNameTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getAppointmentTime()
                    holder.tvNameThree.text =
                        finalWeeklyAppointments[position].getTimeArray()[2].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeThree.text =
                        finalWeeklyAppointments[position].getTimeArray()[2].getAppointmentTime()
                    holder.tvNameFour.text =
                        finalWeeklyAppointments[position].getTimeArray()[3].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeFour.text =
                        finalWeeklyAppointments[position].getTimeArray()[3].getAppointmentTime()
                    holder.rlParentTimeFive.visibility = View.INVISIBLE
                    // Set Views Colors
                    holder.rlParentTimeOne.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[0].getBgColor()!!)
                    holder.tvTimeOne.setTextColor(finalWeeklyAppointments[position].getTimeArray()[0].getTimeColor()!!)
                    holder.rlParentTimeTwo.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[1].getBgColor()!!)
                    holder.tvTimeTwo.setTextColor(finalWeeklyAppointments[position].getTimeArray()[1].getTimeColor()!!)
                    holder.rlParentTimeThree.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[2].getBgColor()!!)
                    holder.tvTimeThree.setTextColor(finalWeeklyAppointments[position].getTimeArray()[2].getTimeColor()!!)
                    holder.rlParentTimeFour.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[3].getBgColor()!!)
                    holder.tvTimeFour.setTextColor(finalWeeklyAppointments[position].getTimeArray()[3].getTimeColor()!!)
                }
                3 -> {
                    // Set Time and patient name
                    holder.tvNameOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getAppointmentTime()
                    holder.tvNameTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getAppointmentTime()
                    holder.tvNameThree.text =
                        finalWeeklyAppointments[position].getTimeArray()[2].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeThree.text =
                        finalWeeklyAppointments[position].getTimeArray()[2].getAppointmentTime()
                    holder.rlParentTimeFour.visibility = View.INVISIBLE
                    holder.rlParentTimeFive.visibility = View.INVISIBLE
                    // Set Views Colors
                    holder.rlParentTimeOne.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[0].getBgColor()!!)
                    holder.tvTimeOne.setTextColor(finalWeeklyAppointments[position].getTimeArray()[0].getTimeColor()!!)
                    holder.rlParentTimeTwo.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[1].getBgColor()!!)
                    holder.tvTimeTwo.setTextColor(finalWeeklyAppointments[position].getTimeArray()[1].getTimeColor()!!)
                    holder.rlParentTimeThree.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[2].getBgColor()!!)
                    holder.tvTimeThree.setTextColor(finalWeeklyAppointments[position].getTimeArray()[2].getTimeColor()!!)
                }
                2 -> {
                    // Set Time and patient name
                    holder.tvNameOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getAppointmentTime()
                    holder.tvNameTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getAppointmentTime()
                    holder.rlParentTimeThree.visibility = View.INVISIBLE
                    holder.rlParentTimeFour.visibility = View.INVISIBLE
                    holder.rlParentTimeFive.visibility = View.INVISIBLE
                    // Set Views Colors
                    holder.rlParentTimeOne.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[0].getBgColor()!!)
                    holder.tvTimeOne.setTextColor(finalWeeklyAppointments[position].getTimeArray()[0].getTimeColor()!!)
                    holder.rlParentTimeTwo.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[1].getBgColor()!!)
                    holder.tvTimeTwo.setTextColor(finalWeeklyAppointments[position].getTimeArray()[1].getTimeColor()!!)
                }
                1 -> {
                    // Set Time and patient name
                    holder.tvNameOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getShortName()!!
                            .toUpperCase()
                    holder.tvTimeOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getAppointmentTime()
                    holder.rlParentTimeTwo.visibility = View.INVISIBLE
                    holder.rlParentTimeThree.visibility = View.INVISIBLE
                    holder.rlParentTimeFour.visibility = View.INVISIBLE
                    holder.rlParentTimeFive.visibility = View.INVISIBLE
                    // Set Views Colors
                    holder.rlParentTimeOne.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[0].getBgColor()!!)
                    holder.tvTimeOne.setTextColor(finalWeeklyAppointments[position].getTimeArray()[0].getTimeColor()!!)
                }
            }
        } catch (e: Exception) {

        }

        holder.rlParentTimeOne.setOnClickListener {
            fragment.onAppointmentItemClicked(position, 0)
        }

        holder.rlParentTimeTwo.setOnClickListener {
            fragment.onAppointmentItemClicked(position, 1)
        }

        holder.rlParentTimeThree.setOnClickListener {
            fragment.onAppointmentItemClicked(position, 2)
        }

        holder.rlParentTimeFour.setOnClickListener {
            fragment.onAppointmentItemClicked(position, 3)
        }

        holder.rlParentTimeFive.setOnClickListener {
            fragment.onAppointmentItemClicked(position, 4)
        }

    }

    override fun getItemCount(): Int {
        return finalWeeklyAppointments.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var rlParentTimeOne: RelativeLayout = itemView.findViewById(R.id.rlParentTimeOne)
        var rlParentTimeTwo: RelativeLayout = itemView.findViewById(R.id.rlParentTimeTwo)
        var rlParentTimeThree: RelativeLayout = itemView.findViewById(R.id.rlParentTimeThree)
        var rlParentTimeFour: RelativeLayout = itemView.findViewById(R.id.rlParentTimeFour)
        var rlParentTimeFive: RelativeLayout = itemView.findViewById(R.id.rlParentTimeFive)

        var linearAppointment: LinearLayout = itemView.findViewById(R.id.linearAppointment)

        var tvNameOne: TextView = itemView.findViewById(R.id.tvNameOne)
        var tvNameTwo: TextView = itemView.findViewById(R.id.tvNameTwo)
        var tvNameThree: TextView = itemView.findViewById(R.id.tvNameThree)
        var tvNameFour: TextView = itemView.findViewById(R.id.tvNameFour)
        var tvNameFive: TextView = itemView.findViewById(R.id.tvNameFive)

        var tvTimeOne: TextView = itemView.findViewById(R.id.tvTimeOne)
        var tvTimeTwo: TextView = itemView.findViewById(R.id.tvTimeTwo)
        var tvTimeThree: TextView = itemView.findViewById(R.id.tvTimeThree)
        var tvTimeFour: TextView = itemView.findViewById(R.id.tvTimeFour)
        var tvTimeFive: TextView = itemView.findViewById(R.id.tvTimeFive)

    }
}
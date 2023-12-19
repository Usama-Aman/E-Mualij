package com.elementary.mualijpro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.elementary.mualijpro.MualijProApplication
import com.elementary.mualijpro.MualijProApplication.Companion.isPhone
import com.elementary.mualijpro.R
import com.elementary.mualijpro.dialogs.DialogReschedule
import com.elementary.mualijpro.models.local.reschedule.FinalRescheduleAvailableList

import java.util.ArrayList

@Suppress("DEPRECATION")
class AvailableScheduleAdapter(
    var fragment: DialogReschedule,
    ctx: Context,
    finalWeeklyAppointments: ArrayList<FinalRescheduleAvailableList>
) :
    RecyclerView.Adapter<AvailableScheduleAdapter.MyViewHolder>() {

    var context: Context = ctx
    private val inflater: LayoutInflater = LayoutInflater.from(ctx)
    private var finalWeeklyAppointments = ArrayList<FinalRescheduleAvailableList>()

    init {
        this.finalWeeklyAppointments = finalWeeklyAppointments
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater.inflate(R.layout.item_reschedule, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        try {

            holder.rlParentTimeOne.visibility = View.VISIBLE
            holder.rlParentTimeTwo.visibility = View.VISIBLE
            holder.rlParentTimeThree.visibility = View.VISIBLE
            holder.rlParentTimeFour.visibility = View.VISIBLE
            holder.rlParentTimeFive.visibility = View.VISIBLE

            holder.linearReschedule.weightSum = if (MualijProApplication.isPhone) 3f else 5f

            when (finalWeeklyAppointments[position].getTimeArray().size) {
                5 -> {
                    // Set Time and patient name
                    holder.tvTimeOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getAppointmentTime()
                    holder.tvTimeTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getAppointmentTime()
                    holder.tvTimeThree.text =
                        finalWeeklyAppointments[position].getTimeArray()[2].getAppointmentTime()
                    holder.tvTimeFour.text =
                        finalWeeklyAppointments[position].getTimeArray()[3].getAppointmentTime()
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
                    holder.tvTimeOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getAppointmentTime()
                    holder.tvTimeTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getAppointmentTime()
                    holder.tvTimeThree.text =
                        finalWeeklyAppointments[position].getTimeArray()[2].getAppointmentTime()
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
                    holder.tvTimeOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getAppointmentTime()
                    holder.tvTimeTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getAppointmentTime()
                    holder.tvTimeThree.text =
                        finalWeeklyAppointments[position].getTimeArray()[2].getAppointmentTime()

                    if (isPhone) {
                        holder.rlParentTimeFour.visibility = View.GONE
                        holder.rlParentTimeFive.visibility = View.GONE
                    } else {
                        holder.rlParentTimeFour.visibility = View.INVISIBLE
                        holder.rlParentTimeFive.visibility = View.INVISIBLE
                    }
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
                    holder.tvTimeOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getAppointmentTime()
                    holder.tvTimeTwo.text =
                        finalWeeklyAppointments[position].getTimeArray()[1].getAppointmentTime()
                    if (isPhone) {
                        holder.rlParentTimeThree.visibility = View.GONE
                        holder.rlParentTimeFour.visibility = View.GONE
                        holder.rlParentTimeFive.visibility = View.GONE
                    } else {
                        holder.rlParentTimeThree.visibility = View.INVISIBLE
                        holder.rlParentTimeFour.visibility = View.INVISIBLE
                        holder.rlParentTimeFive.visibility = View.INVISIBLE
                    }
                    // Set Views Colors
                    holder.rlParentTimeOne.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[0].getBgColor()!!)
                    holder.tvTimeOne.setTextColor(finalWeeklyAppointments[position].getTimeArray()[0].getTimeColor()!!)
                    holder.rlParentTimeTwo.setBackgroundResource(finalWeeklyAppointments[position].getTimeArray()[1].getBgColor()!!)
                    holder.tvTimeTwo.setTextColor(finalWeeklyAppointments[position].getTimeArray()[1].getTimeColor()!!)
                }
                1 -> {
                    // Set Time and patient name
                    holder.tvTimeOne.text =
                        finalWeeklyAppointments[position].getTimeArray()[0].getAppointmentTime()
                    if (isPhone) {
                        holder.rlParentTimeTwo.visibility = View.GONE
                        holder.rlParentTimeThree.visibility = View.GONE
                        holder.rlParentTimeFour.visibility = View.GONE
                        holder.rlParentTimeFive.visibility = View.GONE
                    }else{
                        holder.rlParentTimeTwo.visibility = View.INVISIBLE
                        holder.rlParentTimeThree.visibility = View.INVISIBLE
                        holder.rlParentTimeFour.visibility = View.INVISIBLE
                        holder.rlParentTimeFive.visibility = View.INVISIBLE
                    }
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

        var linearReschedule: LinearLayout = itemView.findViewById(R.id.linearReschedule)


        var tvTimeOne: TextView = itemView.findViewById(R.id.tvTimeOne)
        var tvTimeTwo: TextView = itemView.findViewById(R.id.tvTimeTwo)
        var tvTimeThree: TextView = itemView.findViewById(R.id.tvTimeThree)
        var tvTimeFour: TextView = itemView.findViewById(R.id.tvTimeFour)
        var tvTimeFive: TextView = itemView.findViewById(R.id.tvTimeFive)


    }
}
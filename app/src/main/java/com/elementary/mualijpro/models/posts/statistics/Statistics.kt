package com.elementary.mualijpro.models.posts.statistics

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Statistics {

    @SerializedName("total_appointments")
    @Expose
    private var totalAppointments: Int? = 0
    @SerializedName("current_month_amount")
    @Expose
    private var currentMonthAmount: String? = ""
    @SerializedName("total_amount")
    @Expose
    private var totalAmount: String? = ""
    @SerializedName("average_rating")
    @Expose
    private var averageRating: String? = ""
    @SerializedName("total_reviews")
    @Expose
    private var totalReviews: Int? = 0
    @SerializedName("online_appointments")
    @Expose
    private var onlineAppointments: Int? = 0
    @SerializedName("clinic_appointments")
    @Expose
    private var clinicAppointments: Int? = 0
    @SerializedName("past_appointments")
    @Expose
    private var pastAppointments: Int? = 0
    @SerializedName("upcomings_appointments")
    @Expose
    private var upcomingsAppointments: Int? = 0
    @SerializedName("current_year")
    @Expose
    private var currentYear: String? = ""
    @SerializedName("revenue_progress")
    @Expose
    private var revenueProgress: RevenueProgress = RevenueProgress()
    @SerializedName("appointment_progress")
    @Expose
    private var appointmentProgress: AppointmentProgress = AppointmentProgress()


    fun getTotalAppointments(): Int? {
        if (totalAppointments == null) return 0
        return totalAppointments
    }

    fun setTotalAppointments(totalAppointments: Int) {
        this.totalAppointments = totalAppointments
    }

    fun getCurrentYear(): String? {
        if (currentYear == null) return "0"
        return currentYear
    }

    fun getCurrentMonthAmount(): String? {
        if (currentMonthAmount == null) return ""
        return currentMonthAmount
    }

    fun setCurrentMonthAmount(currentMonthAmount: String) {
        this.currentMonthAmount = currentMonthAmount
    }

    fun getTotalAmount(): String? {
        if (totalAmount == null) return ""
        return totalAmount
    }

    fun setTotalAmount(totalAmount: String?) {
        this.totalAmount = totalAmount
    }

    fun getAverageRating(): String? {
        if (averageRating == null) return "0"
        return averageRating
    }

    fun setAverageRating(averageRating: String?) {
        this.averageRating = averageRating
    }

    fun getTotalReviews(): Int? {
        if (totalReviews == null) return 0
        return totalReviews
    }

    fun setTotalReviews(totalReviews: Int) {
        this.totalReviews = totalReviews
    }

    fun getOnlineAppointments(): Int? {
        if (onlineAppointments == null) return 0
        return onlineAppointments
    }

    fun setOnlineAppointments(onlineAppointments: Int) {
        this.onlineAppointments = onlineAppointments
    }

    fun getClinicAppointments(): Int? {
        if (clinicAppointments == null) return 0
        return clinicAppointments
    }

    fun setClinicAppointments(clinicAppointments: Int) {
        this.clinicAppointments = clinicAppointments
    }

    fun getPastAppointments(): Int? {
        if (pastAppointments == null) return 0
        return pastAppointments
    }

    fun setPastAppointments(pastAppointments: Int) {
        this.pastAppointments = pastAppointments
    }

    fun getUpcomingsAppointments(): Int? {
        if (upcomingsAppointments == null) return 0
        return upcomingsAppointments
    }

    fun setUpcomingsAppointments(upcomingsAppointments: Int) {
        this.upcomingsAppointments = upcomingsAppointments
    }

    fun getRevenueProgress(): RevenueProgress {
        if (revenueProgress == null) return RevenueProgress()
        return revenueProgress
    }

    fun setRevenueProgress(revenueProgress: RevenueProgress) {
        this.revenueProgress = revenueProgress
    }

    fun getAppointmentProgress(): AppointmentProgress {
        if (appointmentProgress == null) return AppointmentProgress()
        return appointmentProgress
    }

    fun setAppointmentProgress(appointmentProgress: AppointmentProgress) {
        this.appointmentProgress = appointmentProgress
    }

}
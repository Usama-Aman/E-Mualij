package com.elementary.mualijpro.models.local.profile

import com.elementary.mualijpro.models.posts.user.UserGender

data class ProfileGetModel(
    val app_token: Any,
    val code: Int,
    val `data`: ProfileData,
    val message: String,
    val status: Boolean
)

data class ProfileData(
    val avg_handling_rating: String,
    val avg_overall_rating: String,
    val avg_wait_rating: String,
    val category_id: Int,
    val certificates: String,
    val city_id: Int,
    val designation: String,
    val doctor_fee: String,
    val education: String,
    val email: String,
    val first_name: String,
    val full_image: String,
    val full_name: String,
    val gender_id: Int,
    val id: Int,
    val is_activated: Int,
    val is_featured: Int,
    val is_live: Int,
    val is_online_accept: Int,
    val is_sponsored: Int,
    val lang_key: String,
    val last_login: String,
    val last_login_from: String,
    val last_name: String,
    val latitude: Double,
    val location: String,
    val longitude: Double,
    val mobile_number: String,
    val photo: String,
    val preferred_language: String,
    val rating: String,
    val remember_token: String,
    val statement: String,
    val total_reviews: Int,
    private var gender: UserGender? = UserGender()
)
package com.metacoders.blood_donation.model

data class BloodReq(
    val userId : String? = "",
    val address : String? = "",
    val phn : String? = "",
    val reqID : String? = "",
    val reqBlood : String? = "",
    val time : Long = 0,
    val userName : String? = "",
    val lat: Double? = 0.00,
    val lon: Double? = 0.00
)

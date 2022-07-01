package com.metacoders.blood_donation.model

data class Users(
    var email: String? = null,
    var mobile: String? = null,
    val userName : String? = "",
    var isDonate: Boolean? = false,
    var user_id: String? = null,
    var address: String? = null,
    var bg: String? = null,
    var user_image: String? = "",
    var uid: String? = "",

    )

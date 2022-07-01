package com.metacoders.blood_donation.model

data class ChatHistoryModel(
    var user1: String? = "null",
    var user2: String = "null",
    var lsatMsgSender: String = "",
    var lastMsg: String = "",
    var msg: String ="" ,
    var timestamp: Long = 0
)

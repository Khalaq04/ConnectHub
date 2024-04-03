package com.krskhalaq.connecthub

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val msg: String = "",
    val time: String = ""
)
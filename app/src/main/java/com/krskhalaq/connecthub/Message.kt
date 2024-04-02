package com.krskhalaq.connecthub

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val hashMap: HashMap<String, String> = HashMap()
)


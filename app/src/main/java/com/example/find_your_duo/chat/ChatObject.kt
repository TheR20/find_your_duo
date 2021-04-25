package com.example.find_your_duo.chat

class ChatObject(internal var message: String, var currentUser: Boolean) {
    fun getMessage(): String {
        return message
    }

    fun setMessage(userID: String?) {
        message = message
    }

}
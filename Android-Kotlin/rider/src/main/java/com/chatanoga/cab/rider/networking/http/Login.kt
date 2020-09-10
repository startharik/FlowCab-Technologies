package com.chatanoga.cab.rider.networking.http

import com.chatanoga.cab.common.models.Rider
import com.chatanoga.cab.common.networking.http.interfaces.HTTPRequest

class Login(fireBaseToken: String): HTTPRequest() {
    override val path: String = "rider/login"
    init {
        this.params = mapOf("token" to fireBaseToken)
    }
}

data class LoginResult(
    val token: String,
    val user: Rider
)
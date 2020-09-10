package com.chatanoga.cab.driver.networking.http

import com.chatanoga.cab.common.models.Driver
import com.chatanoga.cab.common.networking.http.interfaces.HTTPRequest
import com.squareup.moshi.JsonClass

class Login(fireBaseToken: String): HTTPRequest() {
    override val path: String = "driver/login"
    init {
        this.params = mapOf("token" to fireBaseToken)
    }
}

@JsonClass(generateAdapter = true)
data class LoginResult(
    val token: String,
    val user: Driver
)
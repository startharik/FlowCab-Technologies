package com.chatanoga.cab.driver.networking.http

import com.chatanoga.cab.common.models.Driver
import com.chatanoga.cab.common.models.Service
import com.chatanoga.cab.common.networking.http.interfaces.HTTPRequest
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

class GetRegisterInfo(jwtToken: String): HTTPRequest() {
    override val path: String = "driver/get"
    init {
        this.params = mapOf("token" to jwtToken)
    }
}
@JsonClass(generateAdapter = true)
data class RegistrationInfo(
    val driver: Driver,
    val services: List<Service>
)
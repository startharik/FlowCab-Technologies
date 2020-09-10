package com.chatanoga.cab.driver.networking.http

import com.chatanoga.cab.common.models.Driver
import com.chatanoga.cab.common.networking.http.interfaces.HTTPRequest
import com.chatanoga.cab.common.utils.Adapters
import org.json.JSONObject

class Register(jwtToken: String, driver: Driver) : HTTPRequest() {
    override val path: String = "driver/register"

    init {
        val mapped = Adapters.moshi.adapter<Driver>(Driver::class.java).toJsonValue(driver)!!
        this.params = mapOf("token" to jwtToken, "driver" to mapped)
    }
}
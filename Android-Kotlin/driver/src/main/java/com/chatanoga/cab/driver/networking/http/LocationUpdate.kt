package com.chatanoga.cab.driver.networking.http

import com.google.android.gms.maps.model.LatLng
import com.chatanoga.cab.common.networking.http.interfaces.HTTPRequest

class LocationUpdate(jwtToken: String, location: LatLng, inTravel: Boolean): HTTPRequest() {
    override val path: String = "driver/update_location"
    init {
        this.params = mapOf(
                "token" to jwtToken,
                "location" to mapOf("x" to location.longitude, "y" to location.latitude),
                "inTravel" to inTravel
        )
    }
}
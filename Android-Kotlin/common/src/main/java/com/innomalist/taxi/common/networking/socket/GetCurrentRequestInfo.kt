package com.chatanoga.cab.common.networking.socket

import com.google.android.gms.maps.model.LatLng
import com.chatanoga.cab.common.models.Request
import com.chatanoga.cab.common.networking.socket.interfaces.SocketRequest
import com.squareup.moshi.JsonClass

class GetCurrentRequestInfo: SocketRequest()

@JsonClass(generateAdapter = true)
data class CurrentRequestResult(
        val request: Request,
        val driverLocation: LatLng?
)
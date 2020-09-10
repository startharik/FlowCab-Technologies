package ccom.chatanoga.cab.rider.networking.socket

import com.google.android.gms.maps.model.LatLng
import ccom.chatanoga.cab.common.networking.socket.interfaces.SocketRequest
import org.json.JSONObject

class GetDriversLocations(location: LatLng) : SocketRequest() {
    init {
        val obj = JSONObject()
        obj.put("x", location.longitude)
        obj.put("y", location.latitude)
        this.params = arrayOf(obj)
    }
}
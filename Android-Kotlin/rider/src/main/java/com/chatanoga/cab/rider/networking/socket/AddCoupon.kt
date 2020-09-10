package com.chatanoga.cab.rider.networking.socket

import com.chatanoga.cab.common.networking.socket.interfaces.SocketRequest

class AddCoupon(code: String): SocketRequest() {
    init {
        this.params = arrayOf(code)
    }
}
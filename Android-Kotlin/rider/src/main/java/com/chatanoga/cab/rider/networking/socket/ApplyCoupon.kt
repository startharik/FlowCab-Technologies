package com.chatanoga.cab.rider.networking.socket

import com.chatanoga.cab.common.networking.socket.interfaces.SocketRequest

class ApplyCoupon(code: String) : SocketRequest() {
    init {
        this.params = arrayOf(code)
    }
}
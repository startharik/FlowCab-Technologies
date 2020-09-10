package com.chatanoga.cab.rider.networking.socket

import com.chatanoga.cab.common.networking.socket.interfaces.SocketRequest

class DeleteAddress(id: Int): SocketRequest() {
    init {
        this.params = arrayOf(id)
    }
}
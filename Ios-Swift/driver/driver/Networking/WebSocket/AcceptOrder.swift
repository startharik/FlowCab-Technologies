//
//  AcceptOrder.swift
//  driver
//

import Foundation


class AcceptOrder: SocketRequest {
    typealias ResponseType = Request
    var params: [Any]?
    
    init(requestId: Int) {
        self.params = [requestId]
    }
}

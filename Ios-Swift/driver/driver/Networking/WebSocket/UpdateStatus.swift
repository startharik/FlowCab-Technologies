//
//  UpdateStatus.swift
//  driver
//

import Foundation


class UpdateStatus: SocketRequest {
    typealias ResponseType = EmptyClass
    var params: [Any]?
    
    init(turnOnline: Bool) {
        self.params = [turnOnline]
    }
}

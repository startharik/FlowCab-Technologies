//
//  GetRegisterInfo.swift
//  driver
//


import Foundation


class GetRegisterInfo: HTTPRequest {
    var path: String = "driver/get"
    typealias ResponseType = RegistrationInfo
    var params: [String : Any]?
    
    init(jwtToken: String) {
        self.params = [
            "token": jwtToken
        ]
    }
}

struct RegistrationInfo: Codable {
    var driver: Driver
    var services: [Service] = []
}

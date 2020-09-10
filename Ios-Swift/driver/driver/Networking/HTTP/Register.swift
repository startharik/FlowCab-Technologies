//
//  Register.swift
//  driver
//


import Foundation


class Register: HTTPRequest {
    var params: [String : Any]?
    var path: String = "driver/register"
    typealias ResponseType = EmptyClass
    
    init(jwtToken: String, driver: Driver) {
        self.params = [
            "token": jwtToken,
            "driver": try! driver.asDictionary()
        ]
    }
}

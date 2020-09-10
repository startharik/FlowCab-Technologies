//
//  Login.swift
//  driver
//


import Foundation


class Login: HTTPRequest {
    var params: [String : Any]?
    var path: String = "driver/login"
    
    typealias ResponseType = LoginResult
    
    init(firebaseToken: String) {
        self.params = [
            "token": firebaseToken
        ]
    }
}

struct LoginResult: Codable {
    var token: String
    var user: Driver
}

//
//  GetAvailableRequests.swift
//  driver
//

import Foundation


class GetAvailableRequests: SocketRequest {
    typealias ResponseType = [Request]
    
    required public init() {}
}

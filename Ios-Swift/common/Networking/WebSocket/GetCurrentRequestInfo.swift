//
//  GetCurrentRequestInfo.swift
//  Shared
//


import Foundation
import MapKit

public class GetCurrentRequestInfo: SocketRequest {
    public typealias ResponseType = CurrentRequestInfo
    
    required public init() {}
}

public struct CurrentRequestInfo: Codable {
    var request: Request
    var driverLocation: CLLocationCoordinate2D?
    
}

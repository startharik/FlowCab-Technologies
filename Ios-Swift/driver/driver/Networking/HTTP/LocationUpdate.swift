//
//  LocationUpdate.swift
//  driver
//

import Foundation

import MapKit

public class LocationUpdate: HTTPRequest {
    public var path: String = "driver/update_location"
    
    public typealias ResponseType = EmptyClass
    public var params: [String : Any]?
    
    init(jwtToken: String, location: CLLocationCoordinate2D, inTravel: Bool = false) {
        self.params = [
            "token": jwtToken,
            "location": try! location.asDictionary(),
            "inTravel": inTravel
        ]
    }
}

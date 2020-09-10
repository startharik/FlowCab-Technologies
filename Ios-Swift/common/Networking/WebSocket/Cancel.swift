//
//  CancelRequest.swift
//  Shared
//

import Foundation

public class Cancel: SocketRequest {
    public typealias ResponseType = EmptyClass
    
    required public init() {}
}

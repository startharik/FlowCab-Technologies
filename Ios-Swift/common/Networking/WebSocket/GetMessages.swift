//
//  GetMessages.swift
//  Shared
//

import Foundation

public class GetMessages: SocketRequest {
    public typealias ResponseType = [ChatMessage]
    
    required public init() {}
}

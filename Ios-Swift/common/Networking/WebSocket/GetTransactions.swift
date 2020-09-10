//
//  GetTransactions.swift
//  Shared
//


import Foundation

public class GetTransactions: SocketRequest {
    public typealias ResponseType = [Transaction]
    
    required public init() {}
}

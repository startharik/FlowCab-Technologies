//
//  NetworkDispatcher.swift
//

import Foundation

public protocol NetworkDispatcher {
    func dispatch(event: String, params: [Any]?, completionHandler: @escaping (Result<Any, SocketClientError>) -> Void)
}

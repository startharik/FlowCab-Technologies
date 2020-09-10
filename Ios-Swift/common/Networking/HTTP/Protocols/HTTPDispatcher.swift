
import Foundation

public protocol HTTPDispatcher {
    func dispatch(path: String, method: HTTPMethod, params: [String: Any]?, completionHandler: @escaping (Result<Data, HTTPStatusCode>) -> Void)
}

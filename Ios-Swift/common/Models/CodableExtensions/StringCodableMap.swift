//
//  StringCodableMap.swift
//  rider
//


import UIKit

public struct StringCodableMap<Decoded : LosslessStringConvertible> : Codable {
    public var decoded: Decoded
    
    init(_ decoded: Decoded) {
        self.decoded = decoded
    }
    
    public init(from decoder: Decoder) throws {
        
        let container = try decoder.singleValueContainer()
        let decodedString = try container.decode(String.self)
        guard let decoded = Decoded(decodedString) else {
            throw DecodingError.dataCorruptedError(
                in: container, debugDescription: """
                The string \(decodedString) is not representable as a \(Decoded.self)
                """
            )
        }
        
        self.decoded = decoded
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(decoded.description)
    }
}

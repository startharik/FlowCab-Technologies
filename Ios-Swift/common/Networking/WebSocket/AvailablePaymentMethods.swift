//
//  AvailablePaymentMethods.swift
//  Shared
//


import UIKit

public class AvailablePaymentMethods: SocketRequest {
    public typealias ResponseType = [PaymentGateway]
    
    required public init() {}
}

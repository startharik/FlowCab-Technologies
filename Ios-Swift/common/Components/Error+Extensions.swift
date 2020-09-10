//
//  Error+Extensions.swift
//  Shared
//

import Foundation
import StatusAlert

public extension HTTPStatusCode {
    func showAlert() {
        // Creating StatusAlert instance
        let statusAlert = StatusAlert()
        statusAlert.title = NSLocalizedString("Error Happened", comment: "Default title for any error occured")
        statusAlert.message = self.localizedDescription
        if #available(iOS 13.0, *) {
            statusAlert.appearance.tintColor = UIColor.label
        }
        statusAlert.canBePickedOrDismissed = true
        statusAlert.image = UIImage(named: "alert_error")
        statusAlert.showInKeyWindow()
    }
}

//
//  WaitingForPaymentViewController.swift
//  driver
//

import UIKit
import Lottie

class WaitingForPaymentViewController: UIViewController {
    @IBOutlet weak var backgroundView: UIView!
    @IBOutlet weak var viewLoading: UIView!
    @IBOutlet weak var buttonCash: ColoredButton!
    
    var animationView: AnimationView!
    var onDone: ((Bool) -> Void)?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        NotificationCenter.default.addObserver(self, selector:#selector(self.onPaid), name: .paid, object: nil)
        NotificationCenter.default.addObserver(self, selector:#selector(self.requestRefresh), name: .connectedAfterForeground, object: nil)
        NotificationCenter.default.addObserver(self, selector:#selector(self.onForeground), name: UIApplication.willEnterForegroundNotification, object: nil)
        let blurEffect = UIBlurEffect(style: UIBlurEffect.Style.regular)
        let blurEffectView = UIVisualEffectView(effect: blurEffect)
        blurEffectView.frame = self.view.bounds
        //blurEffectView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        self.backgroundView.addSubview(blurEffectView)
        animationView = AnimationView(name: "cash")
        animationView.contentMode = .scaleAspectFit
        animationView.translatesAutoresizingMaskIntoConstraints = false
        animationView.loopMode = .loop
        animationView.backgroundColor = UIColor.clear
        animationView.play()
        viewLoading.addSubview(animationView)
    }
    
    @objc func onForeground(_ notification: Notification) {
        self.buttonCash.isEnabled = false
    }
    
    @objc private func onPaid() {
        self.onDone?(true)
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func onCashPaidTouched(_ sender: Any) {
        PaidInCash().execute() { result in
            switch result {
            case .success(_):
                self.onDone?(true)
                self.dismiss(animated: true, completion: nil)
            case .failure(let error):
                error.showAlert()
            }
            
        }
    }
    
    @objc private func requestRefresh() {
        animationView.play()
        GetCurrentRequestInfo().execute() { result in
            switch result {
            case .success(_):
                self.buttonCash.isEnabled = true
                
            case .failure(_):
                self.dismiss(animated: true, completion: nil)
            }
        }
    }
}

//
//  TripHistoryCollectionViewCell.swift
//  rider
//

import UIKit


class PromotionsCollectionViewCell: UICollectionViewCell {
    public var promotion: Promotion?
    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var textdescription: UILabel!
    @IBOutlet weak var background: GradientView!
    
    override func layoutSubviews() {
        super.layoutSubviews()
        if let pr = promotion {
            title.text = pr.title
            textdescription.text = pr.description
        }
    }
}

//
//  TripHistoryCollectionViewCell.swift
//  rider
//

import UIKit


class TripHistoryCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var pickupLabel: UILabel!
    @IBOutlet weak var startTimeLabel: UILabel!
    @IBOutlet weak var destinationLabel: UILabel!
    @IBOutlet weak var finishTimeLabel: UILabel!
    @IBOutlet weak var background: GradientView!
    @IBOutlet weak var textCost: UILabel!
    @IBOutlet weak var textStatus: UILabel!
}

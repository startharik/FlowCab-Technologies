
import UIKit


class AddressCell: UICollectionViewCell {
    @IBOutlet weak var textTitle: UILabel!
    var address:Address?
    weak var delegate:FavoriteAddressDialogDelegate?
    @IBOutlet weak var background: GradientView!
    @IBOutlet weak var textAddress: UILabel!
    
    @IBAction func onEditClicked(_ sender: Any) {
        delegate?.update(address: address!)
    }
    
    @IBAction func onDeleteClicked(_ sender: Any) {
        delegate?.delete(address: address!)
    }
    
}
protocol FavoriteAddressDialogDelegate: AnyObject {
    func delete(address:Address)
    func update(address:Address)
}

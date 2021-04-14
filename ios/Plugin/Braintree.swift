import Foundation
import Braintree
import BraintreeDropIn

@objc public class Braintree: NSObject {
    @objc public func echo(_ value: String) -> String {
        return value
    }

    @objc public func setToken(_ value: String) -> String {
        return value
    }
}

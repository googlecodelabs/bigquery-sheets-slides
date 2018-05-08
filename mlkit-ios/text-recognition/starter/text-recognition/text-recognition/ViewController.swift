//
//  Copyright (c) 2018 Google Inc.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import UIKit
import FirebaseMLVision

struct ImageDisplay {
  let file: String
  let name: String
}

class ViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {
  
  var frameSublayer = CALayer()
  
  @IBOutlet weak var imageView: UIImageView!
  @IBOutlet weak var pickerView: UIPickerView!
  
  let images = [
    ImageDisplay(file: "do-not-feed-birds", name: "Image 1"),
    ImageDisplay(file: "walk-on-grass", name: "Image 2"),
  ]
  
  override func viewDidLoad() {
    super.viewDidLoad()
    imageView.layer.addSublayer(frameSublayer)
    pickerView.dataSource = self
    pickerView.delegate = self
  }
  
  // MARK: Actions
  
  @IBAction func findTextDidTouch(_ sender: UIButton) {
    runTextRecognition(with: imageView.image!)
  }
  
  @IBAction func findTextCloudDidTouch(_ sender: UIButton) {
    runCloudTextRecognition(with: imageView.image!)
  }
  
  // MARK: Text Recognition
  
  func runTextRecognition(with image: UIImage) {

  }
  
  func runCloudTextRecognition(with image: UIImage) {

  }

  
  // MARK: Image Drawing
  
  func processResult(from text: [VisionText]?, error: Error?) {
    
  }

  
  func processCloudResult(from text: VisionCloudText?, error: Error?) {
    
  }

  
  /// Converts a feature frame to a frame UIView that is displayed over the image.
  ///
  /// - Parameters:
  ///   - featureFrame: The rect of the feature with the same scale as the original image.
  ///   - imageSize: The size of original image.
  ///   - viewRect: The view frame rect on the screen.
  private func addFrameView(featureFrame: CGRect, imageSize: CGSize, viewFrame: CGRect, text: String? = nil) {
    print("Frame: \(featureFrame).")
    
    let viewSize = viewFrame.size
    
    // Find resolution for the view and image
    let rView = viewSize.width / viewSize.height
    let rImage = imageSize.width / imageSize.height
    
    // Define scale based on comparing resolutions
    var scale: CGFloat
    if rView > rImage {
      scale = viewSize.height / imageSize.height
    } else {
      scale = viewSize.width / imageSize.width
    }
    
    // Calculate scaled feature frame size
    let featureWidthScaled = featureFrame.size.width * scale
    let featureHeightScaled = featureFrame.size.height * scale
    
    // Calculate scaled feature frame top-left point
    let imageWidthScaled = imageSize.width * scale
    let imageHeightScaled = imageSize.height * scale
    
    let imagePointXScaled = (viewSize.width - imageWidthScaled) / 2
    let imagePointYScaled = (viewSize.height - imageHeightScaled) / 2
    
    let featurePointXScaled = imagePointXScaled + featureFrame.origin.x * scale
    let featurePointYScaled = imagePointYScaled + featureFrame.origin.y * scale
    
    // Define a rect for scaled feature frame
    let featureRectScaled = CGRect(x: featurePointXScaled,
                                   y: featurePointYScaled,
                                   width: featureWidthScaled,
                                   height: featureHeightScaled)
    
    drawFrame(featureRectScaled, text: text)
  }
  
  /// Creates and draws a frame for the calculated rect as a sublayer.
  ///
  /// - Parameter rect: The rect to draw.
  private func drawFrame(_ rect: CGRect, text: String? = nil) {
    let bpath: UIBezierPath = UIBezierPath(rect: rect)
    let rectLayer: CAShapeLayer = CAShapeLayer()
    rectLayer.path = bpath.cgPath
    rectLayer.strokeColor = Constants.lineColor
    rectLayer.fillColor = Constants.fillColor
    rectLayer.lineWidth = Constants.lineWidth
    if let text = text {
      let textLayer = CATextLayer()
      textLayer.string = text
      textLayer.fontSize = 12.0
      textLayer.foregroundColor = Constants.lineColor
      let center = CGPoint(x: rect.midX, y: rect.midY)
      textLayer.position = center
      textLayer.frame = rect
      textLayer.alignmentMode = kCAAlignmentCenter
      textLayer.contentsScale = UIScreen.main.scale
      frameSublayer.addSublayer(textLayer)
    }
    frameSublayer.addSublayer(rectLayer)
  }
  
  private func removeFrames() {
    guard let sublayers = frameSublayer.sublayers else { return }
    for sublayer in sublayers {
      guard let frameLayer = sublayer as CALayer? else {
        print("Failed to remove frame layer.")
        continue
      }
      frameLayer.removeFromSuperlayer()
    }
  }
  
  func detectorOrientation(in image: UIImage) -> VisionDetectorImageOrientation {
    switch image.imageOrientation {
    case .up:
      return .topLeft
    case .down:
      return .bottomRight
    case .left:
      return .leftBottom
    case .right:
      return .rightTop
    case .upMirrored:
      return .topRight
    case .downMirrored:
      return .bottomLeft
    case .leftMirrored:
      return .leftTop
    case .rightMirrored:
      return .rightBottom
    }
  }
  
  // MARK: UIPickerViewDelegate, UIPickerViewDataSource
  
  func numberOfComponents(in pickerView: UIPickerView) -> Int {
    return 1
  }
  
  func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
    return images.count
  }
  
  func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
    return images[row].name
  }
  
  func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
    removeFrames()
    let imageDisplay = images[row]
    imageView.image = UIImage(named: imageDisplay.file)
  }
  
}

// MARK: - Fileprivate

fileprivate enum Constants {
  static let lineWidth: CGFloat = 3.0
  static let lineColor = UIColor.yellow.cgColor
  static let fillColor = UIColor.clear.cgColor
}

package ca.hccis.perfumeshop // Check your package name!

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

// The @OptIn is required because CameraX's image format is technically still in beta
@OptIn(ExperimentalGetImage::class)
class BarcodeAnalyzer(private val onBarcodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {

    // Boot up the Google ML Kit Scanner
    private val scanner = BarcodeScanning.getClient()

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            // Convert the camera frame into a Google ML Kit Image
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Search the image for barcodes
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { value ->
                            // WE FOUND ONE! Send the number back to our app.
                            onBarcodeScanned(value)
                        }
                    }
                }
                .addOnCompleteListener {
                    // We must close the frame so the camera can process the next one
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
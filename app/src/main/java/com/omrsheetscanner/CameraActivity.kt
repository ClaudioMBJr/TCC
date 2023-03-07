package com.omrsheetscanner

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.omrsheetscanner.databinding.ActivityCameraBinding
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


class CameraActivity : AppCompatActivity(),
    CameraBridgeViewBase.CvCameraViewListener2 {

    private lateinit var binding: ActivityCameraBinding

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted)
                activateOpenCVCameraView()
            else
                finish()
        }

    init {
        if (OpenCVLoader.initDebug())
            Log.d("OpenCV", "OpenCV initialized")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (cameraPermissionIsGranted())
            activateOpenCVCameraView()
        else
            requestPermission.launch(CAMERA)
    }

    private fun cameraPermissionIsGranted() =
        ContextCompat.checkSelfPermission(baseContext, CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun activateOpenCVCameraView() {
        binding.javaCameraView.apply {
            setCameraPermissionGranted()
            setCvCameraViewListener(this@CameraActivity)
            enableView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.javaCameraView.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
    }

    override fun onCameraViewStopped() {
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        val frame = inputFrame.rgba()

        // Convert the image to grayscale
        val grayMat = Mat()
        Imgproc.cvtColor(frame, grayMat, Imgproc.COLOR_BGR2GRAY)

        // Apply a Gaussian blur to reduce noise
        val blurredMat = Mat()
        val kSize = Size(5.0, 5.0)
        Imgproc.GaussianBlur(grayMat, blurredMat, kSize, 0.0)

        // Apply Canny edge detection to find edges
        val threshold1 = 50.0
        val threshold2 = 200.0
        val cannyMat = Mat()
        Imgproc.Canny(blurredMat, cannyMat, threshold1, threshold2)

        // Find contours in the image
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            cannyMat,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        // Filter the contours to find squares
        val squares = mutableListOf<MatOfPoint>()
        for (contour in contours) {
            val approxCurve = MatOfPoint2f()
            val epsilon = 0.02 * Imgproc.arcLength(MatOfPoint2f(*contour.toArray()), true)
            Imgproc.approxPolyDP(MatOfPoint2f(*contour.toArray()), approxCurve, epsilon, true)
            if (approxCurve.toArray().size == 4) {
                val rect = Imgproc.boundingRect(contour)
                val aspectRatio = rect.width.toDouble() / rect.height.toDouble()
                val x = rect.x
                val y = rect.y
                val w = rect.width
                val h = rect.height
                val centerX = x + w / 2
                val centerY = y + h / 2
                val maxDist =
                    minOf(centerX, centerY, frame.width() - centerX, frame.height() - centerY)
                if (aspectRatio in 0.9..1.1 && maxDist >= 50) {
                    squares.add(contour)
                }
            }
        }

        val bitmap = Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(frame, bitmap)

        if (squares.size == 4) {
            runOnUiThread {
                binding.javaCameraView.disableView()

                val builder = AlertDialog.Builder(this)
                    .create()
                val view = layoutInflater.inflate(R.layout.actvity_preview, null)
                builder.setView(view)

                view.findViewById<ImageView>(R.id.preview).setImageBitmap(bitmap)

                builder.setCanceledOnTouchOutside(true)

                builder.setOnCancelListener {
                    binding.javaCameraView.enableView()
                }
                builder.show()
            }
        }

        Imgproc.drawContours(frame, squares, -1, Scalar(0.0, 255.0, 0.0), 3)

        return frame
    }
}
package com.omrsheetscanner

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.omrsheetscanner.Constants.COUNTOUR_IDX
import com.omrsheetscanner.Constants.GREEN
import com.omrsheetscanner.Constants.MAX_RATIO
import com.omrsheetscanner.Constants.MIN_RATIO
import com.omrsheetscanner.Constants.PERCENT_OF_PERIMETER
import com.omrsheetscanner.Constants.SQUARE_POINTS
import com.omrsheetscanner.Constants.THICKNESS_BOX
import com.omrsheetscanner.databinding.ActivityCameraBinding
import java.io.File
import java.io.FileOutputStream
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.imgproc.Imgproc


class CameraActivity : AppCompatActivity(),
    CameraBridgeViewBase.CvCameraViewListener2 {

    private lateinit var binding: ActivityCameraBinding

    private var imageFound = false

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

        if (!imageFound) {

            val preProcessedFrame = preProcessFrame(frame)

            val contours = getContours(preProcessedFrame)

            val squares = findSquares(contours)

            Imgproc.drawContours(
                frame,
                squares,
                COUNTOUR_IDX,
                GREEN,
                THICKNESS_BOX
            )

            if (squares.size == 4) {
                imageFound = true

                val bitmap =
                    Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(frame, bitmap)

                val bitmapFile = File(applicationContext.cacheDir, Constants.FILE_NAME)
                val outputStream = FileOutputStream(bitmapFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                val intent = Intent(this, PreviewActivity::class.java)
                startActivity(intent)
            }
        }

        return frame
    }

    private fun getContours(preProcessedFrame: Mat): MutableList<MatOfPoint> {
        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(
            preProcessedFrame,
            contours,
            Mat(),
            Imgproc.RETR_LIST,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        return contours
    }

    private fun preProcessFrame(subFrame: Mat): Mat {
        val grayMat = Mat()
        Imgproc.cvtColor(subFrame, grayMat, Imgproc.COLOR_BGR2GRAY)

        val thresh = Mat()
        Imgproc.threshold(grayMat, thresh, 50.0, 255.0, Imgproc.THRESH_BINARY)

        val kernel = Mat.ones(5, 5, CvType.CV_8UC1)
        val closedImage = Mat()
        Imgproc.morphologyEx(thresh, closedImage, Imgproc.MORPH_CLOSE, kernel)

        return closedImage
    }

    private fun findSquares(
        contours: MutableList<MatOfPoint>
    ): MutableList<MatOfPoint> {
        val squares = mutableListOf<MatOfPoint>()

        for (contour in contours) {
            val matOfPoint2f = MatOfPoint2f()
            contour.convertTo(matOfPoint2f, CvType.CV_32F)

            val perimeter = Imgproc.arcLength(matOfPoint2f, true)

            val approxCurve = MatOfPoint2f()
            Imgproc.approxPolyDP(
                matOfPoint2f,
                approxCurve,
                PERCENT_OF_PERIMETER * perimeter,
                true
            )

            if (approxCurve.toArray().size == SQUARE_POINTS) {
                val boundingRect = Imgproc.boundingRect(contour)
                val aspectRatio = boundingRect.width.toDouble() / boundingRect.height.toDouble()

                if (aspectRatio in MIN_RATIO..MAX_RATIO && boundingRect.area() > 100)
                    squares.add(contour)
            }
        }

        return squares
    }

    override fun onResume() {
        super.onResume()
        imageFound = false
    }

}
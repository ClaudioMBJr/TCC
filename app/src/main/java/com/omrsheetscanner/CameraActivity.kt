package com.omrsheetscanner

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.omrsheetscanner.Constants.BLACK
import com.omrsheetscanner.Constants.BLUE
import com.omrsheetscanner.Constants.BOTTOM_Y
import com.omrsheetscanner.Constants.COUNTOUR_IDX
import com.omrsheetscanner.Constants.GAUSSIAN_KERNEL_SIZE
import com.omrsheetscanner.Constants.GAUSSIAN_SIGMA
import com.omrsheetscanner.Constants.HIGHT_BOX
import com.omrsheetscanner.Constants.LEFT_X
import com.omrsheetscanner.Constants.MAX_AREA
import com.omrsheetscanner.Constants.MAX_RATIO
import com.omrsheetscanner.Constants.MIN_AREA
import com.omrsheetscanner.Constants.MIN_RATIO
import com.omrsheetscanner.Constants.PERCENT_OF_PERIMETER
import com.omrsheetscanner.Constants.SQUARE_POINTS
import com.omrsheetscanner.Constants.THICKNESS_BOX
import com.omrsheetscanner.Constants.THRESHOLD_CANNY
import com.omrsheetscanner.Constants.TOP_RIGHT_X
import com.omrsheetscanner.Constants.WIDTH_BOX
import com.omrsheetscanner.databinding.ActivityCameraBinding
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.rectangle


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

        // Create 4 rectangles
        val quadrants = createQuadrants()

        //Draw each rectangle on the screen
        drawRectangles(frame, quadrants)

        for (q in quadrants) {

            //Get a subframe from the quadrant
            val subFrame = frame.submat(q)

            //Pre process frame
            val preProcessedFrame = preProcessFrame(subFrame)

            //Get contours in the frame
            val contours = getContours(preProcessedFrame)

            //Get squares
            val squares = findSquares(contours)

            //Draw a contour around the square
            Imgproc.drawContours(
                subFrame,
                squares,
                COUNTOUR_IDX,
                BLUE,
                THICKNESS_BOX
            )
        }

        return frame
    }

    private fun drawRectangles(frame: Mat, quadrants: Array<Rect>) {
        quadrants.forEach {
            rectangle(frame, it, BLACK, THICKNESS_BOX)
        }
    }

    private fun createQuadrants(): Array<Rect> {
        val q1 = Rect(TOP_RIGHT_X, LEFT_X, WIDTH_BOX, HIGHT_BOX)
        val q2 = Rect(TOP_RIGHT_X, TOP_RIGHT_X, WIDTH_BOX, HIGHT_BOX)
        val q3 = Rect(BOTTOM_Y, TOP_RIGHT_X, WIDTH_BOX, HIGHT_BOX)
        val q4 = Rect(BOTTOM_Y, LEFT_X, WIDTH_BOX, HIGHT_BOX)
        return arrayOf(q1, q2, q3, q4)
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
        // Convert the image to grayscale
        val grayMat = Mat()
        Imgproc.cvtColor(subFrame, grayMat, Imgproc.COLOR_BGR2GRAY)

        // Apply a Gaussian blur to reduce noise
        val blurredMat = Mat()
        Imgproc.GaussianBlur(grayMat, blurredMat, GAUSSIAN_KERNEL_SIZE, GAUSSIAN_SIGMA)

        // Apply Canny edge detection to find edges
        val cannyMat = Mat()
        Imgproc.Canny(blurredMat, cannyMat, THRESHOLD_CANNY, THRESHOLD_CANNY)
        return cannyMat
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
            Imgproc.approxPolyDP(matOfPoint2f, approxCurve, PERCENT_OF_PERIMETER * perimeter, true)

            if (approxCurve.toArray().size == SQUARE_POINTS) {
                val boundingRect = Imgproc.boundingRect(contour)
                val aspectRatio = boundingRect.width.toDouble() / boundingRect.height.toDouble()
                val area = boundingRect.area()

                if (aspectRatio in MIN_RATIO..MAX_RATIO && area in MIN_AREA..MAX_AREA) {
                    squares.add(contour)
                }
            }
        }
        return squares
    }
}
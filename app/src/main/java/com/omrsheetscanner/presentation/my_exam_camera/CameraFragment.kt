package com.omrsheetscanner.presentation.my_exam_camera

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.omrsheetscanner.common.Constants
import com.omrsheetscanner.common.Constants.COUNTOUR_IDX
import com.omrsheetscanner.common.Constants.GREEN
import com.omrsheetscanner.common.Constants.PERCENT_OF_PERIMETER
import com.omrsheetscanner.common.Constants.SQUARE_POINTS
import com.omrsheetscanner.common.Constants.THICKNESS_BOX
import com.omrsheetscanner.databinding.FragmentCameraBinding
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream

class CameraFragment : Fragment(),
    CameraBridgeViewBase.CvCameraViewListener2 {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private val args: CameraFragmentArgs by navArgs()

    private var imageFound = false

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted)
                activateOpenCVCameraView()
            else
                requireActivity().finish()
        }

    init {
        if (OpenCVLoader.initDebug())
            Log.d("OpenCV", "OpenCV initialized")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        if (cameraPermissionIsGranted())
            activateOpenCVCameraView()
        else
            requestPermission.launch(CAMERA)
    }

    private fun cameraPermissionIsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun activateOpenCVCameraView() {
        binding.javaCameraView.apply {
            setCameraPermissionGranted()
            setCvCameraViewListener(this@CameraFragment)
            setMaxFrameSize(1280, 720)
            enableView()
        }
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

                val scrRects = mutableListOf<Rect>().apply {
                    add(Imgproc.boundingRect(squares[0]))
                    add(Imgproc.boundingRect(squares[1]))
                    add(Imgproc.boundingRect(squares[2]))
                    add(Imgproc.boundingRect(squares[3]))
                }.sortedBy { it.x + it.y }

                val topLeft = scrRects.first().br()
                val bottomRight = scrRects.last().tl()

                val bottomLeft = Point(
                    topLeft.x,
                    bottomRight.y
                )

                val topRight = Point(
                    bottomRight.x,
                    topLeft.y
                )

                Log.d("Points", "topLeft x = ${topLeft.x} y = ${topLeft.y}")
                Log.d("Points", "bottomLeft x = ${bottomLeft.x} y = ${bottomLeft.y}")
                Log.d("Points", "topRight x = ${topRight.x} y = ${topRight.y}")
                Log.d("Points", "bottomRight x = ${bottomRight.x} y = ${bottomRight.y}")

                val rect = Rect(topLeft, bottomRight)
                val region = frame.submat(rect)

                val srcPoints = MatOfPoint2f(
                    topLeft,
                    bottomLeft,
                    topRight,
                    bottomRight
                )

                val dstPoints = MatOfPoint2f(
                    Point(0.0, 0.0),
                    Point(0.0, frame.height().toDouble()),
                    Point(frame.width().toDouble(), 0.0),
                    Point(frame.width().toDouble(), frame.height().toDouble()),
                )

                val transformationMatrix = Imgproc.getPerspectiveTransform(
                    srcPoints,
                    dstPoints
                )
                val size = frame.size()

                val outputImage = Mat()
                Imgproc.warpPerspective(
                    frame,
                    outputImage,
                    transformationMatrix,
                    size
                )

                createBitmap(outputImage)

                imageFound = true

                findNavController().navigate(
                    CameraFragmentDirections.actionCameraFragmentToPreviewFragment(
                        args.myExam
                    )
                )
            }
        }

        return frame
    }

    private fun createBitmap(mat: Mat) {
        val bitmap =
            Bitmap.createBitmap(
                mat.cols(),
                mat.rows(),
                Bitmap.Config.ARGB_8888
            )
        Utils.matToBitmap(mat, bitmap)

        val bitmapFile = File(requireActivity().applicationContext.cacheDir, Constants.FILE_NAME)
        val outputStream = FileOutputStream(bitmapFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
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
        Imgproc.threshold(grayMat, thresh, 100.0, 255.0, Imgproc.THRESH_BINARY)

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

                if (aspectRatio in 0.5..0.7 && boundingRect.area() > 8000)
                    squares.add(contour)
            }
        }

        return squares
    }

    override fun onResume() {
        super.onResume()
        imageFound = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.javaCameraView.disableView()
        _binding = null
    }

}
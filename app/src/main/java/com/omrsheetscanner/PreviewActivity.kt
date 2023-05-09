package com.omrsheetscanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.omrsheetscanner.Constants.BLUE
import com.omrsheetscanner.Constants.GREEN
import com.omrsheetscanner.Constants.RED
import com.omrsheetscanner.Constants.YELLOW
import com.omrsheetscanner.databinding.ActvityPreviewBinding
import java.io.File
import java.io.FileInputStream
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.imgproc.Imgproc

class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActvityPreviewBinding

    private val row = 10
    private val col = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActvityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val bitmapFile = File(applicationContext.cacheDir, Constants.FILE_NAME)
            val inputStream = FileInputStream(bitmapFile)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)

            val gray = preProcessFrame(mat)

            val contours = getContours(gray)

            val bubbles = mutableListOf<MatOfPoint>()

            contours.forEach {
                val boundingRect = Imgproc.boundingRect(it)
                val aspectRatio = boundingRect.width.toDouble() / boundingRect.height.toDouble()

                if (boundingRect.area() > 800.0 && aspectRatio in 0.9..1.2)
                    bubbles.add(it)
            }

            val lines = bubbles.sortedBy { Imgproc.boundingRect(it).y }.chunked(20)

            val questions = mutableListOf<List<MatOfPoint>>()

            lines.forEach { line ->
                val sectorByLine = line.sortedBy { Imgproc.boundingRect(it).x }.chunked(5)
                sectorByLine.forEach { question ->
                    questions.add(question)
                }
            }

//            for (i in (col - 1) downTo 0) {
//                for (j in 0 until row) {
//                    sortedRegions.add(regions[(j * col) + i])
//                }
//            }

            questions.forEachIndexed { index, matOfPoints ->
                val color = when (index % 4) {
                    0 -> GREEN
                    1 -> BLUE
                    2 -> RED
                    else -> YELLOW
                }

                Imgproc.drawContours(
                    mat,
                    matOfPoints,
                    Constants.COUNTOUR_IDX,
                    color,
                    Constants.THICKNESS_BOX
                )
            }

            val processedBitmap = getFinalBitMap(mat)

            binding.preview.setImageBitmap(processedBitmap)

        } catch (e: Exception) {
            e.printStackTrace()
            AlertDialog.Builder(this)
                .setMessage("Imagem inválida")
                .setPositiveButton(
                    "OK"
                ) { _, _ -> finish() }
                .create()
                .show()
        }
    }

    private fun getFinalBitMap(mat: Mat): Bitmap? {
        val processedBitmap =
            Bitmap.createBitmap(
                mat.cols(),
                mat.rows(),
                Bitmap.Config.ARGB_8888
            )

        Utils.matToBitmap(mat, processedBitmap)
        return processedBitmap
    }

    private fun preProcessFrame(subFrame: Mat): Mat {
        val grayMat = Mat()
        Imgproc.cvtColor(subFrame, grayMat, Imgproc.COLOR_BGR2GRAY)

        val thresh = Mat()
        Imgproc.threshold(
            grayMat,
            thresh,
            100.0,
            255.0,
            (Imgproc.THRESH_BINARY_INV or Imgproc.THRESH_OTSU)
        )

        return thresh
    }

    private fun getContours(preProcessedFrame: Mat): MutableList<MatOfPoint> {
        val contours = mutableListOf<MatOfPoint>()

        Imgproc.findContours(
            preProcessedFrame,
            contours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        return contours
    }


    override fun onDestroy() {
        super.onDestroy()
        applicationContext.cacheDir.listFiles()?.forEach { it.delete() }
    }

}
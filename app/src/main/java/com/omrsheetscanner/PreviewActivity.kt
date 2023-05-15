package com.omrsheetscanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.omrsheetscanner.Constants.GREEN
import com.omrsheetscanner.databinding.ActvityPreviewBinding
import java.io.File
import java.io.FileInputStream
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.drawContours


class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActvityPreviewBinding

    private val row = 10
    private val col = 4
    private val marksByLine = 20
    private val marksByQuestion = 5

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

            //Cria uma lista com cada linha
            val lines = bubbles.sortedBy { Imgproc.boundingRect(it).y }.chunked(marksByLine)

            val questions = mutableListOf<List<MatOfPoint>>()

            //Adiciona na lista questions agrupamentos de 5 retirados de cada linha
            lines.forEach { line ->
                val lineBySector =
                    line.sortedBy { Imgproc.boundingRect(it).x }.chunked(marksByQuestion)
                lineBySector.forEach { question ->
                    questions.add(question)
                }
            }

            val sortedQuestions = mutableListOf<List<MatOfPoint>>()

            //Ordena a matrix
            for (i in 0 until col) {
                for (j in 0 until row) {
                    sortedQuestions.add(questions[(j * col) + i])
                }
            }

            val userAnswer = mutableListOf<MatOfPoint>()

            sortedQuestions.forEachIndexed { indexQuestion, questions ->
                val filledMarks = mutableListOf<MatOfPoint>()
                questions.forEachIndexed { index, mark ->

                    if (filledMarks.size > 1) {
                        filledMarks.clear()
                        return
                    }

                    val markContour = mutableListOf(mark)

                    val mask = Mat.zeros(gray.size(), CvType.CV_8UC1)
                    drawContours(mask, markContour, -1, Scalar(255.0), -1)

                    val maskedImage = Mat()
                    Core.bitwise_and(gray, mask, maskedImage)

                    val total = Core.countNonZero(maskedImage)

                    Log.d(
                        "Avaliação",
                        "Questão ${indexQuestion + 1} opção ${index + 1} contagem de pixels diferente de 0 = $total"
                    )

                    if (total > 800)
                        filledMarks.add(mark)

                }
                if (filledMarks.isNotEmpty()) {
                    userAnswer.add(filledMarks.first())
                    filledMarks.clear()
                }
            }

            drawContours(
                mat,
                userAnswer,
                Constants.COUNTOUR_IDX,
                GREEN,
                Constants.THICKNESS_BOX
            )

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
//        applicationContext.cacheDir.listFiles()?.forEach { it.delete() }
    }

}
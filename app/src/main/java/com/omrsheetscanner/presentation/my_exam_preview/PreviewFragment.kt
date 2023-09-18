package com.omrsheetscanner.presentation.my_exam_preview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.omrsheetscanner.common.Constants
import com.omrsheetscanner.common.Constants.GREEN
import com.omrsheetscanner.common.Constants.RED
import com.omrsheetscanner.databinding.FragmentPreviewBinding
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.drawContours
import java.io.File
import java.io.FileInputStream

class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    private val args: PreviewFragmentArgs by navArgs()

    private val row = 10
    private val col = args.myExam.getColumns()
    private val totalQuestions = args.myExam.questions
    private val marksByQuestion = args.myExam.options
    private val correctAnswer = args.myExam.examAnswers
    private val marksByLine = marksByQuestion * col

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reviewExam()

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun reviewExam() {
        try {
            val bitmapFile =
                File(requireActivity().applicationContext.cacheDir, Constants.FILE_NAME)
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

            if (questions.size != totalQuestions)
                throw Exception()

            val sortedQuestions = mutableListOf<List<MatOfPoint>>()

            //Ordena a matrix
            for (i in 0 until col) {
                for (j in 0 until row) {
                    sortedQuestions.add(questions[(j * col) + i])
                }
            }

            val userCorrectAnswer = mutableListOf<MatOfPoint>()
            val userWrongAnswer = mutableListOf<MatOfPoint>()

            sortedQuestions.forEachIndexed { indexQuestion, question ->
                var userAnswer: MatOfPoint? = null
                kotlin.run breaker@{
                    question.forEachIndexed { index, mark ->
                        val markContour = mutableListOf(mark)

                        val mask = Mat.zeros(gray.size(), CvType.CV_8UC1)
                        drawContours(mask, markContour, -1, Scalar(255.0), -1)

                        val maskedImage = Mat()
                        Core.bitwise_and(gray, mask, maskedImage)

                        val total = Core.countNonZero(maskedImage)

                        if (total > 800) {
                            if (userAnswer == null) {
                                userAnswer = mark
                                if (index == correctAnswer[indexQuestion])
                                    userCorrectAnswer.add(mark)
                                else userWrongAnswer.add(mark)
                            } else {
                                question.forEach {
                                    userCorrectAnswer.remove(it)
                                    userWrongAnswer.remove(it)
                                }
                                return@breaker
                            }
                        }
                    }
                }
                userAnswer = null
            }

            drawContours(
                mat,
                userCorrectAnswer,
                Constants.COUNTOUR_IDX,
                GREEN,
                Constants.THICKNESS_BOX
            )

            drawContours(
                mat,
                userWrongAnswer,
                Constants.COUNTOUR_IDX,
                RED,
                Constants.THICKNESS_BOX
            )

            val processedBitmap = getFinalBitMap(mat)

            binding.preview.setImageBitmap(processedBitmap)

        } catch (e: Exception) {
            e.printStackTrace()
            AlertDialog.Builder(requireContext())
                .setMessage("Imagem inválida")
                .setPositiveButton(
                    "OK"
                ) { _, _ -> requireActivity().finish() }
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


    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().applicationContext.cacheDir.listFiles()?.forEach { it.delete() }
        _binding = null
    }

}
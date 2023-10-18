package com.omrsheetscanner.presentation.my_exam_preview

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.omrsheetscanner.common.Constants
import com.omrsheetscanner.common.Constants.GREEN
import com.omrsheetscanner.common.Constants.RED
import com.omrsheetscanner.common.MatConverter
import com.omrsheetscanner.databinding.FragmentPreviewBinding
import com.omrsheetscanner.domain.model.StudentGrade
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.drawContours

class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    private val args: PreviewFragmentArgs by navArgs()

    private var studentGrade : Double = 0.0

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

        binding.btnSave.setOnClickListener {
            findNavController().navigate(PreviewFragmentDirections.actionPreviewFragmentToSaveEditStudentFragment(StudentGrade(name = "", grade = studentGrade.toString())))
        }
    }

    private fun reviewExam() {
        val row = args.myExam.getRows()
        val col = args.myExam.getColumns()
        val totalQuestions = args.myExam.questions
        val marksByQuestion = args.myExam.options
        val correctAnswer = args.myExam.examAnswers
        val marksByLine = marksByQuestion * col

        try {
            val tempImg = MatConverter.matFromJson(args.matJson)
            val mat = tempImg.clone()

            binding.preview.setImageBitmap(getFinalBitMap(mat))

            val gray = preProcessFrame(mat)

            val contours = getContours(gray)

            val bubbles = mutableListOf<MatOfPoint>()

            contours.forEach {
                val boundingRect = Imgproc.boundingRect(it)
                val aspectRatio = boundingRect.width.toDouble() / boundingRect.height.toDouble()

                if (aspectRatio > 1.35) {
                    if (boundingRect.area() in 1000.0..1600.0) {
                        bubbles.add(it)
                    }
                }
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

            if (questions.size != totalQuestions) {
                Log.d("TAG questionSize", questions.size.toString())

                throw Exception()
            }

            val sortedQuestions = mutableListOf<List<MatOfPoint>>()

            //Ordena a matrix
            for (i in 0..<col) {
                for (j in 0 until row) {
                    sortedQuestions.add(questions[(j * col) + i])
                }
            }

            Log.d("TAG sortedQuestion", sortedQuestions.size.toString())

            val userCorrectAnswer = mutableListOf<MatOfPoint>()
            val userWrongAnswer = mutableListOf<MatOfPoint>()

            sortedQuestions.forEachIndexed { indexQuestion, question ->
                var userAnswer: MatOfPoint? = null
                kotlin.run breaker@{
                    question.forEachIndexed { index, mark ->
                        val correctIndex = index + 1
                        val markContour = mutableListOf(mark)

                        val mask = Mat.zeros(gray.size(), CvType.CV_8UC1)
                        drawContours(mask, markContour, -1, Scalar(255.0), -1)

                        val maskedImage = Mat()
                        Core.bitwise_and(gray, mask, maskedImage)

                        val total = Core.countNonZero(maskedImage)

                        if (total > 800) {
                            if (userAnswer == null) {
                                userAnswer = mark
                                if (correctIndex == correctAnswer[indexQuestion]) {
                                    userCorrectAnswer.add(mark)
                                } else {
                                    userWrongAnswer.add(mark)
                                }
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

            studentGrade = ((userCorrectAnswer.size * 100) / args.myExam.maxScore).toDouble()

            binding.preview.setImageBitmap(processedBitmap)
            binding.progress.isVisible = false

        } catch (e: Exception) {
            findNavController().popBackStack()
            e.printStackTrace()
            Toast.makeText(requireContext(), "Não foi possível processar a imagem", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFinalBitMap(mat: Mat): Bitmap {
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
        _binding = null
    }

}
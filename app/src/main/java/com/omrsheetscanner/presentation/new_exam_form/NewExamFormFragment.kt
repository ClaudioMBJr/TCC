package com.omrsheetscanner.presentation.new_exam_form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.omrsheetscanner.R
import com.omrsheetscanner.databinding.FragmentMyExamFormBinding
import com.omrsheetscanner.domain.model.MyExam

class NewExamFormFragment : Fragment() {

    private var _binding: FragmentMyExamFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyExamFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnContinue.setOnClickListener {
            if (canContinue()) {
                val myExam = MyExam(
                    title = binding.examNameEditText.text.toString(),
                    description = binding.examDescriptionEditText.text.toString(),
                    options = binding.spinnerAmountOptions.selectedItem.toString().toInt(),
                    questions = binding.spinnerAmountQuestions.selectedItem.toString().toInt(),
                    maxScore = binding.examMaxScoreEditText.text.toString().toInt()
                )

                findNavController().navigate(
                    NewExamFormFragmentDirections.actionNewExamFormFragmentToSelectCorrectAnswersFragment(
                        myExam
                    )
                )
            }
        }
    }

    private fun canContinue(): Boolean {
        val maxScore = binding.examMaxScoreEditText.text.toString().toIntOrNull() ?: 0

        when {
            binding.examNameEditText.text.isEmpty() -> showWarning(getString(R.string.text_warning_fill_exam_title))
            maxScore == 0 -> showWarning(getString(R.string.text_warning_invalid_score))
            else -> return true
        }
        return false
    }

    private fun showWarning(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.omrsheetscanner.new_exam_form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.omrsheetscanner.R
import com.omrsheetscanner.common.Exam
import com.omrsheetscanner.databinding.FragmentMyExamFormBinding

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

        binding.btnContinue.setOnClickListener {
            if (canContinue())
                Exam(
                    title = binding.examNameEditText.text.toString(),
                    description = binding.examDescriptionEditText.text.toString(),
                    options = binding.spinnerAmountOptions.selectedItem.toString().toInt(),
                    questions = binding.spinnerAmountQuestions.selectedItem.toString().toInt(),
                    maxScore = binding.examMaxScoreEditText.toString().toInt()
                )
        }
    }

    private fun canContinue(): Boolean {
        when {
            binding.examNameEditText.text.isEmpty() -> showWarning(getString(R.string.text_warning_fill_exam_title))
            binding.examMaxScoreEditText.text.toString() == getString(R.string.text_invalid_max_score) -> showWarning(
                getString(R.string.text_warning_invalid_score)
            )

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
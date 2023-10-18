package com.omrsheetscanner.presentation.select_correct_answers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.omrsheetscanner.R
import com.omrsheetscanner.databinding.FragmentSelectCorrectAnswersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectCorrectAnswersFragment : Fragment() {

    private var _binding: FragmentSelectCorrectAnswersBinding? = null
    private val binding get() = _binding!!

    private val args: SelectCorrectAnswersFragmentArgs by navArgs()

    private val selectCorrectAnswersViewModel: SelectCorrectAnswersViewModel by viewModels()

    private lateinit var adapter: SelectCorrectAnswersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectCorrectAnswersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = SelectCorrectAnswersAdapter(
            amountOfQuestions = args.myExam.questions,
            amountOfOptions = args.myExam.options
        )

        binding.answersRv.adapter = adapter

        binding.btnContinue.setOnClickListener {
            if (canContinue()) {
                selectCorrectAnswersViewModel.saveExam(args.myExam.copy(examAnswers = adapter.selectedAnswers))
                findNavController().navigate(SelectCorrectAnswersFragmentDirections.actionSelectCorrectAnswersFragmentToMyExamsFragment())
            }
        }
    }

    private fun canContinue(): Boolean {
        return if (adapter.selectedAnswers.contains(0)) {
            showWarning(getString(R.string.text_warning_all_options_must_be_chosen))
            false
        } else {
            true
        }
    }

    private fun showWarning(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
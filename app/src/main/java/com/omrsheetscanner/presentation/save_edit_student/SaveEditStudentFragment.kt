package com.omrsheetscanner.presentation.save_edit_student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.omrsheetscanner.databinding.FragmentSaveEditStudentBinding
import com.omrsheetscanner.domain.model.StudentGrade
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveEditStudentFragment : Fragment() {

    private var _binding: FragmentSaveEditStudentBinding? = null
    private val binding get() = _binding!!

    private val args: SaveEditStudentFragmentArgs by navArgs()

    private val saveEditStudentViewModel: SaveEditStudentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveEditStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.studentNameEdit.setText(args.student.name)
        binding.studentGradeEdit.setText(args.student.grade)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            saveEditStudentViewModel.saveStudent(
                StudentGrade(name = args.student.name, grade = args.student.grade)
            )
        }
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
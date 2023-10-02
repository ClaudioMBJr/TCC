package com.omrsheetscanner.presentation.my_exam_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.omrsheetscanner.databinding.FragmentMyExamInfoBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyExamInfoFragment : Fragment() {

    private var _binding: FragmentMyExamInfoBinding? = null
    private val binding get() = _binding!!

    private val myExamsInfoViewModel: MyExamInfoViewModel by viewModels()

    private val args: MyExamInfoFragmentArgs by navArgs()

    private var adapter: MyExamInfoAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyExamInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.titleExam.text = args.myExam.title
        binding.descriptionExam.text = args.myExam.description

        binding.btnReviewExam.setOnClickListener {
            findNavController().navigate(
                MyExamInfoFragmentDirections.actionMyExamInfoFragmentToCameraFragment(
                    args.myExam
                )
            )
        }

        binding.btnDeleteExam.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Excluir prova")
                .setMessage("Tem certeza que deseja excluir a avaliação?")
                .setPositiveButton(
                    "Excluir"
                ) { _, _ ->
                    myExamsInfoViewModel.deleteExam(args.myExam.id)
                    findNavController().popBackStack()
                }
                .setNegativeButton("Cancelar") { _, _ -> }
                .setCancelable(true)
                .create()
                .show()
        }

        binding.btnSaveExam.setOnClickListener {
            convertXmlToPdf()
        }

        observerStudentsList()
        configureSearchView()
    }

    private fun observerStudentsList() {
        myExamsInfoViewModel.students.observe(viewLifecycleOwner) {
            adapter = MyExamInfoAdapter(
                students = it.toMutableList(),
                onStudentClicked = {},
                showNoResultsMessage = {
                    showNoResultsLabel(it)
                })
            binding.studentsRv.adapter = adapter
            binding.textNoStudents.isVisible = it.isEmpty()
        }
    }

    private fun configureSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(newText: String?) {
        adapter?.getFilter()?.filter(newText)
    }

    private fun showNoResultsLabel(isVisible: Boolean) {
        binding.textNoStudentsSearch.isVisible = isVisible
    }

    private fun convertXmlToPdf() {
        PDFConverter().createPdf(requireContext(), requireActivity(), args.myExam)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
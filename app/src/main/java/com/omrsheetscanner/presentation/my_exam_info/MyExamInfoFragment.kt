package com.omrsheetscanner.presentation.my_exam_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.omrsheetscanner.databinding.FragmentMyExamInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyExamInfoFragment : Fragment() {

    private var _binding: FragmentMyExamInfoBinding? = null
    private val binding get() = _binding!!

    private val myExamsInfoViewModel: MyExamInfoViewModel by viewModels()

    private val args: MyExamInfoFragmentArgs by navArgs()

    private lateinit var adapter: MyExamInfoAdapter

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
        adapter.getFilter().filter(newText)
    }

    private fun showNoResultsLabel(isVisible: Boolean) {
        binding.textNoStudentsSearch.isVisible = isVisible
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
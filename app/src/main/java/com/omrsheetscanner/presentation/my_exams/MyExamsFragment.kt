package com.omrsheetscanner.presentation.my_exams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.omrsheetscanner.databinding.FragmentMyExamsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyExamsFragment : Fragment() {

    private var _binding: FragmentMyExamsBinding? = null
    private val binding get() = _binding!!

    private val myExamsViewModel: MyExamsViewModel by viewModels()

    private var adapter: MyExamsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyExamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        observerMyExamsList()
        configureSearchView()
    }

    private fun observerMyExamsList() {
        myExamsViewModel.myExams.observe(viewLifecycleOwner) {
            adapter = MyExamsAdapter(
                exams = it.toMutableList(),
                onExamClicked = {
                    findNavController().navigate(MyExamsFragmentDirections.actionMyExamsFragmentToMyExamInfoFragment(it))
                },
                showNoResultsMessage = {
                    showNoResultsLabel(it)
                })
            binding.examsRv.adapter = adapter
            binding.textNoExams.isVisible = it.isEmpty()
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
        binding.textNoExamsSearch.isVisible = isVisible
    }

    override fun onResume() {
        super.onResume()
        myExamsViewModel.getAllExams()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
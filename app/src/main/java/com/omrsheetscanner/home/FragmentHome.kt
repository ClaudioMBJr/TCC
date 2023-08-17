package com.omrsheetscanner.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.omrsheetscanner.databinding.FragmentHomeBinding

class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreateNewExam.setOnClickListener {
            findNavController().navigate(FragmentHomeDirections.actionFragmentHomeToNewExamFormFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
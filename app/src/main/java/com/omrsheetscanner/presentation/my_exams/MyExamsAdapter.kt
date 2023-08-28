package com.omrsheetscanner.presentation.my_exams

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omrsheetscanner.R
import com.omrsheetscanner.domain.model.MyExam

class MyExamsAdapter(
    private val exams: MutableList<MyExam>,
    private val onExamClicked: (MyExam) -> Unit,
    private val showNoResultsMessage : (Boolean) -> Unit
) : RecyclerView.Adapter<MyExamsAdapter.ViewHolder>() {

    private val allExams = mutableListOf<MyExam>().apply {
        addAll(exams)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val examTitle: TextView
        val examDescription: TextView

        init {
            examTitle = view.findViewById(R.id.examTitle)
            examDescription = view.findViewById(R.id.examDescription)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_exam, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.examTitle.text = exams[position].title
        viewHolder.examDescription.text = exams[position].description

        viewHolder.itemView.setOnClickListener {
            onExamClicked(exams[position])
        }
    }

    override fun getItemCount(): Int = exams.size

    fun getFilter(): Filter = examsFilter

    private val examsFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<MyExam> = mutableListOf()
            if (constraint.isNullOrEmpty()) {
                filteredList.addAll(allExams)
            } else {
                val query = constraint.toString().trim().lowercase()
                val filteredExams = allExams.filter {
                    it.title.trim().lowercase().contains(query)
                }
                filteredList.addAll(filteredExams)
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results == null)
                return

            (results.values as MutableList<MyExam>).let { list ->
                showNoResultsMessage(list.isEmpty())
                exams.clearAndAdd(list)
                notifyDataSetChanged()
            }
        }
    }

    fun <T> MutableList<T>.clearAndAdd(newList: MutableList<T>) {
        clear()
        addAll(newList)
    }
}
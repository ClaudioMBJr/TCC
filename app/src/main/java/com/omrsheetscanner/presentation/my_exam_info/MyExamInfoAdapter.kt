package com.omrsheetscanner.presentation.my_exam_info

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omrsheetscanner.R
import com.omrsheetscanner.domain.model.StudentGrade

class MyExamInfoAdapter(
    private val students: MutableList<StudentGrade>,
    private val onStudentClicked: (StudentGrade) -> Unit,
    private val showNoResultsMessage: (Boolean) -> Unit
) : RecyclerView.Adapter<MyExamInfoAdapter.ViewHolder>() {

    private val allStudents = mutableListOf<StudentGrade>().apply {
        addAll(students)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val studentName: TextView
        val studentGrade: TextView

        init {
            studentName = view.findViewById(R.id.studentName)
            studentGrade = view.findViewById(R.id.studentGrade)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_student, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.studentName.text = students[position].name
        viewHolder.studentGrade.text = students[position].grade

        viewHolder.itemView.setOnClickListener {
            onStudentClicked(students[position])
        }
    }

    override fun getItemCount(): Int = students.size

    fun getFilter(): Filter = examsFilter

    private val examsFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<StudentGrade> = mutableListOf()
            if (constraint.isNullOrEmpty()) {
                filteredList.addAll(allStudents)
            } else {
                val query = constraint.toString().trim().lowercase()
                val filteredExams = allStudents.filter {
                    it.name.trim().lowercase().contains(query)
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

            (results.values as MutableList<StudentGrade>).let { list ->
                showNoResultsMessage(list.isEmpty())
                students.clearAndAdd(list)
                notifyDataSetChanged()
            }
        }
    }

    fun <T> MutableList<T>.clearAndAdd(newList: MutableList<T>) {
        clear()
        addAll(newList)
    }
}
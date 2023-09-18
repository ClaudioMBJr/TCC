package com.omrsheetscanner.presentation.my_exam_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omrsheetscanner.R
import com.omrsheetscanner.domain.model.MyExam

class AnswersAdapter(private val myExam: MyExam) :
    RecyclerView.Adapter<AnswersAdapter.AnswersViewHolder>() {

    class AnswersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionNumber: TextView
        val option01: View
        val option02: View
        val option03: View
        val option04: View
        val option05: View

        init {
            questionNumber = view.findViewById(R.id.questionNumber)
            option01 = view.findViewById(R.id.option01)
            option02 = view.findViewById(R.id.option02)
            option03 = view.findViewById(R.id.option03)
            option04 = view.findViewById(R.id.option04)
            option05 = view.findViewById(R.id.option05)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_answer_px, parent, false)
        return AnswersViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswersViewHolder, position: Int) {
        setAmountOfOptions(holder)
        holder.questionNumber.setQuestionNumber(position)
    }

    private fun setAmountOfOptions(viewHolder: AnswersViewHolder) {
        when (myExam.options) {
            2 -> {
                viewHolder.option05.visibility = View.GONE
                viewHolder.option04.visibility = View.GONE
                viewHolder.option03.visibility = View.GONE
            }

            3 -> {
                viewHolder.option05.visibility = View.GONE
                viewHolder.option04.visibility = View.GONE
            }

            4 -> {
                viewHolder.option05.visibility = View.GONE
            }

            else -> Unit
        }
    }

    private fun TextView.setQuestionNumber(position: Int) {
        text = context.getString(R.string.text_question_number, (position + 1).toString())
    }

    override fun getItemCount(): Int {
        return myExam.questions
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
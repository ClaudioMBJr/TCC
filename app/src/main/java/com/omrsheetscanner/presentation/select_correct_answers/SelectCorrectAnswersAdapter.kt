package com.omrsheetscanner.presentation.select_correct_answers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.omrsheetscanner.R

class SelectCorrectAnswersAdapter(
    private val amountOfQuestions: Int,
    private val amountOfOptions: Int
) : RecyclerView.Adapter<SelectCorrectAnswersAdapter.ViewHolder>() {

    val selectedAnswers = MutableList(amountOfQuestions) { 0 }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_select_corret_answer, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        setAmountOfOptions(viewHolder)
        viewHolder.questionNumber.setQuestionNumber(position)
        viewHolder.option01.selectedQuestion(viewHolder, position, 1)
        viewHolder.option02.selectedQuestion(viewHolder, position, 2)
        viewHolder.option03.selectedQuestion(viewHolder, position, 3)
        viewHolder.option04.selectedQuestion(viewHolder, position, 4)
        viewHolder.option05.selectedQuestion(viewHolder, position, 5)
    }

    private fun setAmountOfOptions(viewHolder: ViewHolder) {
        when (amountOfOptions) {
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
        val realPosition = position + 1
        text = context.getString(R.string.text_question_number, realPosition.toString())
    }

    private fun View.selectedQuestion(viewHolder: ViewHolder, position: Int, correctAnswer: Int) {
        setOnClickListener {
            unselectPreviousQuestions(viewHolder, position)
            background = AppCompatResources.getDrawable(context, R.drawable.black_circle_shape)
            selectedAnswers[position] = correctAnswer
        }
    }

    private fun View.unselectPreviousQuestions(viewHolder: ViewHolder, position: Int) {
        when (selectedAnswers[position]) {
            1 -> viewHolder.option01.background =
                AppCompatResources.getDrawable(context, R.drawable.circle_shape)

            2 -> viewHolder.option02.background =
                AppCompatResources.getDrawable(context, R.drawable.circle_shape)

            3 -> viewHolder.option03.background =
                AppCompatResources.getDrawable(context, R.drawable.circle_shape)

            4 -> viewHolder.option04.background =
                AppCompatResources.getDrawable(context, R.drawable.circle_shape)

            5 -> viewHolder.option05.background =
                AppCompatResources.getDrawable(context, R.drawable.circle_shape)

            else -> return
        }

        selectedAnswers[position] = 0
    }

    override fun getItemCount(): Int = amountOfQuestions

    override fun getItemViewType(position: Int): Int {
        return position
    }

}
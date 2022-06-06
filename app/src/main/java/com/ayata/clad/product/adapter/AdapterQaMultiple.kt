package com.ayata.clad.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerQuestionDividerBinding
import com.ayata.clad.databinding.ItemRecyclerQuestionTypeAnswerBinding
import com.ayata.clad.databinding.ItemRecyclerQuestionTypeQuestionBinding
import com.ayata.clad.product.qa.ModelQA

class AdapterQaMultiple(data: List<ModelQA>) :
    RecyclerView.Adapter<AdapterQaMultiple.HomeRecyclerViewHolder>() {
    var items = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {

        return when (viewType) {
            R.layout.item_recycler_question_type_question -> HomeRecyclerViewHolder.QuestionViewHolder(
                ItemRecyclerQuestionTypeQuestionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_recycler_question_type_answer -> HomeRecyclerViewHolder.AnswerViewHolder(
                ItemRecyclerQuestionTypeAnswerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_recycler_question_divider -> HomeRecyclerViewHolder.DividerViewHolder(
                ItemRecyclerQuestionDividerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid ViewType Provided")
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HomeRecyclerViewHolder, position: Int) {
        when (holder) {
            is HomeRecyclerViewHolder.QuestionViewHolder -> holder.bind(
                items[position] as ModelQA.Question
            )
            is HomeRecyclerViewHolder.AnswerViewHolder -> holder.bind(items[position] as ModelQA.Answer)
            is HomeRecyclerViewHolder.DividerViewHolder -> holder.bind(items[position] as ModelQA.Divider)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ModelQA.Question -> R.layout.item_recycler_question_type_question
            is ModelQA.Answer -> R.layout.item_recycler_question_type_answer
            is ModelQA.Divider -> R.layout.item_recycler_question_divider
        }
    }

    sealed class HomeRecyclerViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        class QuestionViewHolder(private val binding: ItemRecyclerQuestionTypeQuestionBinding) :
            HomeRecyclerViewHolder(binding) {

            fun bind(
                item: ModelQA.Question
            ) {
                with(item) {
                    binding.apply {
                        tvQuestion.text = item.question
                        tvDate.text = "${item.postedBy} - ${item.postedAt}"
                    }
                }
            }
        }


        class AnswerViewHolder(private val binding: ItemRecyclerQuestionTypeAnswerBinding) :
            HomeRecyclerViewHolder(binding) {
            fun bind(item: ModelQA.Answer) {
                with(item) {
                    binding.apply {
                        tvAnswer.text = item.answer
                        tvDate.text = "${item.postedBy} - ${item.postedAt}"
                    }

                }
            }
        }

        class DividerViewHolder(private val binding: ItemRecyclerQuestionDividerBinding) :
            HomeRecyclerViewHolder(binding) {
            fun bind(item: ModelQA.Divider) {
                with(item) {

                }
            }
        }


    }

//    private var itemFilterClick: ((ModelQA.Question,Int) -> Unit)? = null
//    fun setFilterClickListener(listener: ((MyFilterRecyclerViewItem.Title,Int) -> Unit)) {
//        itemFilterClick = listener
//    }
//
//    private var itemFilterColorClick: ((MyFilterRecyclerViewItem.Color,Int) -> Unit)? = null
//    fun setFilterColorClickListener(listener: ((MyFilterRecyclerViewItem.Color,Int) -> Unit)) {
//        itemFilterColorClick = listener
//    }
//

}
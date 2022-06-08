package com.ayata.clad.product.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ayata.clad.R
import com.ayata.clad.databinding.ItemRecyclerQuestionDividerBinding
import com.ayata.clad.databinding.ItemRecyclerQuestionTypeAnswerBinding
import com.ayata.clad.databinding.ItemRecyclerQuestionTypeQuestionBinding
import com.ayata.clad.product.qa.ModelQA

class AdapterQaMultiple(params: MutableList<ModelQA>) :
    RecyclerView.Adapter<AdapterQaMultiple.HomeRecyclerViewHolder>() {
    var items = params
    fun setMyItems(params: MutableList<ModelQA>) {
        this.items = params
        notifyDataSetChanged()
    }

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
            is HomeRecyclerViewHolder.QuestionViewHolder -> {
                holder.bind(
                    items[position] as ModelQA.Question
                )
                holder.itemView.findViewById<TextView>(R.id.tv_edit).setOnClickListener {
                    Log.d("testposition", "onBindViewHolder: " + position);
                    itemEditClick?.let {
                        it(items[position] as ModelQA.Question, position)
                    }
                }
                holder.itemView.rootView.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        //check if editable and deletable
                        if ((items[position] as ModelQA.Question).isDeleteable && (items[position] as ModelQA.Question).isEditable) {
                            itemDeleteClick?.let {
                                it(items[position] as ModelQA.Question, position)
                            }
                        }
                        return false
                    }

                })


            }
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
                    if (item.isDeleteable) {
//                        binding.tvDelete.visibility = View.VISIBLE
                    } else {
//                        binding.tvDelete.visibility = View.GONE
                    }
                    if (item.isEditable) {
                        binding.tvEdit.visibility = View.VISIBLE
                    } else {
                        binding.tvEdit.visibility = View.GONE
                    }
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

    private var itemEditClick: ((ModelQA.Question, Int) -> Unit)? = null
    fun setEditClickListener(listener: ((ModelQA.Question, Int) -> Unit)) {
        itemEditClick = listener
    }

    //
    private var itemDeleteClick: ((ModelQA.Question, Int) -> Unit)? = null
    fun setDeleteClickListener(listener: ((ModelQA.Question, Int) -> Unit)) {
        itemDeleteClick = listener
    }
//

}
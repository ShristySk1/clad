package com.ayata.clad.product.qa

import java.io.Serializable


sealed class ModelQA : Serializable {
    data class Question(
        val question: String,
        val postedAt:String,
        val postedBy:String,
        val isDeleteable:Boolean,
        val isEditable:Boolean,
        val questionId: Int?=-1,
    ) : ModelQA()

    data class Answer(
        val answer: String,
        val postedAt:String,
        val postedBy:String
    ) : ModelQA()

    class Divider : ModelQA()
}
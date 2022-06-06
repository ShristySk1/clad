package com.ayata.clad.product.qa

import java.io.Serializable


sealed class ModelQA : Serializable {
    data class Question(
        val question: String,
        val postedAt:String,
        val postedBy:String
    ) : ModelQA()

    data class Answer(
        val answer: String,
        val postedAt:String,
        val postedBy:String
    ) : ModelQA()

    class Divider : ModelQA()
}
package com.ayata.clad.product.qa.response


import com.ayata.clad.home.response.Query

data class ResponseQA(
    val message: String,
    val queries: List<Query>?
)
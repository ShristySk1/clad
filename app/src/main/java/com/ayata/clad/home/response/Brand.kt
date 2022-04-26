package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Brand(
    @SerializedName("brand_image")
    val brandImage: String="",
    val id: Int=0,
    val name: String="",
    val slug:String=""
):Serializable

//{
//    companion object {
//        operator fun invoke(
//             brandImage: String?=null,
//              id: Int?=null,
//            name: String? = null,
//              slug:String?=null
//        ) = Brand(
//            brandImage?:"",
//            id?:0,
//             name?: "",
//            slug?:""
//
//        )
//    }
//}
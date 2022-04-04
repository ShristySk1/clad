package com.ayata.clad.profile.address


import com.google.gson.annotations.SerializedName

 class ModelTest(
    val district: List<String>,
    val state: String
){
     override fun toString(): String {
         return "$state"
     }
 }
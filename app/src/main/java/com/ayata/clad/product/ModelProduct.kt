package com.ayata.clad.product

data class ModelProduct(val id:Int,val image:String,val name:String,val company:String,val price:String,val isWishList:Boolean=false)
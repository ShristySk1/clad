package com.ayata.clad.view_all.model

class ModelViewAllProduct {

    constructor (title: String,price:String,company:String,isOnWishList:Boolean,imageUrl: String) {
        this.imageUrl = imageUrl
        this.title = title
        this.price=price
        this.company=company
        this.isOnWishList=isOnWishList
    }

    var title: String = ""
    var imageUrl: String = ""
    var price:String=""
    var company:String=""
    var isOnWishList:Boolean=false
}
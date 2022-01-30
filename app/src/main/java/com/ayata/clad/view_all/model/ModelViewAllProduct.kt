package com.ayata.clad.view_all.model

class ModelViewAllProduct {

    constructor (title: String,priceNPR:String,priceUSD:String,company:String,isOnWishList:Boolean,imageUrl: String) {
        this.imageUrl = imageUrl
        this.title = title
        this.priceNPR=priceNPR
        this.priceUSD=priceUSD
        this.company=company
        this.isOnWishList=isOnWishList
    }

    var title: String = ""
    var imageUrl: String = ""
    var priceNPR: String=""
    var priceUSD:String=""
    var company:String=""
    var isOnWishList:Boolean=false
}
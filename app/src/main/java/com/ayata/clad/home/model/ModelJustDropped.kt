package com.ayata.clad.home.model

class ModelJustDropped {

    constructor(imageUrl: String, title: String, priceNPR:String,priceUSD:String,logoUrl:String) {
        this.imageUrl = imageUrl
        this.title = title
        this.priceNPR=priceNPR
        this.priceUSD=priceUSD
        this.logoUrl=logoUrl
    }

    var title: String = ""
    var priceNPR: String=""
    var priceUSD:String=""
    var imageUrl: String=""
    var logoUrl:String=""

}
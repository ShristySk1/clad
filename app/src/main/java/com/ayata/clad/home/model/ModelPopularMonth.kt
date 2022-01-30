package com.ayata.clad.home.model

class ModelPopularMonth {

    constructor (title: String,price:String,priceUSD:String,imageUrl: String) {
        this.imageUrl = imageUrl
        this.title = title
        this.price=price
        this.priceUSD=priceUSD
    }

    var title: String = ""
    var imageUrl: String = ""
    var price:String=""
    var priceUSD:String=""
}
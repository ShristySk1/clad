package com.ayata.clad.product

class ModelRecommendedProduct {

    constructor (imageUrl: String,title: String,company:String,logo:String,priceNPR:String,priceUSD:String) {
        this.imageUrl = imageUrl
        this.title = title
        this.priceNPR=priceNPR
        this.priceUSD=priceUSD
        this.company=company
        this.logo=logo
    }

    var title: String = ""
    var imageUrl: String = ""
    var priceNPR: String=""
    var priceUSD:String=""
    var company:String=""
    var logo:String=""
}
package com.ayata.clad.home.model

class ModelRecommended {

    constructor (title: String,description:String,priceNPR:String,priceUSD: String,imageUrl: String) {
        this.imageUrl = imageUrl
        this.title = title
        this.priceNPR=priceNPR
        this.priceUSD=priceUSD
        this.description=description
    }

    var title: String = ""
    var imageUrl: String = ""
    var priceNPR:String=""
    var description:String=""
    var priceUSD:String=""

}
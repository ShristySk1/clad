package com.ayata.clad.home.model

import kotlin.math.log

class ModelMostPopular {

    constructor(imageUrl: String, title: String, description:String, priceNPR:String,priceUSD: String,logoUrl:String) {
        this.imageUrl = imageUrl
        this.title = title
        this.description=description
        this.priceNPR=priceNPR
        this.priceUSD=priceUSD
        this.logoUrl= logoUrl
    }

    var title: String = ""
    var description:String=""
    var imageUrl: String=""
    var logoUrl:String=""
    var priceNPR:String=""
    var priceUSD:String=""

}
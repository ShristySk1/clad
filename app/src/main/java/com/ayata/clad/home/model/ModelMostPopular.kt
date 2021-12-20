package com.ayata.clad.home.model

import kotlin.math.log

class ModelMostPopular {

    constructor(imageUrl: String, title: String, description:String, price:String,logoUrl:String) {
        this.imageUrl = imageUrl
        this.title = title
        this.description=description
        this.price=price
        this.logoUrl= logoUrl
    }

    var title: String = ""
    var description:String=""
    var imageUrl: String=""
    var logoUrl:String=""
    var price:String=""

}
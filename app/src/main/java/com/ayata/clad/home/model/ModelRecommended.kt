package com.ayata.clad.home.model

class ModelRecommended {

    constructor (title: String,description:String,price:String,imageUrl: String) {
        this.imageUrl = imageUrl
        this.title = title
        this.price=price
        this.description=description
    }

    var title: String = ""
    var imageUrl: String = ""
    var price:String=""
    var description:String=""
}
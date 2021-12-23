package com.ayata.clad.product

class ModelRecommendedProduct {

    constructor (imageUrl: String,title: String,company:String,logo:String,price:String) {
        this.imageUrl = imageUrl
        this.title = title
        this.price=price
        this.company=company
        this.logo=logo
    }

    var title: String = ""
    var imageUrl: String = ""
    var price:String=""
    var company:String=""
    var logo:String=""
}
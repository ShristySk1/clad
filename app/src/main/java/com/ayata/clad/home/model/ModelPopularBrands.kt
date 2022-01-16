package com.ayata.clad.home.model

class ModelPopularBrands {

    constructor(imageurl: String, title: String, description:String) {
        this.imageurl = imageurl
        this.title = title
        this.description=description
    }

    constructor(imageDrawable: Int, title: String, description:String) {
        this.imageDrawable = imageDrawable
        this.title = title
        this.description=description
    }

    var title: String = ""
    var description:String=""
    var imageurl: String=""
    var imageDrawable:Int=0

}
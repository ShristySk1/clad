package com.ayata.clad.shopping_bag.model

class ModelCircleText {

    constructor(
        productId: Int, title: String,
        isSelected: Boolean,
        priceNpr:String,
        priceDollar:String
    ) {
        this.productId = productId
        this.title = title
        this.isSelected = isSelected
        this.priceNpr=priceNpr
        this.priceDollar=priceDollar
    }


    var title: String = ""
    var productId: Int = 0
    var isSelected: Boolean = false
    var priceNpr: String = ""
    var priceDollar: String = ""

}
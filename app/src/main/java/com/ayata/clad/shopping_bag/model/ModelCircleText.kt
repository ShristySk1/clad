package com.ayata.clad.shopping_bag.model

class ModelCircleText {

    constructor(productId:Int,title:String,
                isSelected:Boolean){
        this.productId=productId
        this.title=title
        this.isSelected=isSelected
    }


    var title:String=""
    var productId:Int=0
    var isSelected:Boolean=false

}
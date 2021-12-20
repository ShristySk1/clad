package com.ayata.clad.shopping_bag.model

class ModelShippingAddress {

    constructor(name:String,address:String,
                isSelected:Boolean){
        this.name=name
        this.isSelected=isSelected
        this.address=address
    }


    var name:String=""
    var isSelected:Boolean=false
    var address:String=""

}
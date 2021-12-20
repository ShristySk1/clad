package com.ayata.clad.shopping_bag.model

class ModelPaymentMethod {

    constructor(name:String,logo:String,
                isSelected:Boolean){
        this.name=name
        this.isSelected=isSelected
        this.logo=logo
    }


    var name:String=""
    var isSelected:Boolean=false
    var logo:String=""

}
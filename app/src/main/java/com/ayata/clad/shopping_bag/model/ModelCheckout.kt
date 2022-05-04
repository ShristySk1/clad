package com.ayata.clad.shopping_bag.model

import java.io.Serializable

class ModelCheckout:Serializable {

    constructor(name:String,itemId:Int,priceNPR:Double,priceUSD:Double,size:String,qty:Int,
                isSelected:Boolean,image:String,cartId:Int,color:String,colorHex:String,brand:String,stock:String,coupenAvailability:Boolean,coupenString:String,stockQty:Int){
        this.name=name
        this.isSelected=isSelected
        this.itemId=itemId
        this.image=image
        this.priceNPR=priceNPR
        this.priceUSD=priceUSD
        this.size=size
        this.qty=qty
        this.cartId=cartId
        this.color=color
        this.colorHex=colorHex
        this.brand=brand
        this.stockQty=stockQty
        this.stock=stock
        this.isCoupenAvailable=coupenAvailability
        this.coupenText=coupenString
    }


    var name:String=""
    var isSelected:Boolean=false
    var itemId:Int=0
    var priceNPR:Double=0.0
    var priceUSD:Double=0.0
    var size:String=""
    var qty:Int=0
    var image:String=""
    var cartId:Int=0
    var color:String=""
    var colorHex:String=""
    var brand:String=""
    var stock:String=""
    var stockQty:Int=0
    var isCoupenAvailable:Boolean=false
    var coupenText:String=""
    override fun toString(): String {
        return "ModelCheckout(name='$name', isSelected=$isSelected, itemId=$itemId, cartId=$cartId)"
    }

}
package com.ayata.clad.profile.giftcard

import kotlin.math.log

class ModelGiftCard {
    constructor(title: String, description:String,code:String, valid:String) {
        this.title = title
        this.description=description
        this.valid=valid
        this.code= code
    }
    var title: String = ""
    var description:String=""
    var valid: String=""
    var code:String=""

}
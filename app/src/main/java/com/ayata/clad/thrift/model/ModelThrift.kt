package com.ayata.clad.thrift.model

class ModelThrift {

    constructor(name:String,
                timeText:String,boldText:String,description:String,like:Double,comment:Double,isLiked:Boolean,isBookmarked:Boolean){
        this.name=name
        this.timeText=timeText
        this.comment=comment
        this.like=like
        this.description=description
        this.boldText=boldText
        this.isBookmarked=isBookmarked
        this.isLiked=isLiked
    }

    var boldText:String=""
    var name:String=""
    var comment:Double=0.0
    var description:String=""
    var timeText:String=""
    var like:Double=0.0
    var isLiked:Boolean=false
    var isBookmarked:Boolean=false

}
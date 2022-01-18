package com.ayata.clad.home.model

class ModelStory {

    constructor(imageUrl: String, title: String, description:String,storyList:ArrayList<String>) {
        this.imageUrl = imageUrl
        this.title = title
        this.description=description
        this.storyList=storyList
    }

    var title: String = ""
    var description:String=""
    var imageUrl: String=""
    var storyList=ArrayList<String>()

}
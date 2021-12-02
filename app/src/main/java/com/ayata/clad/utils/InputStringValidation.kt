package com.ayata.clad.utils

class InputStringValidation {

    companion object ValidationData {

        fun isNumberempty(numberinstring:String):Boolean{
            if(numberinstring.isNullOrBlank())
                return true
            return false

        }
    }

}
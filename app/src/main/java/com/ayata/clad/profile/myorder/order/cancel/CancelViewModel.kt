package com.ayata.clad.profile.myorder.order.cancel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.profile.edit.response.Details

class CancelViewModel:ViewModel() {
    var detail:MutableLiveData<Boolean> = MutableLiveData()
    fun setCancelDetails(boolean: Boolean){
        detail.postValue(boolean)

}
    fun getCancelDetails():LiveData<Boolean>{
        return detail
    }

}
package com.ayata.clad.profile.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayata.clad.profile.edit.response.Details

class AccountViewModel:ViewModel() {
    var detail:MutableLiveData<Details> = MutableLiveData()
    fun setAccountDetail(details: Details){
        detail.postValue(details)

}
    fun getAccountDetails():LiveData<Details>{
        return detail
    }

}
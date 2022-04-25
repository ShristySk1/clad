package com.ayata.clad.profile.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {
    var detail: MutableLiveData<String> = MutableLiveData()
    var test: MutableLiveData<Test> = MutableLiveData()
    fun getData()=test.value
    fun setProductListFromCategory(
        token: String,
        filterSlug: String,
        refactorAllFromList: String,
        refactorAllFromList1: String,
        refactorAllFromList2: String,
        mysortByFilter: String,
        newMin: String,
        newMax: String
    ) {
        val data = Test(
            token,
            filterSlug,
            refactorAllFromList,
            refactorAllFromList1,
            refactorAllFromList2,
            mysortByFilter,
            newMin,
            newMax
        )
        test.value = data
    }
    fun clear(){
        test=MutableLiveData()
    }

}

data class Test(
    val token: String,
    val filterSlug: String,
    val refactorAllFromList: String,
    val refactorAllFromList1: String,
    val refactorAllFromList2: String,
    val mysortByFilter: String,
    val newMin: String,
    val newMax: String
)
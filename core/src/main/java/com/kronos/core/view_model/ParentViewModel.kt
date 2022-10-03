package com.kronos.core.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

open class ParentViewModel(
    var loading: MutableLiveData<Boolean> = MutableLiveData(),
    var error: MutableLiveData<Hashtable<String, String>> = MutableLiveData()
) : ViewModel() {

    init {
        loading.value = false
        error.value = Hashtable()
    }
}
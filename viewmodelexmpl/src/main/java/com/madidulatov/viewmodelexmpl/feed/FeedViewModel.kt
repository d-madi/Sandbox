package com.madidulatov.viewmodelexmpl.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FeedViewModel: ViewModel() {
    private val _feeds: MutableLiveData<Any> = MutableLiveData()
    val feeds: LiveData<Any> = _feeds

    override fun onCleared() {
        super.onCleared()
    }
}
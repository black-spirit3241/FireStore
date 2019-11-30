package com.padc.firebase.firestore.mvp.presenters

import androidx.lifecycle.ViewModel
import com.padc.firebase.firestore.mvp.views.BaseView

abstract class BasePresenter<T: BaseView> : ViewModel() {

    protected lateinit var mView: T

    open fun initPresenter(view: T){
        this.mView  = view
    }
}

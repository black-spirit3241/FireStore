package com.padc.firebase.firestore.mvp.presenters

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.padc.firebase.firestore.data.models.*
import com.padc.firebase.firestore.data.vos.ArticleVO
import com.padc.firebase.firestore.delegates.ArticleItemDelegate
import com.padc.firebase.firestore.mvp.views.ArticlesView

class ArticlesPresenter : BaseGoogleSignInPresenter<ArticlesView>(), ArticleItemDelegate {

//    private val model : FirebaseModel = FirebaseModelImpl
    private val model : FirebaseModel = FirestoreModelImpl
    private val userModel : UserAuthenticationModel = UserAuthenticationModelImpl
    private val clearedLiveData = MutableLiveData<Unit>()

    fun onUIReady(owner: LifecycleOwner){
        model.getAllArticles(clearedLiveData).observe(owner, Observer {
            mView.showArticles(it)
        })
    }

    override fun onArticleItemClicked(data: ArticleVO) {
        mView.navigateToDetail(data.id)
    }

    override fun onCleared() {
        clearedLiveData.value = Unit
        super.onCleared()
    }

    fun onStart() {
        userModel.currentUser?.let {
            mView.showLoginUser(it)
        } ?: mView.showLogoutUser()
    }

    fun onUserProfileClicked(context: Context) {
        if (userModel.isLoginUser()){
            userModel.logOut()
            onStart()

        } else {
            googleSignIn(context)
        }
    }
}
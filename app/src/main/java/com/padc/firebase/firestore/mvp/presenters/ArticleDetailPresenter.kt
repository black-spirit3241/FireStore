package com.padc.firebase.firestore.mvp.presenters

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.padc.firebase.firestore.data.models.*
import com.padc.firebase.firestore.data.vos.ArticleVO
import com.padc.firebase.firestore.mvp.views.ArticleDetailView

class ArticleDetailPresenter: BaseGoogleSignInPresenter<ArticleDetailView>() {

//    private val model: FirebaseModel = FirebaseModelImpl
    private val model : FirebaseModel =FirestoreModelImpl
    private val userModel: UserAuthenticationModel = UserAuthenticationModelImpl
    private val clearedLiveData = MutableLiveData<Unit>()

    private lateinit var article: ArticleVO
    private var pickedImage: Uri? = null

    fun onUIReady(owner: LifecycleOwner, id: String) {
        model.getArticleById(id, clearedLiveData).observe(owner, Observer {
            mView.showArticle(it)
            article = it
        })
    }

    override fun onCleared() {
        clearedLiveData.value = Unit
        super.onCleared()
    }

    fun onClapped(count: Int) {
        model.updateClapCount(1, article)
    }

    fun onCommentClicked(context: Context) {
        if (userModel.isLoginUser()){
            mView.showCommentInputView()
        } else {
            googleSignIn(context)
        }
    }

    fun onCommentSendClicked(comment: String) {
        if (comment.isNotEmpty() || pickedImage != null){
            model.addComment(comment, pickedImage,  article)
        }
    }

    fun onImagePicked(uri: Uri) {
        pickedImage = uri
        mView.showPickedImage(uri)
    }
}
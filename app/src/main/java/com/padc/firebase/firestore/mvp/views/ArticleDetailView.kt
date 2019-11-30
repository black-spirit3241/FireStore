package com.padc.firebase.firestore.mvp.views

import android.net.Uri
import com.padc.firebase.firestore.data.vos.ArticleVO

interface ArticleDetailView : BaseGoogleSignInView{

    fun showArticle(data: ArticleVO)
    fun showCommentInputView()
    fun showPickedImage(uri: Uri)
}
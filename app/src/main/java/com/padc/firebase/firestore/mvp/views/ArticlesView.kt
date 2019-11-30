package com.padc.firebase.firestore.mvp.views

import com.google.firebase.auth.FirebaseUser
import com.padc.firebase.firestore.data.vos.ArticleVO

interface ArticlesView  : BaseGoogleSignInView{
    fun navigateToDetail(id: String)
    fun showArticles(data: List<ArticleVO>)
    fun showLoginUser(user: FirebaseUser)
    fun showLogoutUser()
}
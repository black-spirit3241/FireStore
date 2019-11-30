package com.padc.firebase.firestore.delegates

import com.padc.firebase.firestore.data.vos.ArticleVO

interface ArticleItemDelegate {
    fun onArticleItemClicked(data: ArticleVO)
}
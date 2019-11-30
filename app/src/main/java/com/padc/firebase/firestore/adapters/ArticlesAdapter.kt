package com.padc.firebase.firestore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padc.firebase.firestore.R
import com.padc.firebase.firestore.data.vos.ArticleVO
import com.padc.firebase.firestore.delegates.ArticleItemDelegate
import com.padc.firebase.firestore.viewholders.ArticleViewHolder

class ArticlesAdapter(private val delegate: ArticleItemDelegate): BaseAdapter<ArticleViewHolder, ArticleVO>() {
//
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_item_article, parent, false)
        return ArticleViewHolder(itemView, delegate)

    }

}
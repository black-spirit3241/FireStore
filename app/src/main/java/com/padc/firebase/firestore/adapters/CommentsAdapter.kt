package com.padc.firebase.firestore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padc.firebase.firestore.R
import com.padc.firebase.firestore.data.vos.CommentVO
import com.padc.firebase.firestore.viewholders.CommentViewHolder

class CommentsAdapter: BaseAdapter<CommentViewHolder, CommentVO>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.viewholder_comment, parent, false
        )

        return CommentViewHolder(view)
    }
}
package com.padc.firebase.firestore.data.models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.padc.firebase.firestore.data.vos.ArticleVO
import com.padc.firebase.firestore.data.vos.CommentVO
import com.padc.firebase.firestore.data.vos.UserVO
import com.padc.firebase.firestore.utils.REF_KEY_CLAP_COUNT
import com.padc.firebase.firestore.utils.REF_KEY_COMMENTS
import com.padc.firebase.firestore.utils.REF_PATH_ARTICLES
import com.padc.firebase.firestore.utils.STORAGE_FOLDER_PATH

object FirestoreModelImpl : FirebaseModel{

    // Access a Cloud Firestore instance from your Activity
    val db = FirebaseFirestore.getInstance()
    const val TAG="FireStoreModel"


    override fun getAllArticles(cleared: LiveData<Unit>): LiveData<List<ArticleVO>> {
        val liveData=MutableLiveData<List<ArticleVO>>()
        val articleRef=db.collection(REF_PATH_ARTICLES)

       val realTimeListener = object : EventListener<QuerySnapshot>{

           override fun onEvent(documentSnapshot: QuerySnapshot?, p1: FirebaseFirestoreException?) {
               val articles = ArrayList<ArticleVO>()
               for(snapshot in documentSnapshot?.documents!!){
                   val article=snapshot.toObject(ArticleVO::class.java)
                   article?.let {
                       articles.add(article)
                   }
               }
               liveData.value=articles
           }
       }

        // Start real-time data observing
        articleRef.addSnapshotListener(realTimeListener)

        // Stop real-time data observing when Presenter's onCleared() was called
        cleared.observeForever(object : Observer<Unit> {
            override fun onChanged(unit: Unit?) {
                unit?.let {
                    cleared.removeObserver(this)
                }
            }
        })

        return liveData
    }

    override fun getArticleById(id: String, cleared: LiveData<Unit>): LiveData<ArticleVO> {
        val liveData=MutableLiveData<ArticleVO>()
        val articleRef=db.collection(REF_PATH_ARTICLES).document(id)

        articleRef.addSnapshotListener{
            snapshot, e ->
            if(snapshot != null && snapshot.exists()){
                Log.d(TAG,"Get data success!")
                val article=snapshot.toObject(ArticleVO::class.java)
               article?.let {
                   liveData.value=it
               }
            }else{
                Log.d(TAG,"Get data fail!!!" + e?.message)
            }
        }

        // Stop real-time data observing when Presenter's onCleared() was called
        cleared.observeForever(object : Observer<Unit> {
            override fun onChanged(unit: Unit?) {
                unit?.let {
                    cleared.removeObserver(this)
                }
            }
        })

        return liveData
    }

    override fun updateClapCount(count: Int, article: ArticleVO) {
        val articleRef = db.collection(REF_PATH_ARTICLES).document(article.id)
        val data= hashMapOf(REF_KEY_CLAP_COUNT to count+article.claps)
        articleRef.set(data,SetOptions.merge())
    }

    override fun addComment(comment: String, pickedImage: Uri?, article: ArticleVO) {

        if (pickedImage != null) {
           uploadImageAndAddComment(comment, pickedImage, article)

        } else {
            val currentUser = UserAuthenticationModelImpl.currentUser!!
            val newComment = CommentVO(
                System.currentTimeMillis().toString(), "", comment, UserVO(
                    currentUser.providerId,
                    currentUser.displayName ?: "",
                    currentUser.photoUrl.toString()
                )
            )
            addComment(newComment, article)
        }
    }

    private fun uploadImageAndAddComment(comment: String, pickedImage: Uri, article: ArticleVO) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesFolderRef = storageRef.child(STORAGE_FOLDER_PATH)

        val imageRef = imagesFolderRef.child(
            pickedImage.lastPathSegment ?: System.currentTimeMillis().toString()
        )

        val uploadTask = imageRef.putFile(pickedImage)
        uploadTask.addOnFailureListener {
            Log.e(FirebaseModelImpl.TAG, it.localizedMessage)
        }
            .addOnSuccessListener {
                // get comment image's url

                imageRef.downloadUrl.addOnCompleteListener {
                    Log.d(FirebaseModelImpl.TAG, "Image Uploaded ${it.result.toString()}")

                    val currentUser = UserAuthenticationModelImpl.currentUser!!
                    val newComment = CommentVO(
                        System.currentTimeMillis().toString(), it.result.toString(), comment,
                        UserVO(
                            currentUser.providerId,
                            currentUser.displayName ?: "",
                            currentUser.photoUrl.toString()
                        )
                    )

                    addComment(newComment, article)
                }

            }

    }

    private fun addComment(comment: CommentVO, article: ArticleVO) {
        val commentRef=db.collection(REF_PATH_ARTICLES).document(article.id)
        val key=comment.id

        val comments=article.comments.toMutableMap()
        comments[key]=comment

        val data= mapOf(REF_KEY_COMMENTS to comments)
        commentRef.update(data).addOnSuccessListener {
            Log.d(TAG,"Add comment Success")
        }.addOnFailureListener {
            Log.d(TAG,"Add Comment Fail")
        }
    }

}
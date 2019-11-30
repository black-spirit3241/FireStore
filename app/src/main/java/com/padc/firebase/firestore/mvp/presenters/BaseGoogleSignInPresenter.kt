package com.padc.firebase.firestore.mvp.presenters

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.padc.firebase.firestore.R
import com.padc.firebase.firestore.data.models.UserAuthenticationModel
import com.padc.firebase.firestore.data.models.UserAuthenticationModelImpl
import com.padc.firebase.firestore.mvp.views.BaseGoogleSignInView

abstract class BaseGoogleSignInPresenter<V: BaseGoogleSignInView>: BasePresenter<V>() {

    private val userModel: UserAuthenticationModel = UserAuthenticationModelImpl

    fun googleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
//
//        val googleSignInClient = GoogleSignIn.getClient(context, gso)
//
//        mView.navigateToGoogleSignInScreen(googleSignInClient.signInIntent, RC_GOOGLE_SIGN)

        // Configure Google Sign In
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("1005260523235-i9hnp4dll5j7nr3ktqh381gdp8sb313r.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
        val googleSignInClient=GoogleSignIn.getClient(context,gso)

        mView.navigateToGoogleSignInScreen(googleSignInClient.signInIntent, RC_GOOGLE_SIGN)

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, owner: LifecycleOwner) {

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                userModel.firebaseAuthWithGoogle(account!!) { mView.showGoogleLoginError(it) }
                    .observe(owner, Observer {
                        mView.showGoogleLoginSuccess(it)
                    })

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)

                mView.showGoogleLoginError(e.message ?: "")

            }
        }
    }


    companion object {

        const val RC_GOOGLE_SIGN = 9001
        const val TAG = "GoogleSignInPresenter"
    }
}
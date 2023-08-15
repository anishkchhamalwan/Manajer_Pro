package com.example.manajerpro.activities.firebase

import android.app.Activity
import android.util.Log
import com.example.manajerpro.activities.MainActivity
import com.example.manajerpro.activities.SignInActivity
import com.example.manajerpro.activities.SignUpActivity
import com.example.manajerpro.activities.models.User
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registeredUser(activity:SignUpActivity,userInfo: User){
        mFireStore.collection(com.example.manajerpro.activities.utils.Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener{
                activity.userRegisteredSuccess()
            }

    }
    fun signInUser(activity: Activity){
        mFireStore.collection(com.example.manajerpro.activities.utils.Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener{document->
                val loggedInUser= document.toObject(User::class.java)!!
                when(activity){
                    is SignInActivity->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity->{
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                }


            }
            .addOnFailureListener{
                e->
                when(activity){
                    is SignInActivity->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SignINUSer","Error writing document",e)
            }
    }

    fun getCurrentUserId():String{
        var currentUser=  com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser!=null){
            currentUserID=currentUser.uid
        }
        return currentUserID
    }
}
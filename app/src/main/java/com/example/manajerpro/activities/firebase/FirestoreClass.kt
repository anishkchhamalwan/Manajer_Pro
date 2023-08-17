package com.example.manajerpro.activities.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.manajerpro.activities.MainActivity
import com.example.manajerpro.activities.MyProfileActivity
import com.example.manajerpro.activities.SignInActivity
import com.example.manajerpro.activities.SignUpActivity
import com.example.manajerpro.activities.models.User
import com.example.manajerpro.activities.utils.Constants
import com.google.firebase.auth.FirebaseAuth
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

    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap:HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener{
                Log.i(activity.javaClass.simpleName,"Profile Data Updated")
                Toast.makeText(activity,"Profile Updated Successfully",Toast.LENGTH_LONG).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
                Toast.makeText(activity,"Error while Updating",Toast.LENGTH_LONG).show()

            }
    }
     fun loadUserData(activity: Activity){
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
                    is MyProfileActivity->{
                        activity.setUserDataInUI(loggedInUser)
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
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID

        /*var currentUser=  com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser!=null){
            currentUserID=currentUser.uid
        }
        return currentUserID
         */
    }
}
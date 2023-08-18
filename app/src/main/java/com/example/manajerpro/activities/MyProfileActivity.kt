package com.example.manajerpro.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.manajerpro.R
import com.example.manajerpro.activities.firebase.FirestoreClass
import com.example.manajerpro.activities.models.User
import com.example.manajerpro.activities.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException


class MyProfileActivity : BaseActivity() {


    private var mSelectedImageFileUri: Uri?=null
    private lateinit var mUserDetails:User
    private var mProfileImageURL: String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setUpActionBar()

        FirestoreClass().loadUserData(this)

        val iv = findViewById<ImageView>(R.id.iv_user_image)
        iv.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
            else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        val btn_update= findViewById<Button>(R.id.btn_update)
        btn_update.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadUserImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
            else{
                Toast.makeText(this,"Permission for choosing image is not granted",Toast.LENGTH_LONG).show()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode== Constants.PICK_IMAGE_REQUEST_CODE && data!!.data!=null)
        {
            mSelectedImageFileUri =data.data

            val iv = findViewById<ImageView>(R.id.iv_user_image)

            try{
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv)
            }catch(e: IOException){
                e.printStackTrace()
            }

        }

    }
    private fun setUpActionBar() {
        val tb = findViewById<Toolbar>(R.id.toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }
        tb.setNavigationOnClickListener { onBackPressed() }
    }

    fun setUserDataInUI(user:User){

        mUserDetails=user

        val iv = findViewById<ImageView>(R.id.iv_user_image)

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv)

        val tvusname= findViewById<TextView>(R.id.et_name)
        val tvemail= findViewById<TextView>(R.id.et_email)
        val mobno= findViewById<TextView>(R.id.et_mobile)


        tvusname.text = user.name
        tvemail.text = user.email
        if(user.mobile!= 0L){
            mobno.text = user.mobile.toString()
        }

    }
    fun updateUserProfileData(){

        val userHashMap = HashMap<String,Any>()
        if(mProfileImageURL.isNotEmpty()&&mUserDetails.image!=mProfileImageURL){
            userHashMap[Constants.IMAGE] = mProfileImageURL

        }
        val tvusname= findViewById<TextView>(R.id.et_name)
        if(tvusname.text.toString() !=mUserDetails.name){

            userHashMap[Constants.NAME]=tvusname.text.toString()
        }
        val tvmobile= findViewById<TextView>(R.id.et_mobile)
        if(tvmobile.text.toString() !=mUserDetails.mobile.toString()){

            userHashMap[Constants.MOBILE]=tvmobile.text.toString().toLong()
        }
       FirestoreClass().updateUserProfileData(this,userHashMap)
    }
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri!=null){
            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()
                        +"."+Constants.getFileExtension(this,mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                it-> Log.i("Firebase Image URL",it.metadata!!.reference!!.downloadUrl.toString())
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener{
                    uri-> Log.i("Downloadable Image Uri",uri.toString())
                    mProfileImageURL=uri.toString()

                    updateUserProfileData()
                }

            }.addOnFailureListener{
                exception->
                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }
            hideProgressDialog()
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}
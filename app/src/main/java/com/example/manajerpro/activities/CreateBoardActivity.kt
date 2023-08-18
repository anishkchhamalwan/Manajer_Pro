package com.example.manajerpro.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.manajerpro.R
import com.example.manajerpro.activities.firebase.FirestoreClass
import com.example.manajerpro.activities.models.Board
import com.example.manajerpro.activities.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var mSelectedImageFileUri : Uri?= null
    private lateinit var mUserName :String
    private  var mBoardImageURL: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        setUpActionBar()

        if(intent.hasExtra(Constants.NAME)){
            mUserName= intent.getStringExtra(Constants.NAME).toString()
        }

        val iv = findViewById<ImageView>(R.id.iv_board_image)
        iv.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        val btn_create= findViewById<Button>(R.id.btn_create)
        btn_create.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }

    }


    private fun createBoard(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        val etboard = findViewById<EditText>(R.id.et_board_name)
        var board = Board(
            etboard.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FirestoreClass().createBoard(this,board)
    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if(mSelectedImageFileUri!=null){
            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child("BOARD_IMAGE"+System.currentTimeMillis()
                        +"."+Constants.getFileExtension(this,mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    it-> Log.i("BOARD Image URL",it.metadata!!.reference!!.downloadUrl.toString())
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener{
                        uri-> Log.i("Downloadable Image Uri",uri.toString())
                    mBoardImageURL=uri.toString()

                    createBoard()
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

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setUpActionBar() {
        val tb = findViewById<Toolbar>(R.id.toolbar_create_board_activity)

        setSupportActionBar(tb)
        (tb).setNavigationIcon(R.drawable.ic_white_color_back_24dp)
        tb.title= resources.getString(R.string.create_board_title)
        (tb).setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
            else{
                Toast.makeText(this,"Permission for choosing image is not granted", Toast.LENGTH_LONG).show()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode== Constants.PICK_IMAGE_REQUEST_CODE && data!!.data!=null)
        {
            mSelectedImageFileUri =data.data

            val iv = findViewById<ImageView>(R.id.iv_board_image)

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


}
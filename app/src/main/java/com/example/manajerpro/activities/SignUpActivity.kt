package com.example.manajerpro.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.manajerpro.R
import com.example.manajerpro.activities.firebase.FirestoreClass
import com.example.manajerpro.activities.models.User
import com.example.manajerpro.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()
        binding.btnSignUp.setOnClickListener {
            registerUser()
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this,"You have succesfully registed the email",Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun registerUser(){
        val name:String = binding.etName.text.toString()
        val email:String = binding.etEmail.text.toString()
        val password:String = binding.etPassword.text.toString()
        if(validateForm(name,email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task->
                hideProgressDialog()
                if(task.isSuccessful){
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid,name,registeredEmail)

                  FirestoreClass().registeredUser(this,user)

                }else{
                    Toast.makeText(this,task.exception!!.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateForm(name:String,email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please Enter The Name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please Enter The Email Address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please Enter The Password")
                false
            }
            else->{
                true
            }

        }
    }
}

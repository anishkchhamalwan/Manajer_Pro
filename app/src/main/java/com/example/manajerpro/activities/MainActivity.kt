package com.example.manajerpro.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.manajerpro.R
import com.example.manajerpro.activities.firebase.FirestoreClass
import com.example.manajerpro.activities.models.User
import com.example.manajerpro.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding:ActivityMainBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    companion object{
        const val MY_PROFILE_REQUEST_CODE :Int =11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.nav_open, R.string.nav_close)
        //actionBarDrawerToggle.setToolbarNavigationClickListener { toggleDrawer() }
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        //binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        //actionBarDrawerToggle.syncState()

        // to make the Navigation drawer icon always appear on the action bar
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpActionBar()
        binding.navView.setNavigationItemSelectedListener(this)


        FirestoreClass().loadUserData(this)

        val fab = findViewById<FloatingActionButton>(R.id.fab_create_board)
        fab.setOnClickListener {
            startActivity(Intent(this,CreateBoardActivity::class.java))
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
    private fun setUpActionBar(){
        val tb = findViewById<Toolbar>(R.id.toolbar_main_activity)
        //val view = layoutInflater.inflate(R.layout.app_bar_main,null)
       // val viewFromOther = view.findViewById<View>(R.id.toolbar_main_activity)
        setSupportActionBar(tb)
        (tb).setNavigationIcon(R.drawable.ic_action_navigation_menu)
        (tb).setNavigationOnClickListener {
            toggleDrawer()
        }
       // supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    private fun toggleDrawer(){
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode== MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this)
        }
        else{
            Log.e("Cancelled","Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                startActivityForResult(Intent(this,MyProfileActivity::class.java,),MY_PROFILE_REQUEST_CODE)

                Toast.makeText(this,"MY PROFILE ROCKS",Toast.LENGTH_LONG).show()
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    fun updateNavigationUserDetails(user: User){
        val iv = findViewById<ImageView>(R.id.nav_user_image)
       // val view = layoutInflater.inflate(R.layout.nav_header_main,null)
        //val viewFromOther = view.findViewById<ImageView>(R.id.nav_user_image)
      //  val nav_header_main: NavHeaderMainBinding = NavHeaderMainBinding.inflate(layoutInflater)

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv)

       val tv_usname= findViewById<TextView>(R.id.tv_username)
        tv_usname.text=user.name
        //nav_header_main.tvUsername.text = user.name

        //val tvUser= view.findViewById<TextView>(R.id.tv_username)
        //tvUser.text= user.name
    }
}
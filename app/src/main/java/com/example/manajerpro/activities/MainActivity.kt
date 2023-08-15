package com.example.manajerpro.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.example.manajerpro.R
import com.example.manajerpro.activities.firebase.FirestoreClass
import com.example.manajerpro.activities.models.User
import com.example.manajerpro.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding:ActivityMainBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.nav_open, R.string.nav_close)
        actionBarDrawerToggle.setToolbarNavigationClickListener { toggleDrawer() }
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener(this)


        FirestoreClass().signInUser(this)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
    private fun setUpActionBar(){
        val view = layoutInflater.inflate(R.layout.app_bar_main,null)
        val viewFromOther = view.findViewById<View>(R.id.toolbar_main_activity)
        setSupportActionBar(viewFromOther as Toolbar)
        (viewFromOther as Toolbar).setNavigationIcon(R.drawable.ic_action_navigation_menu2)
        (viewFromOther as Toolbar).setNavigationOnClickListener {
            toggleDrawer()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
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
        /*val view = layoutInflater.inflate(R.layout.nav_header_main,null)
        val viewFromOther = view.findViewById<ImageView>(R.id.nav_user_image)
        val nav_header_main: NavHeaderMainBinding = NavHeaderMainBinding.inflate(layoutInflater)

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_header_main.navUserImage)
        nav_header_main.tvUsername.text = user.name

        //val tvUser= view.findViewById<TextView>(R.id.tv_username)
        //tvUser.text= user.name

         */
    }
}
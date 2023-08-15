package com.example.manajerpro.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.manajerpro.R
import com.example.manajerpro.activities.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val typeface: Typeface = Typeface.createFromAsset(assets,"carbon bl.otf")
        val tv = findViewById<TextView>(R.id.tv_app_name)
        tv.typeface=typeface
        Handler().postDelayed({

            var currentUserID=FirestoreClass().getCurrentUserId()
            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this,MainActivity::class.java))
            }
            else{
                startActivity(Intent(this,IntroActivity::class.java))
            }
            finish()
        }
        ,2500)
    }
}
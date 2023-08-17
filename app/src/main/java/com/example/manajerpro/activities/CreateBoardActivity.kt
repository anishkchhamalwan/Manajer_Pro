package com.example.manajerpro.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.manajerpro.R

class CreateBoardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        setUpActionBar()
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

}
package com.example.manajerpro.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manajerpro.R
import com.example.manajerpro.activities.adapters.TaskListItemsAdapter
import com.example.manajerpro.activities.firebase.FirestoreClass
import com.example.manajerpro.activities.models.Board
import com.example.manajerpro.activities.models.Task
import com.example.manajerpro.activities.utils.Constants

class TaskListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentId= ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()

        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,boardDocumentId)
    }

    fun boardDetails(board:Board){
        hideProgressDialog()
        setUpActionBar(board.name)

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        val rv_task_list = findViewById<RecyclerView>(R.id.rv_task_list)
        rv_task_list.layoutManager= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_task_list.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this,board.taskList)
        rv_task_list.adapter = adapter

    }
    private fun setUpActionBar(title:String) {
        val tb = findViewById<Toolbar>(R.id.toolbar_task_list_activity)
        setSupportActionBar(tb)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title
        }
        tb.setNavigationOnClickListener { onBackPressed() }
    }
}
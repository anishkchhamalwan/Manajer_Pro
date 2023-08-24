package com.example.manajerpro.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manajerpro.R
import com.example.manajerpro.activities.adapters.TaskListItemsAdapter
import com.example.manajerpro.activities.firebase.FirestoreClass
import com.example.manajerpro.activities.models.Board
import com.example.manajerpro.activities.models.Card
import com.example.manajerpro.activities.models.Task
import com.example.manajerpro.activities.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)


        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()

        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDocumentId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&requestCode== MEMBERS_REQUEST_CODE ||requestCode== CARD_DETAILS_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this,mBoardDocumentId)
        }else{
            Log.e("Cancelled","Cancelled")
        }
    }
    fun boardDetails(board:Board){
        mBoardDetails=board
        hideProgressDialog()
        setUpActionBar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)

        val rvTaskList = findViewById<RecyclerView>(R.id.rv_task_list)
        rvTaskList.layoutManager= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this,mBoardDetails.taskList)
        rvTaskList.adapter = adapter

    }
    private fun setUpActionBar() {
        val tb = findViewById<Toolbar>(R.id.toolbar_task_list_activity)
        setSupportActionBar(tb)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }
        tb.setNavigationOnClickListener { onBackPressed() }
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDetails.documentId)
    }

    fun createTaskList(taskListName:String){
        Log.e("Task List Name",taskListName)
        val task= Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun updateTaskList(position:Int, listName:String, model:Task){
        val task= Task(listName,model.createdBy)

        mBoardDetails.taskList[position]= task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun addCardToTaskList(position: Int,cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        val cardAssignedUserList : ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FirestoreClass().getCurrentUserId())

        val card = Card(cardName,FirestoreClass().getCurrentUserId(),cardAssignedUserList)

        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )
        mBoardDetails.taskList[position]=task

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }

    fun cardDetails(taskListPosition:Int,cardPosition:Int){
        val intent = Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent=Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object{
        const val MEMBERS_REQUEST_CODE :Int= 13
        const val CARD_DETAILS_REQUEST_CODE:Int =14
    }


}
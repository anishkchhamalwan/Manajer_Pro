package com.example.manajerpro.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manajerpro.R
import com.example.manajerpro.activities.adapters.MemberListItemAdapter
import com.example.manajerpro.activities.firebase.FirestoreClass
import com.example.manajerpro.activities.models.Board
import com.example.manajerpro.activities.models.User
import com.example.manajerpro.activities.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var mAssignedMembersList: ArrayList<User>
    private lateinit var mBoardDetails: Board
    private var anyChangesMade: Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if(intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAILS)!!
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assignedTo)

        }
        setUpActionBar()

    }

    fun setUpMembersList(list: ArrayList<User>){

        mAssignedMembersList= list
        hideProgressDialog()

       val rv=  findViewById<RecyclerView>(R.id.rv_members_list)
        rv.layoutManager= LinearLayoutManager(this)
        rv.setHasFixedSize(true)

        val adapter = MemberListItemAdapter(this,list)
        rv.adapter=adapter

    }

    fun membersDetails(user:User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDetails,user)

    }

    private fun setUpActionBar() {
        val tb = findViewById<Toolbar>(R.id.toolbar_members_activity)
        setSupportActionBar(tb)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.members)
        }
        tb.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()

            if(email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this,email)

            }else{
                Toast.makeText(
                    this,
                    "Please Enter members email Address"
                    ,Toast.LENGTH_LONG
                ).show()
            }

        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if(anyChangesMade)
        {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
    fun membersAssignSuccess(user:User){
        hideProgressDialog()
        mAssignedMembersList.add(user)

        anyChangesMade =true
        setUpMembersList(mAssignedMembersList)
    }
}
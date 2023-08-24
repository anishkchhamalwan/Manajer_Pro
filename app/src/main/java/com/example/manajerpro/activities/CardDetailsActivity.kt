package com.example.manajerpro.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manajerpro.R
import com.example.manajerpro.activities.adapters.CardMembersListItemsAdapter
import com.example.manajerpro.activities.dialogs.LabelColorListDialog
import com.example.manajerpro.activities.dialogs.MembersListDialog
import com.example.manajerpro.activities.firebase.FirestoreClass
import com.example.manajerpro.activities.models.Board
import com.example.manajerpro.activities.models.Card
import com.example.manajerpro.activities.models.SelectedMembers
import com.example.manajerpro.activities.models.Task
import com.example.manajerpro.activities.models.User
import com.example.manajerpro.activities.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private  lateinit var mBoardDetails:Board
    private var mTaskListPosition:Int =-1
    private var mCardListPosition : Int=-1
    private var mSelectedColor :String= "#FFFFFF"
    private lateinit var mMembersDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        getIntentData()
        setUpActionBar()

        val etNameCard = findViewById<EditText>(R.id.et_name_card_details)
        etNameCard.setText( mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name)
        etNameCard.setSelection(etNameCard.text.toString().length)

        mSelectedColor= mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }

        findViewById<TextView>(R.id.tv_select_label_color).setOnClickListener {
            labelColorsListDialog()
        }

        findViewById<TextView>(R.id.tv_select_members).setOnClickListener {
            membersListDialog()
        }

        setupSelectedMembersList()

        findViewById<Button>(R.id.btn_update_card_details).setOnClickListener {
            if(findViewById<EditText>(R.id.et_name_card_details).text.toString().isNotEmpty()){
                updateCardDetails()
            }
            else{
                Toast.makeText(this,
                    "Enter card Name ",
                    Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun colorsList():ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")
        return colorsList
    }

    private fun setColor(){
        findViewById<TextView>(R.id.tv_select_label_color).text = ""
        findViewById<TextView>(R.id.tv_select_label_color).setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card-> {
                alertDialogDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setUpActionBar() {
        val tb = findViewById<Toolbar>(R.id.toolbar_card_details_activity)
        setSupportActionBar(tb)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name

        }
        tb.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }
    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails= intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition =  intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardListPosition =  intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }

        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList =  intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    private fun membersListDialog(){
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition]
            .cards[mCardListPosition].assignedTo

        if(cardAssignedMembersList.size >0){
            for(i in mMembersDetailList.indices){
                for(j in cardAssignedMembersList){
                    if(mMembersDetailList[i].id ==j){
                        mMembersDetailList[i].selected= true
                    }
                }
            }
        }else{
            for(i in mMembersDetailList.indices){
                mMembersDetailList[i].selected=false
            }
        }
        val listDialog =  object:MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member)
        ){
            override fun onItemSelected(user: User, action: String) {

                if(action == Constants.SELECT){

                    if(!mBoardDetails.
                        taskList[mTaskListPosition].cards[mCardListPosition]
                            .assignedTo.contains(user.id)) {

                        mBoardDetails.taskList[mTaskListPosition]
                            .cards[mCardListPosition]
                            .assignedTo.add(user.id)
                    }
                }
                else{
                    mBoardDetails.taskList[mTaskListPosition]
                        .cards[mCardListPosition]
                        .assignedTo.remove(user.id)

                    for(i in mMembersDetailList.indices){
                        if(mMembersDetailList[i].id ==user.id){
                            mMembersDetailList[i].selected=false
                        }
                    }
                }

                setupSelectedMembersList()

            }

        }
        listDialog.show()
    }

    private fun updateCardDetails(){
        Log.e(
            "Update Card Details","$Task List Position is $mTaskListPosition and Card Pos is $mCardListPosition)"
        )
        val card= Card(
            findViewById<EditText>(R.id.et_name_card_details).text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo,
            mSelectedColor
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    private fun deleteCard(){
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardListPosition)

        val taskList:ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }
    private fun alertDialogDeleteCard(cardName:String){
        val builder= AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(
            resources.getString(
                R.string.Confirmation_message_to_delete_card
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.yes)){ dialogInterface, which ->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton(resources.getString(R.string.no)){dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog : AlertDialog =builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

   private fun labelColorsListDialog(){
        val colorsList: ArrayList<String> = colorsList()
       val listDialog = object: LabelColorListDialog(
           this,
           colorsList,
           resources.getString(R.string.select_label_color),
           mSelectedColor){
           override fun onItemSelected(color: String) {
               mSelectedColor = color
               setColor()
           }
       }
       listDialog.show()
    }

    private fun setupSelectedMembersList()
    {
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].
        cards[mCardListPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for(i in mMembersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailList[i].id == j) {
                    val selectedMember= SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )

                    selectedMembersList.add(selectedMember)
                }
            }
        }
        if(selectedMembersList.size >0){
            selectedMembersList.add(SelectedMembers("",""))
            findViewById<TextView>(R.id.tv_select_members).visibility= View.GONE
            val rv = findViewById<RecyclerView>(R.id.rv_selected_members_list)
            rv.visibility= View.VISIBLE

            rv.layoutManager=GridLayoutManager(
                this,6
            )
            val adapter = CardMembersListItemsAdapter(this,selectedMembersList,true)
            rv.adapter=adapter

            adapter.setOnClickListener(
                object: CardMembersListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }
                }
            )
        }
        else{
            findViewById<TextView>(R.id.tv_select_members).visibility= View.VISIBLE
            val rv = findViewById<RecyclerView>(R.id.rv_selected_members_list)
            rv.visibility= View.GONE


        }

    }
}
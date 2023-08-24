package com.example.manajerpro.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.manajerpro.R
import com.example.manajerpro.activities.dialogs.LabelColorListDialog
import com.example.manajerpro.activities.firebase.FirestoreClass
import com.example.manajerpro.activities.models.Board
import com.example.manajerpro.activities.models.Card
import com.example.manajerpro.activities.models.Task
import com.example.manajerpro.activities.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private  lateinit var mBoardDetails:Board
    private var mTaskListPosition:Int =-1
    private var mCardListPosition : Int=-1
    private var mSelectedColor :String= "#FFFFFF"

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
}
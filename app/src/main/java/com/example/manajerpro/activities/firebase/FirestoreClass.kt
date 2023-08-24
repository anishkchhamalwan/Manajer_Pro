package com.example.manajerpro.activities.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.manajerpro.activities.CardDetailsActivity
import com.example.manajerpro.activities.CreateBoardActivity
import com.example.manajerpro.activities.MainActivity
import com.example.manajerpro.activities.MembersActivity
import com.example.manajerpro.activities.MyProfileActivity
import com.example.manajerpro.activities.SignInActivity
import com.example.manajerpro.activities.SignUpActivity
import com.example.manajerpro.activities.TaskListActivity
import com.example.manajerpro.activities.models.Board
import com.example.manajerpro.activities.models.User
import com.example.manajerpro.activities.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registeredUser(activity:SignUpActivity,userInfo: User){
        mFireStore.collection(com.example.manajerpro.activities.utils.Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener{
                activity.userRegisteredSuccess()
            }
    }

    fun getBoardDetails(activity: TaskListActivity,documentId: String){
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document->
                Log.e(activity.javaClass.simpleName,document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentId= document.id

                activity.boardDetails(board)
            }
            .addOnFailureListener{
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating Boards",e)
            }
    }

    fun createBoard(activity: CreateBoardActivity,board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Board created successfully")
                Toast.makeText(activity,"Board created successfully",Toast.LENGTH_LONG).show()

                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                Exception->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while Creating Board",
                    Exception)

            }
    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document->
                Log.i(activity.javaClass.simpleName,document.documents.toString())
                val boardList:ArrayList<Board> =ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId=i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
            }
            .addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating Boards")
            }
    }


    fun addUpdateTaskList(activity: Activity,board:Board){
        val taskListHashMap =HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST]= board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"TaskList Updated Successfully .")
                if(activity is TaskListActivity)
                     activity.addUpdateTaskListSuccess()
                else if(activity is CardDetailsActivity)
                    activity.addUpdateTaskListSuccess()
            }.addOnFailureListener {
                exception->
                if(activity is TaskListActivity)
                    activity.hideProgressDialog()
                else if(activity is CardDetailsActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board.",exception)

            }
    }

    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap:HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener{
                Log.i(activity.javaClass.simpleName,"Profile Data Updated")
                Toast.makeText(activity,"Profile Updated Successfully",Toast.LENGTH_LONG).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
                Toast.makeText(activity,"Error while Updating",Toast.LENGTH_LONG).show()

            }
    }
     fun loadUserData(activity: Activity,readBoardsList:Boolean=false){
        mFireStore.collection(com.example.manajerpro.activities.utils.Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener{document->
                val loggedInUser= document.toObject(User::class.java)!!
                when(activity){
                    is SignInActivity->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity->{
                        activity.updateNavigationUserDetails(loggedInUser,readBoardsList)
                    }
                    is MyProfileActivity->{
                        activity.setUserDataInUI(loggedInUser)
                    }
                }


            }
            .addOnFailureListener{
                e->
                when(activity){
                    is SignInActivity->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SignINUSer","Error writing document",e)
            }
    }

    fun getCurrentUserId():String{
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID

        /*var currentUser=  com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser!=null){
            currentUserID=currentUser.uid
        }
        return currentUserID
         */
    }

    fun getAssignedMembersListDetails(
        activity:Activity,assignedTo:ArrayList<String>
    ){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID,assignedTo)
            .get()
            .addOnSuccessListener{
                document->
                Log.e(activity.javaClass.simpleName,document.documents.toString())
                val usersList: ArrayList<User> =ArrayList()

                for(i in document.documents){
                    val user= i.toObject(User::class.java)!!
                    usersList.add(user)
                }
                if(activity is MembersActivity)
                    activity.setUpMembersList(usersList)
                else if(activity is TaskListActivity)
                    activity.boardMembersDetailsList(usersList)
            }
            .addOnFailureListener {
                e->
                if(activity is MembersActivity)
                    activity.hideProgressDialog()
                else if(activity is TaskListActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while treating a board.",e)
            }
    }

    fun getMemberDetails(activity: MembersActivity,email:String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                document->
                if(document.documents.size>0){
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.membersDetails(user)
                }
                else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No Such Member Found")
                }
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName
                    ,"Error while getting user details ",
                    e
                )
            }
    }

    fun assignMemberToBoard(activity: MembersActivity,board:Board,user:User){
        val assignedToHashMap = HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO]=board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.membersAssignSuccess(user)
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while Creating a board",e)
            }
    }

}
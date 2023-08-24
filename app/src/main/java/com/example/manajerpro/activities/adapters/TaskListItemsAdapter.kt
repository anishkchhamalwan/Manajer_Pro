package com.example.manajerpro.activities.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manajerpro.R
import com.example.manajerpro.activities.TaskListActivity
import com.example.manajerpro.activities.models.Task


open class TaskListItemsAdapter(private val context: Context,private var list: ArrayList<Task>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_task,parent,false)
        val layoutParams= LinearLayout.LayoutParams((parent.width * 0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp()).toPx(),0,(40.toDp()).toPx(),0)

        view.layoutParams= layoutParams
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){


            val tvAddTask = holder.itemView.findViewById<TextView>(R.id.tv_add_task_list)

            if(position == (list.size-1)){
                tvAddTask.visibility = View.VISIBLE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.INVISIBLE
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.INVISIBLE
               // val llTask=  holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item)
                //llTask.visibility= View.INVISIBLE
            }  else{
                tvAddTask.visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.VISIBLE
                //val llTask= holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item)
                //llTask.visibility= View.VISIBLE
            }

            val tvTaskList = holder.itemView.findViewById<TextView>(R.id.tv_task_list_title)
            tvTaskList.text= model.title
            val cvAddCard = holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name)

            tvAddTask.setOnClickListener {

                 tvAddTask.visibility = View.GONE
                 cvAddCard.visibility =View.VISIBLE

            }

           holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener {

                tvAddTask.visibility = View.VISIBLE
               // val cvAddTask=  holder.itemView.findViewById<CardView>(R.id.cv_add_card)
                cvAddCard.visibility= View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener {

                val listName= holder.itemView.findViewById<EditText>(R.id.et_task_list_name).text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }

                }else{
                    Toast.makeText(context,"Please Enter List Name.",Toast.LENGTH_LONG).show()
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_edit_list_name).setOnClickListener {
                holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).setText(model.title)
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.INVISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility=View.VISIBLE

            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view).setOnClickListener {

                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility= View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility =View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name).setOnClickListener {
                val listName = holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position,listName,model)
                    }
                }else{
                    Toast.makeText(context,"Please Enter a List Name.",Toast.LENGTH_LONG).show()
                }

            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list).setOnClickListener {
                alertDialogForDeleteList(position,model.title)
            }

            holder.itemView.findViewById<TextView>(R.id.tv_add_card).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility=View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility= View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_close_card_name).setOnClickListener {

                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility=View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility= View.GONE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_card_name).setOnClickListener {

                val cardName= holder.itemView.findViewById<EditText>(R.id.et_card_name).text.toString()

                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                      context.addCardToTaskList(position,cardName)

                    }

                }else{
                    Toast.makeText(context,"Please Enter Card Name.",Toast.LENGTH_LONG).show()
                }
            }
            val rv = holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list)
            rv.layoutManager = LinearLayoutManager(context)
            rv.setHasFixedSize(true)

            val adapter = CardListItemsAdapter(context,model.cards)
            rv.adapter= adapter

            adapter.setOnClickListener(
                object :CardListItemsAdapter.OnClickListener{
                    override fun onClick(cardPosition: Int) {
                        if(context is TaskListActivity){
                            context.cardDetails(position, cardPosition )
                        }
                    }
                }
            )
        }
    }

    private fun alertDialogForDeleteList(position: Int,title:String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){dialogInterface,which-> dialogInterface.dismiss()
        if(context is TaskListActivity){
            context.deleteTaskList(position)
        }
        }
        builder.setNegativeButton("No"){dialogInterface,which ->
            dialogInterface.dismiss()
        }
        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    private fun Int.toDp():Int =
        (this/ Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx():Int =
        (this* Resources.getSystem().displayMetrics.density).toInt()


    class MyViewHolder(view:View):RecyclerView.ViewHolder(view)
}
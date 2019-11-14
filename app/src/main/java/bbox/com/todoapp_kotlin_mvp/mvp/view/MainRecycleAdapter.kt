package bbox.com.todoapp_kotlin_mvp.mvp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import bbox.com.todoapp_kotlin_mvp.R
import bbox.com.todoapp_kotlin_mvp.models.Todo
import java.util.ArrayList

 class MainRecycleAdapter(val mContext:Context):RecyclerView.Adapter<MainRecycleAdapter.MyViewHolder>()
{
    //private  lateinit var mContext: Context
    private  var mTodoList: MutableList<Todo> = ArrayList()

    private  lateinit var mItemClickListener: onRecyclerViewItemClickListener

//region interface 1

    interface onRecyclerViewItemClickListener{
        fun onItemClickListener(view: View, position: Int, mBeanObj: Todo)
       //  fun onItemClickListener(view: View, position: Int, mBeanObj: Todo)
    }

    fun setOnItemClickListener(mItemClickListener: onRecyclerViewItemClickListener) {
        this.mItemClickListener = mItemClickListener
    }

//endregion


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) , View.OnClickListener{

        var mTxtVwName: TextView
        var mTxtVwExpDate:TextView
        var mTxtVwStatus:TextView
        var mTxtVwDesc:TextView
        var btnEdit: ImageView
         var chkBxComplete: CheckBox
        var cardVw: CardView


        init {

            cardVw = view.findViewById(R.id.card_view) as CardView

            mTxtVwName = view.findViewById(R.id.txtVw_Name) as TextView
            mTxtVwExpDate = view.findViewById(R.id.txtVw_expireDate) as TextView
            mTxtVwStatus = view.findViewById(R.id.txtVw_status) as TextView
            mTxtVwDesc = view.findViewById(R.id.txtVw_ExpDate) as TextView

            btnEdit = view.findViewById(R.id.btn_Edit) as ImageView
            chkBxComplete = view.findViewById(R.id.chkBox_todoComplete) as CheckBox

            cardVw.setOnClickListener(this)
            btnEdit.setOnClickListener(this)
            chkBxComplete.setOnClickListener(this)
        }



        override fun onClick(v: View) {
            if (mItemClickListener != null) {
                val _beanObj = mTodoList.get(adapterPosition)

                mItemClickListener.onItemClickListener(v, adapterPosition, _beanObj)
            }
        }

    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_recyler_celllayout, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mTodoList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val _beanObj :Todo = mTodoList[position]

        holder.mTxtVwName.setText(_beanObj.name)

        holder.mTxtVwExpDate.setText(_beanObj.expiry_date?.toString())

        holder.mTxtVwStatus.setText(_beanObj.status)
        holder.mTxtVwDesc.setText(_beanObj.description)

     /*   if (_beanObj.status.toLowerCase().equals("Completed".toLowerCase()))
            holder.chkBxComplete.isChecked = true
        else
            holder.chkBxComplete.isChecked = false*/

        holder.chkBxComplete.isChecked = if(_beanObj.status.toLowerCase().equals("Completed".toLowerCase())) true else false


    }

    fun setTodosInAdapter(todos: MutableList<Todo> )
    {
        this.mTodoList = todos
        notifyDataSetChanged()
    }


    fun notifyDeleted(position: Int) {
        mTodoList.removeAt(position)
        notifyItemRemoved(position)
    }


}
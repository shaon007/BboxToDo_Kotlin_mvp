package bbox.com.todoapp_kotlin_mvp.mvp.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import bbox.com.todoapp_kotlin_mvp.R
import bbox.com.todoapp_kotlin_mvp.models.Todo
import bbox.com.todoapp_kotlin_mvp.mvp.ToDoMvp
import bbox.com.todoapp_kotlin_mvp.mvp.view.MainRecycleAdapter.onRecyclerViewItemClickListener
import bbox.com.todoapp_kotlin_mvp.utils.VerticalSpacingItemDecorator
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject



class MainActivity : DaggerAppCompatActivity(), ToDoMvp.View {

    @set:Inject
     var presenter: ToDoMvp.Presenter? = null


      lateinit var rootView: ViewGroup
    lateinit var mRecycleVw: RecyclerView
    lateinit var  mAdapter: MainRecycleAdapter

    private var isSwipeDeleteable = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()

    }

    override fun onStart() {
        super.onStart()
        presenter?.setView(this)
        presenter?.loadData("", "", "")
    }

    override fun onStop() {
        super.onStop()
        presenter?.rxUnsubscribe()
    }


    private fun initialize() {
        rootView = findViewById(R.id.main_content) as ViewGroup

        mRecycleVw = findViewById(R.id.rclrVw) as RecyclerView

        val gridLayoutManager:GridLayoutManager = GridLayoutManager(this, 1)
        mRecycleVw.layoutManager = gridLayoutManager
        mRecycleVw.addItemDecoration(VerticalSpacingItemDecorator(10))

        mAdapter = MainRecycleAdapter(this)
        mRecycleVw.adapter = mAdapter


        mAdapter.setOnItemClickListener(object :
            onRecyclerViewItemClickListener {
          override  fun onItemClickListener(view: View, position: Int, mBeanObj: Todo) {

              //  Toast.makeText(this@MainActivity , "position: $position \n name:  ${mBeanObj.name} " ,Toast.LENGTH_SHORT).show()

              when (view.id) {
                  R.id.btn_Edit ->

                      presenter?.mainAdapterMenuClick(
                          "edit",
                          mBeanObj,
                          DetailActivity.IntentMode.TOVIEW.name,
                          true,
                          false
                      )

                  R.id.card_view -> {
                      Log.d("TEstIDLog", "act sw:: " + R.id.btn_Edit)
                      presenter?.mainAdapterMenuClick(
                          "view",
                          mBeanObj,
                          DetailActivity.IntentMode.TOVIEW.name,
                          false,
                          true
                      )
                  }

                  R.id.chkBox_todoComplete -> {
                      if ((view as CheckBox).isChecked)
                          mBeanObj.status= "Completed"
                      else
                          mBeanObj.status = "todo"

                      presenter?.updateTodo(Integer.valueOf(mBeanObj.id), mBeanObj)
                  }
              }
          }
        });



    }


    override fun updateData(todos: MutableList<Todo>) {
        mAdapter.setTodosInAdapter(todos)
    }

    override fun showSnackbar(msg: String) {
        Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun refreshAdapter() {
        mAdapter.notifyDataSetChanged()
    }




    //fab click
    fun addNewTodo(view: View) {
        presenter?.addNewTodo()
    }


   override fun getFilterDialog(filterDialog: AlertDialog) {
        filterDialog.show()
    }


    override fun attachSwipeToRecyclerVw(itemTouchHelper: ItemTouchHelper) {
        itemTouchHelper.attachToRecyclerView(mRecycleVw)
    }


    override fun notifyDelete(deletedPosition: Int) {
        mAdapter.notifyDeleted(deletedPosition)

    }

    override fun isSwipeDeletable(): Boolean {
        return isSwipeDeleteable
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_filter -> {
                isSwipeDeleteable = false
                presenter?.setUpFilterDialogPresenter()

                return true
            }

            R.id.menu_refresh -> {
                isSwipeDeleteable = true
                presenter?.loadData("", "", "")

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }


}

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import bbox.com.todoapp_kotlin_mvp.R
import bbox.com.todoapp_kotlin_mvp.models.Todo
import bbox.com.todoapp_kotlin_mvp.mvp.ToDoMvp
import bbox.com.todoapp_kotlin_mvp.mvp.view.DetailActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

public class MainPresenter : ToDoMvp.Presenter {

    private var view: ToDoMvp.View? = null
    private var model: ToDoMvp.Model? = null
    private var subscription: Disposable? = null

    internal var listTodo: MutableList<Todo> = ArrayList<Todo>()


    @Inject
    constructor(model: ToDoMvp.Model) {
        this.model = model
    }

    override
    fun loadData(filterDate: String, filterStatus: String, filterName: String) {
        listTodo.clear()
        subscription = model?.result(filterDate, filterStatus, filterName)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.concatMap { t -> Observable.fromIterable<Todo>(t).subscribeOn(Schedulers.io()) }
          /*  ?.concatMap(object : Function<MutableList<Todo>, ObservableSource<Todo>> {
                override fun apply(todos: MutableList<Todo>): ObservableSource<Todo> {
                    return Observable.fromIterable<Todo>(todos).subscribeOn(Schedulers.io())
                }
            })*/
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeWith(object : DisposableObserver<Todo>() {
                override fun onComplete() {
                    view?.updateData(listTodo)
                    swipeRecyclerDelete(listTodo)
                }

                override fun onError(e: Throwable) {
                    view?.showSnackbar("Error getting Todos")
                }

                override fun onNext(todo: Todo) {
                    listTodo.add(todo)
                }
            })


    }

    override fun rxUnsubscribe() {
               if (subscription != null) {
            if (!subscription?.isDisposed!!) {
                    subscription!!.dispose()
            }
        }

    }


    override fun setView(view: ToDoMvp.View) {
        this.view = view
    }

    override fun addNewTodo() {
          val intentMain = Intent(view as Context?, DetailActivity::class.java)

        intentMain.putExtra("intent_Main_mode", DetailActivity.IntentMode.TOADD.name)
        intentMain.putExtra("intent_Main_setEditModeEnabled", true)
        intentMain.putExtra("intent_Main_setMenuEnabled", false)
        (view as Context).startActivity(intentMain)


    }


    override fun mainAdapterMenuClick( optionChoosen: String,  todo: Todo,  strIntentMode: String,  editModeEnabled: Boolean,    menuEnabled: Boolean   )
    {
            val intentMain = Intent(view as Context?, DetailActivity::class.java)

        intentMain.putExtra("intent_Main_obj", todo)
        intentMain.putExtra("intent_Main_mode", DetailActivity.IntentMode.TOVIEW.name)
        intentMain.putExtra("intent_Main_setEditModeEnabled", editModeEnabled)
        intentMain.putExtra("intent_Main_setMenuEnabled", menuEnabled)

        (view as Context).startActivity(intentMain)

    }


    override fun updateTodo(id: Int, todo: Todo) {
        model?.updateTodoResult(id, todo)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeWith(object : DisposableObserver<Todo>() {
                override fun onComplete() {

                }

                override fun onError(e: Throwable) {
                        view?.showSnackbar("Error updating Todos")
                }

                override fun onNext(todo: Todo) {
                    view?.showSnackbar("Updated successfully")
                    view?.refreshAdapter()
                }
            })
    }


    override fun deleteTodo(id: Int, deletedPosition: Int) {
        val call = model?.deleteTodoResult(id)

        call?.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    view?.showSnackbar("Successfully deleted")
                    view?.notifyDelete(deletedPosition)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {

            }
        })
    }


    override fun setUpFilterDialogPresenter() {

        val mAlertBuilder = AlertDialog.Builder((view as Context?)!!)

        val li = LayoutInflater.from(view as Context?)
        val promptsView = li.inflate(R.layout.dialog_filter, null)

        mAlertBuilder.setPositiveButton("ok", null)
        mAlertBuilder.setNegativeButton("cancel", null)
        mAlertBuilder.setView(promptsView)

        val mRadioGroupDate = promptsView.findViewById(R.id.radioGroupDate) as RadioGroup
        val mRadioGroupStatus = promptsView.findViewById(R.id.radioGroupStatus) as RadioGroup
        val mEdTxtVwName = promptsView.findViewById(R.id.edDlName) as EditText

        val mAlertDialog = mAlertBuilder.create()

        mAlertDialog.setOnShowListener {
            val btnDialog_positive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnDialog_positive.setOnClickListener {
                val radioButtonId_date = mRadioGroupDate.checkedRadioButtonId
                val mRdBtn_Date = promptsView.findViewById(radioButtonId_date) as? RadioButton

                val radioButtonId_status = mRadioGroupStatus.checkedRadioButtonId
                val mRdBtn_status = promptsView.findViewById(radioButtonId_status) as? RadioButton

                var strdt = ""

                if (radioButtonId_date == R.id.rdBtnWeek)
                    strdt = getCalculatedDate(-7)
                else if (radioButtonId_date == R.id.rdBtnMonth)
                    strdt = getCalculatedDate(-30)
                else if (radioButtonId_date == R.id.rdBtn6Month)
                    strdt = getCalculatedDate(-180)


                var strStatus = ""
                if (mRdBtn_status != null)
                    strStatus = mRdBtn_status.text.toString()

                loadData(strdt, strStatus, mEdTxtVwName.text.toString())

                // mainViewModel.getToDosFromServer(strdt, strRd, mEdTxtVwName.getText().toString());

                mAlertDialog.dismiss()
            }
        }
        //  mAlertDialog.show();

        view!!.getFilterDialog(mAlertDialog)


    }


    fun getCalculatedDate(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, days)
        val newDate = calendar.time


        val s = SimpleDateFormat("yyyy-MM-dd")

        return s.format(newDate)
    }


    fun swipeRecyclerDelete(todos: List<Todo>) {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return if (!view!!.isSwipeDeletable()) false else true

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                try {
                    val position = viewHolder.adapterPosition
                    deleteTodo(todos[position].id, position)

                } catch (e: Exception) {
                    Log.e("MainActivity", e.message)
                }

            }


            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder( c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive )
                    .addSwipeRightBackgroundColor( ContextCompat.getColor( (view as Context?)!! ,    R.color.recycler_view_item_swipe_right_background   )  )
                    .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightLabel((view as Context).getString(R.string.action_delete))
                    .setSwipeRightLabelColor(Color.WHITE)
                    .create()
                    .decorate()
                super.onChildDraw( c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive )

            }
        }


        val itemTouchHelper = ItemTouchHelper(callback)
        view!!.attachSwipeToRecyclerVw(itemTouchHelper)
        //itemTouchHelper.attachToRecyclerView(mRecycleVw);
    }

}

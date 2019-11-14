package bbox.com.todoapp_kotlin_mvp.mvp

import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import bbox.com.todoapp_kotlin_mvp.models.Todo
import io.reactivex.Observable
import retrofit2.Call

interface ToDoMvp {

    interface View {
        fun updateData(todos: MutableList<Todo>)

        fun showSnackbar(s: String)

        fun refreshAdapter()

        fun notifyDelete(deletedPosition: Int)

        fun getFilterDialog(filterDialog: AlertDialog)

        fun attachSwipeToRecyclerVw(itemTouchHelper: ItemTouchHelper)

        fun isSwipeDeletable(): Boolean


    }

    interface Presenter {

        fun loadData(filterDate: String, filterStatus: String, filterName: String)

        fun rxUnsubscribe()

        fun setView(view: ToDoMvp.View)

        fun addNewTodo()

        fun mainAdapterMenuClick(   optionChoosen: String, todo: Todo, strIntentMode: String, editModeEnabled: Boolean,   menuEnabled: Boolean  )

        fun deleteTodo(id: Int, deletedPosition: Int)

        fun updateTodo(id: Int, todo: Todo)

        fun setUpFilterDialogPresenter()


    }

    interface Model {

        fun result(filterDate: String,  filterStatus: String,  filterName: String   ): Observable<MutableList<Todo>>

        fun addTodoResult(todo: Todo): Observable<Todo>

        fun updateTodoResult(id: Int, todo: Todo): Observable<Todo>

        fun deleteTodoResult(id: Int): Call<Void>

    }
}
package bbox.com.todoapp_kotlin_mvp.repostitory

import bbox.com.todoapp_kotlin_mvp.models.Todo
import io.reactivex.Observable
import retrofit2.Call

interface Repository {

    fun fetchToDosFromServer(
        filterDate: String,
        filterStatus: String,
        filterName: String
    ): Observable<MutableList<Todo>>

    fun postTodoToServer(todo: Todo): Observable<Todo>

    fun updateTodoInServer(id: Int, todo: Todo): Observable<Todo>

    fun deleteTodoInServer(id: Int): Call<Void>


}
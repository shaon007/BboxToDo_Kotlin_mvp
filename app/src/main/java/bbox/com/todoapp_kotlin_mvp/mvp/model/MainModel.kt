package bbox.com.todoapp_kotlin_mvp.mvp.model

import bbox.com.todoapp_kotlin_mvp.models.Todo
import bbox.com.todoapp_kotlin_mvp.mvp.ToDoMvp
import bbox.com.todoapp_kotlin_mvp.repostitory.Repository
import io.reactivex.Observable
import retrofit2.Call
import javax.inject.Inject

class MainModel @Inject
constructor(private val repository: Repository) : ToDoMvp.Model {

   override fun result( filterDate: String, filterStatus: String, filterName: String)   : Observable<MutableList<Todo>> {
        return repository.fetchToDosFromServer(filterDate, filterStatus, filterName)
    }


    override fun addTodoResult(todo: Todo): Observable<Todo> {
        return repository.postTodoToServer(todo)
    }


    override fun updateTodoResult(id: Int, todo: Todo): Observable<Todo> {
        return repository.updateTodoInServer(id, todo)
    }

    override  fun deleteTodoResult(id: Int): Call<Void> {
        return repository.deleteTodoInServer(id)
    }




}
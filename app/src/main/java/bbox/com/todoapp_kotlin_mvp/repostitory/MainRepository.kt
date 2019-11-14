package bbox.com.todoapp_kotlin_mvp.repostitory

import bbox.com.todoapp_kotlin_mvp.models.Todo
import bbox.com.todoapp_kotlin_mvp.network.MainApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import java.text.SimpleDateFormat
import javax.inject.Inject


class MainRepository @Inject constructor(val mainApi: MainApi, val dtFormatter:SimpleDateFormat): Repository
{
    override fun fetchToDosFromServer( filterDate: String,  filterStatus: String, filterName: String   )
            : Observable<MutableList<Todo>>
    {
        var filterDate = filterDate

        val returnedData = mainApi.getTodos().subscribeOn(Schedulers.io())


        if(!filterDate.isNullOrEmpty() || !filterStatus.isNullOrEmpty() || !filterName.isNullOrEmpty())
        {
            if(filterDate.isNullOrEmpty())
                { filterDate = "1800-01-01"}

            return filterToDos(filterDate, filterStatus, filterName, returnedData)

        }

        return returnedData


    }


    fun filterToDos( filterDate: String,  filterStatus: String,  filterName: String,  returnedDatafromAPi: Observable<MutableList<Todo>>
    ): Observable<MutableList<Todo>> {

        return returnedDatafromAPi
            .subscribeOn(Schedulers.io())
            .flatMap { todos -> Observable.fromIterable(todos).subscribeOn(Schedulers.io()) }
            .filter { todo ->
                val datenow = dtFormatter.parse(filterDate)
                val dateTodo = dtFormatter.parse(todo.expiry_date)

                dateTodo.after(datenow)
            }
            .filter { todo ->
                if (filterStatus.isNullOrEmpty()) true else todo.status.toLowerCase().equals(
                    filterStatus.toLowerCase()
                )
            }
            .filter { todo -> todo.name.toLowerCase().contains(filterName.toLowerCase()) }

            .toList()
            .toObservable()
    }


    override fun postTodoToServer(todo: Todo): Observable<Todo> {

        return mainApi.createTodos(todo).subscribeOn(Schedulers.io())
    }


    override fun updateTodoInServer(id: Int, todo: Todo): Observable<Todo> {

        return mainApi.updateTodos(id, todo).subscribeOn(Schedulers.io())
    }

    override fun deleteTodoInServer(id: Int): Call<Void> {

        return mainApi.deleteTodo(id)
    }


}

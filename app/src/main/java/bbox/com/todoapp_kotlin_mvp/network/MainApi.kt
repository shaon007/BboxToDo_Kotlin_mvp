package bbox.com.todoapp_kotlin_mvp.network

import bbox.com.todoapp_kotlin_mvp.models.Todo
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface MainApi {

    // get Todos
    @GET("todos.json")
   fun getTodos() : Observable<MutableList<Todo>>


    // new Todos
    @POST("todos.json")
    fun createTodos(@Body objTodo: Todo): Observable<Todo>

    // update Todos
    @PATCH("todos/{id}.json")
    fun updateTodos(@Path("id") id: Int, @Body objTodo: Todo): Observable<Todo>

    // delete Todos
    @DELETE("todos/{id}.json")
    fun deleteTodo(@Path("id") id: Int): Call<Void>


}
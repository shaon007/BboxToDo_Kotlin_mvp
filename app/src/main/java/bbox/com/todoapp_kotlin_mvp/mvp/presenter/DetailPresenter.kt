package bbox.com.todoapp_kotlin_mvp.mvp.presenter

import android.content.Context
import android.view.View
import android.widget.Toast
import bbox.com.todoapp_kotlin_mvp.models.Todo
import bbox.com.todoapp_kotlin_mvp.mvp.DetailMvp
import bbox.com.todoapp_kotlin_mvp.mvp.ToDoMvp
import bbox.com.todoapp_kotlin_mvp.mvp.view.DetailActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class DetailPresenter  : DetailMvp.Presenter  {

    @Inject
    constructor(model:ToDoMvp.Model)
    {
        this.model = model
    }

    private var view: DetailMvp.View? = null
    private var model: ToDoMvp.Model?=null

    private var subscription: Disposable ?=null


    override fun setView(view: DetailMvp.View) {
       this.view = view
    }

    override fun saveTodo(id: Int, todo: Todo, strIntentMode: String?) {
        if(todo.name.isNullOrEmpty() || todo.status.isNullOrEmpty() || todo.description.isNullOrEmpty() || todo.expiry_date.isNullOrEmpty())
            view?.showSnackbar("Please enter all values!")

        else{
            if (strIntentMode == DetailActivity.IntentMode.TOADD.name)
            {
                subscription = model?.addTodoResult(todo)
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribeWith(object : DisposableObserver<Todo>() {
                        override fun onComplete() {

                        }

                        override fun onError(e: Throwable) {
                            view?.showSnackbar("Error adding Todos")
                        }

                        override fun onNext(todo: Todo) {
                            view?.updateData(todo)
                            view?.showSnackbar("Added successfully")
                        }
                    })!!


                view?.setViewMode(DetailActivity.IntentMode.TOVIEW.name)
                view?.setSaveButtonVisibility(View.GONE)
            }
            else if(strIntentMode == DetailActivity.IntentMode.TOVIEW.name)
            {
                subscription = model?.updateTodoResult(id, todo)
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribeWith(object : DisposableObserver<Todo>() {
                        override fun onComplete() {

                        }

                        override fun onError(e: Throwable) {
                            if (view != null)
                                view?.showSnackbar("Error updating Todos")
                        }

                        override fun onNext(todo: Todo) {
                            view?.updateData(todo)
                            view?.showSnackbar("Updated successfully")
                        }
                    })!!

                view?.setSaveButtonVisibility(View.GONE)
            }

            view?.setMenuEnabled(true)
        }
    }

    override fun deleteTodo(id: Int) {
        val call = model?.deleteTodoResult(id)

        call?.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful)
                    Toast.makeText(view as Context, "Delete Success", Toast.LENGTH_SHORT).show()
                view?.navigateToMain()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {

            }
        })
    }

    override fun rxUnsubscribe() {
        if (subscription != null) {
            if (!subscription?.isDisposed!!)
                {     subscription!!.dispose() }
        }
    }



}
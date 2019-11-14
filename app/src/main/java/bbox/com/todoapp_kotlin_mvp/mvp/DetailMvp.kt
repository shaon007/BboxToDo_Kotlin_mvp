package bbox.com.todoapp_kotlin_mvp.mvp

import bbox.com.todoapp_kotlin_mvp.models.Todo

interface DetailMvp {

    interface View {

        fun updateData(todos: Todo)

        fun showSnackbar(s: String)

        fun setViewMode(s: String)
        fun setSaveButtonVisibility(viewMode: Int)
        fun setMenuEnabled(boln: Boolean)

        fun navigateToMain()

    }


    interface Presenter {
        fun setView(view: View)


        fun saveTodo(id: Int, todo: Todo, strIntentMode: String?)

        fun deleteTodo(id: Int)

        fun rxUnsubscribe()

    }

}
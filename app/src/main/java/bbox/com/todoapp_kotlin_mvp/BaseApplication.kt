package bbox.com.todoapp_kotlin_mvp


import bbox.com.todoapp_kotlin_mvp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class BaseApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().application(this).build()
        // return null;
    }
}
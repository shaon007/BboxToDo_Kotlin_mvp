package bbox.com.todoapp_kotlin_mvp.di

import bbox.com.todoapp_kotlin_mvp.mvp.view.DetailActivity
import bbox.com.todoapp_kotlin_mvp.mvp.view.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {


    @ContributesAndroidInjector()
        abstract fun contributeMainActivity(): MainActivity



    @ContributesAndroidInjector()
            abstract fun contributeDetailActivity(): DetailActivity


}
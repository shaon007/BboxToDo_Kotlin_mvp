package bbox.com.todoapp_kotlin_mvp.di

import MainPresenter
import bbox.com.todoapp_kotlin_mvp.mvp.DetailMvp
import bbox.com.todoapp_kotlin_mvp.mvp.ToDoMvp
import bbox.com.todoapp_kotlin_mvp.mvp.model.MainModel
import bbox.com.todoapp_kotlin_mvp.mvp.presenter.DetailPresenter
import bbox.com.todoapp_kotlin_mvp.network.MainApi
import bbox.com.todoapp_kotlin_mvp.repostitory.MainRepository
import bbox.com.todoapp_kotlin_mvp.repostitory.Repository
import bbox.com.todoapp_kotlin_mvp.utils.Constants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideRetrofitInstance(): Retrofit {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }


    @Singleton
    @Provides
     fun provideMainApi(retrofit: Retrofit): MainApi {
        return retrofit.create<MainApi>(MainApi::class.java!!)
    }


   /* @Singleton
    @Provides
     fun provideString(): String {
        return "Hello there"
    }
*/

    @Provides
    fun provideRepository(mainApi: MainApi): Repository {
        return MainRepository(mainApi, SimpleDateFormat("yyyy-MM-dd"))
    }

    @Provides
    fun provideMainPresenter(mainModel: ToDoMvp.Model): ToDoMvp.Presenter {
        return MainPresenter(mainModel)
    }

    @Provides
    fun provideMainModel(repository: Repository): ToDoMvp.Model {
        return MainModel(repository)
    }


//-------------- for Details MVP

    @Provides
    fun provideDetailPresenter(mainModel: ToDoMvp.Model): DetailMvp.Presenter {
        return DetailPresenter(mainModel)
    }



}
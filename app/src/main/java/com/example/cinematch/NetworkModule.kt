package com.example.cinematch

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesInterceptor(@ApplicationContext context: Context) : Interceptor {
        return authInterceptor(context)
    }

    @Provides
    @Singleton
    fun okhttpclient(authInterceptor: Interceptor) : OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(authInterceptor).build()

    }

    @Provides
    @Singleton
    fun retrofitinstance(okHttpClient: OkHttpClient) : Apiserviceinterface {
        return Retrofit.Builder()
            .baseUrl(Constants.base_url_apicall)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(Apiserviceinterface::class.java)
    }

    // Retrofit setup



    @Provides
    @Singleton
    fun getfirebaseinstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()

    }
     @Provides
     @Singleton
     fun provideRepository(api : Apiserviceinterface) : Repository {
         return Repository(api)
     }

    @Provides
    @Singleton
    fun provideOpenRouterService(): OpenRouterService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openrouter.ai/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(OpenRouterService::class.java)
    }

    @Provides
    @Singleton
    fun provideAIRepository(apiService: OpenRouterService): AIRepository {
        return AIRepository(apiService)
    }

}
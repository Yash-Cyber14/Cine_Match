package com.example.cinematch

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class authInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = TokenManager.gettoken(context = context )
        val newrequest = chain.request().newBuilder()

        if(token != null){
            newrequest.addHeader(name = "Autherisation" ,value = "Bearer $token")
        }

        return chain.proceed(newrequest.build())

    }


}
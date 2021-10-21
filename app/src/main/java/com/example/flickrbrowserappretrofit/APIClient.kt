package com.example.flickrbrowserappretrofit

import android.util.Log
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient {
    private var retrofit : Retrofit? = null

    fun getClient(): Retrofit?{
        /*val gson= GsonBuilder()
            .setLenient()
            .create()*/
        retrofit = Retrofit.Builder()
            .baseUrl("https://www.flickr.com/services/rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        Log.d("MyData","https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=60877df427a4d42fe3c1c257e98f9b2e&tags=${Data.search}&per_page=${Data.count}&format=json&nojsoncallback=2")
        return retrofit
    }
}
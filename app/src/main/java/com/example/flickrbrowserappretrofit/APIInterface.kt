package com.example.flickrbrowserappretrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface APIInterface {
    @GET("?method=flickr.photos.search&api_key=60877df427a4d42fe3c1c257e98f9b2e&tags=&per_page=&format=json&nojsoncallback=2")
    fun getInformation(@Query("tags")tags: String, @Query("per_page")per_page: String): Call<Photos>

}
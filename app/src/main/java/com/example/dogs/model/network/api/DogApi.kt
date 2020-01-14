package com.example.dogs.model.network.api

import com.example.dogs.model.entities.DogBreed
import io.reactivex.Single
import retrofit2.http.GET

interface DogApi {

    @GET("DevTides/DogsApi/master/dogs.json")
    fun getDogs(): Single<List<DogBreed>>
}
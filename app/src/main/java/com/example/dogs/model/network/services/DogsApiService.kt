package com.example.dogs.model.network.services

import com.example.dogs.BuildConfig
import com.example.dogs.model.network.api.DogApi
import com.example.dogs.model.entities.DogBreed
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class DogsApiService {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    private val api by lazy {
        retrofit.create(DogApi::class.java)
    }

    fun getDogs(): Single<List<DogBreed>> {
        return api.getDogs()
    }
}
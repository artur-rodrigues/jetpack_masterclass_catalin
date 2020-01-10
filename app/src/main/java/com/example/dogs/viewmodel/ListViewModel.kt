package com.example.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogs.model.DogBreed

class ListViewModel: ViewModel() {

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        val newList = listOf(
            createDogMock(1),
            createDogMock(2),
            createDogMock(3)
        )

        dogs.value = newList
        dogsLoadError.value = false
        loading.value = false
    }

    // Vai ser retirado
    private fun createDogMock(num: Int): DogBreed {
        return DogBreed(
            num.toString(),
            "DogBreed $num",
            "$num years",
            "BreedGroup $num",
            "BreedFor $num",
            "Temperament $num",
            "Url $num"
        )
    }
}
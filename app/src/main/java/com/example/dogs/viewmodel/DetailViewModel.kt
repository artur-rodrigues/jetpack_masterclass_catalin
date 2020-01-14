package com.example.dogs.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.dogs.model.entities.DogBreed
import com.example.dogs.model.room.DogDatabase
import kotlinx.coroutines.launch

class DetailViewModel(application: Application): BaseViewModel(application) {

    val dogLiveData = MutableLiveData<DogBreed>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun fetch(uuid: Int) {
        launch {
            dogLiveData.value = DogDatabase(getApplication()).dogDao().getDog(uuid)
        }
    }
}
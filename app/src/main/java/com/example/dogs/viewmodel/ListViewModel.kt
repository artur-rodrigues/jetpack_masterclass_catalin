package com.example.dogs.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogs.R
import com.example.dogs.model.entities.DogBreed
import com.example.dogs.model.network.services.DogsApiService
import com.example.dogs.model.room.DogDatabase
import com.example.dogs.utils.NotificationsHelper
import com.example.dogs.utils.SharedPreferencesHelper
import com.example.dogs.utils.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesHelper(getApplication())
    private val dogsService = DogsApiService()
    private val disposable = CompositeDisposable()

    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        val updateTime = prefHelper.getUpdateTime()
        if (updateTime != null && updateTime != 0L &&
            System.nanoTime() - updateTime < refreshTime) {
            fetchFromDataBase()
        } else {
            fetchFromRemote()
        }
    }

    fun refreshBypassCache() {
        fetchFromRemote()
    }

    private fun fetchFromRemote() {
        loading.value = true
        disposable.addAll(subscribeFetchDogs())
    }

    private fun fetchFromDataBase() {
        launch {
            val application: Application = getApplication()
            val dogs = DogDatabase(application).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            application.toast(R.string.dogs_retrieved)
        }
    }

    private fun subscribeFetchDogs(): Disposable {
        return dogsService.getDogs()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>() {

                override fun onSuccess(list: List<DogBreed>) {
                    storeDogsLocally(list)
                    getApplication<Application>().toast("Dogs retrivied from endpoin")
                    NotificationsHelper(getApplication()).createNotification()
                }

                override fun onError(e: Throwable) {
                    dogsLoadError.value = true
                    loading.value = false
                    e.printStackTrace()
                }
            })
    }

    private fun dogsRetrieved(list: List<DogBreed>) {
        dogs.value = list
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(list: List<DogBreed>) {
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(*list.toTypedArray())

            for(i in 0 until (list.size - 1)) {
                list[i].uuid = result[i].toInt()
            }

            dogsRetrieved(list)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
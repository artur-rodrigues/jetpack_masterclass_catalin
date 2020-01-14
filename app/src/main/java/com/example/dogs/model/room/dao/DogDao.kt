package com.example.dogs.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dogs.model.entities.DogBreed

@Dao
interface DogDao {

    @Insert
    suspend fun insertAll(vararg dogs: DogBreed): List<Long>

    @Query(value = "SELECT * FROM dog_breed")
    suspend fun getAllDogs(): List<DogBreed>

    @Query(value = "SELECT * FROM dog_breed WHERE uuid = :uuid")
    suspend fun getDog(uuid: Int): DogBreed

    @Query(value = "DELETE FROM dog_breed")
    suspend fun deleteAllDogs()
}
package com.example.dogs.model

sealed class Result {
    class Success<T>(result: T): Result()
    class Loading: Result()
    class Error(error: Throwable): Result()
}
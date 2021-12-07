package com.bdjobs.app.utilities

//
// Created by Soumik on 11/8/2021.
// piyal.developer@gmail.com
//

data class Resource<T> (val status: Status,val data:T?=null,val message:String?=null){
    companion object {
        fun <T> success(data:T?):Resource<T>{
            return Resource(status = Status.SUCCESS,data = data)
        }
        fun <T> error(message: String?):Resource<T>{
            return Resource(status = Status.ERROR,message = message)
        }
        fun <T> loading():Resource<T>{
            return Resource(status = Status.LOADING)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
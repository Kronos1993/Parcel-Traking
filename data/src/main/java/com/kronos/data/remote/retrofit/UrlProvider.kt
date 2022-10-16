package com.kronos.data.remote.retrofit

interface UrlProvider {
    fun getApiUrl():String
    fun getServerUrl():String
}
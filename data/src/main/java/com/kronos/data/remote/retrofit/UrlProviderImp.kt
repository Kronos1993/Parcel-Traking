package com.kronos.data.remote.retrofit

import javax.inject.Inject

class UrlProviderImp @Inject constructor(
) : UrlProvider {
    override fun getApiUrl(): String {
        return UrlConstants.BASE_URL
    }

    override fun getServerUrl(): String {
        return UrlConstants.SERVER_URL
    }
}